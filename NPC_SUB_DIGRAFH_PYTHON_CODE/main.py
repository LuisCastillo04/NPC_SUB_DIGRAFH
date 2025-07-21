# main.py
import random as rd
import pandas as pd
import multiprocessing as mp        # ← añadido para el timeout
import Timer
from Algoritmos import *
import networkx as nx
from itertools import combinations

# ---------------------------------------------------------------------
TIMEOUT   = 15     # s máximos por llamada
SEED_BASE = 42    # cambia este valor para otra serie reproducible
N_TESTS   = 10    # nº de instancias que se generarán
REPS      = 5     # nº de repeticiones por algoritmo
# ---------------------------------------------------------------------

def _worker(pipe, func, args, kwargs):
    """Subproceso que cronometra y devuelve (tiempo, retorno)."""
    cron = Timer.timer(); cron.start()
    try:
        ret = func(*args, **(kwargs or {}))   # ← (bool, ops)
        pipe.send((cron.stop(), ret))         # enviamos tupla
    except Exception as e:
        pipe.send(e)                          # propagamos excepción
    finally:
        pipe.close()

# ---------------------------------------------------------------------
def run_with_timeout(func, args=(), kwargs=None, limit=TIMEOUT):
    """
    Ejecuta func(*args, **kwargs) en un subproceso.
    Devuelve el tiempo (float) cronometrado con Timer.timer()
    o None si se alcanza el límite de tiempo.
    """
    parent_conn, child_conn = mp.Pipe(duplex=False)
    p = mp.Process(target=_worker, args=(child_conn, func, args, kwargs))
    p.start()
    p.join(limit)

    if p.is_alive():                     # ⏰ se agotó el tiempo
        p.terminate()
        p.join()
        return None

    result = parent_conn.recv()
    if isinstance(result, Exception):
        raise result                     # relanza excepción del hijo
    return result                        # tiempo en segundos
# ---------------------------------------------------------------------

def add_random_edges(nodes: list, num_edges: int):
    posibles = list(combinations(nodes, 2))
    return rd.sample(posibles, num_edges)

def tests():
    t = Timer.timer()                   # ← se mantiene, usado para cabecera
    methods = [BipartiteSubsetBF, BipartiteSubsetDC, BipartiteSubsetBT]

    with open('results.csv', 'w') as f:
        f.write('seed;vertices;aristas;n;k;'
                  'tiempo BF;ops BF;'
                  'tiempo DC;ops DC;'
                  'tiempo BT;ops BT\n')   # cabecera

        rd.seed(SEED_BASE)
        for t_id  in range(N_TESTS):
            seed = SEED_BASE + t_id                # semilla por caso
            rd.seed(seed)

            # -------- generar grafo aleatorio reproducible ------------
            G = nx.Graph()
            nodes = range(1, rd.randint(2, 10))
            G.add_nodes_from(nodes)

            k = rd.randint(0, nodes[-1] * (nodes[-1] - 1) // 2)
            k1 = rd.randint(0, k)
            G.add_edges_from(add_random_edges(nodes, k))


            fila = [
                str(seed),
                f'[1...{nodes[-1]}]',         # vértices
                str(list(G.edges())),          # aristas
                str(k),
                str(k1)
            ]

            # ------------- medir cada algoritmo -----------------------
            for method in methods:
                ti_acum, op_acum = 0.0, 0
                timeout = False

                for _ in range(REPS):
                    res = run_with_timeout(method, args=(G, k1))
                    if res is None:            # se produjo TIMEOUT
                        timeout = True
                        break

                    elapsed, ret = res 
                    if isinstance(ret, tuple) and len(ret) == 2 and isinstance(ret[1], int):
                        _, ops = ret                # forma correcta (bool, ops)
                    else:
                        ops = 0                     # no hay contador válido

                    ti_acum += elapsed
                    op_acum += ops

                if timeout:
                    fila.extend(['TIMEOUT', 'TIMEOUT'])
                else:
                    fila.extend([f'{ti_acum/REPS:.6f}', str(op_acum // REPS)])

            f.write(';'.join(fila) + '\n')

def main():
    tests()

if __name__ == '__main__':
    mp.freeze_support()   # necesario en Windows; inofensivo en otros SO
    main()
