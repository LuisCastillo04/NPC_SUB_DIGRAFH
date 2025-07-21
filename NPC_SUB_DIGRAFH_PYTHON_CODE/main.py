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

def _worker(pipe, func, shared_ops, args, kwargs):
    """Subproceso que cronometra y devuelve (tiempo, retorno)."""
    counter = OpCounter(shared_ops)
    kwargs = kwargs or {}
    kwargs['counter'] = counter             # ← se pasa al algoritmo

    cron = Timer.timer(); cron.start()
    try:
        ret = func(*args, **kwargs)         # (bool, ops)  o  bool
        pipe.send((cron.stop(), ret))
    except Exception as e:
        pipe.send(e)
    finally:
        pipe.close()

# ---------------------------------------------------------------------
def run_with_timeout(func, args=(), kwargs=None, limit=TIMEOUT):
    """
    Ejecuta func(*args, **kwargs) en un subproceso.
    Devuelve el tiempo (float) cronometrado con Timer.timer()
    o None si se alcanza el límite de tiempo.
    """
    shared_ops = mp.Value('i', 0)           # ← valor compartido


    parent, child = mp.Pipe(duplex=False)
    p = mp.Process(target=_worker,
                   args=(child, func, shared_ops, args, kwargs))
    p.start(); p.join(limit)

     # ----- se agotó el tiempo -----------------------------------------
    if p.is_alive():
        ops_so_far = shared_ops.value       # ← lo que llevamos contado
        p.terminate(); p.join()
        return ("TIMEOUT", ops_so_far)

    # ----- terminó a tiempo -------------------------------------------
    result = parent.recv()
    if isinstance(result, Exception):
        raise result
    return result                             # tiempo en segundos
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

                    
                    if isinstance(res, tuple) and res[0] == "TIMEOUT":
                        timeout = True
                        op_acum += res[1]      # sumamos las ops que llevaba
                        break

                    elapsed, ret = res 
                    ops = (
                        ret[1]
                        if isinstance(ret, tuple) and isinstance(ret[1], int)
                        else 0
                    )
                    ti_acum += elapsed
                    op_acum += ops


                if timeout:
                    fila.extend(['TIMEOUT', str(op_acum)])   # tiempo=TIMEOUT, ops reales
                else:
                    fila.extend([f'{ti_acum/REPS:.6f}', str(op_acum // REPS)])

            f.write(';'.join(fila) + '\n')

def main():
    tests()

if __name__ == '__main__':
    mp.freeze_support()   # necesario en Windows; inofensivo en otros SO
    main()
