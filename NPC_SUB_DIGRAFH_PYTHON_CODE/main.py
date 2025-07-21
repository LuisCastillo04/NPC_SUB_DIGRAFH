# main.py
import random as rd
import pandas as pd
import multiprocessing as mp        # ← añadido para el timeout
import Timer
from Algoritmos import *
import networkx as nx
from itertools import combinations

TIMEOUT = 5      # segundos máximos permitidos por ejecución


def _worker(pipe, func, args, kwargs):
        cron = Timer.timer()
        cron.start()
        try:
            func(*args, **kwargs)
            pipe.send(cron.stop())       # enviamos tiempo empleado
        except Exception as e:
            pipe.send(e)                 # propagamos excepción
        finally:
            pipe.close()

# ---------------------------------------------------------------------
def run_with_timeout(func, args=(), kwargs=None, limit=TIMEOUT):
    """
    Ejecuta func(*args, **kwargs) en un subproceso.
    Devuelve el tiempo (float) cronometrado con Timer.timer()
    o None si se alcanza el límite de tiempo.
    """
    if kwargs is None:
        kwargs = {}

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
        f.write('vertices;aristas;n;k;tiempo BF;tiempo DC;tiempo BT\n')   # cabecera

        for _ in range(10):
            G = nx.Graph()
            nodes = range(1, rd.randint(2, 10))
            G.add_nodes_from(nodes)
            k = rd.randint(0, nodes[-1] * (nodes[-1] - 1) // 2)
            k1 = rd.randint(0, k)
            G.add_edges_from(add_random_edges(nodes, k))
            f.write(f'[1...{nodes[-1]}];{G.edges};{k};{k1}')
            for method in methods:
                ti = 0
                for _ in range(5):
                    elapsed = run_with_timeout(method, args=(G, k1))
                    if elapsed is None:      # se produjo TIMEOUT
                        ti = 'TIMEOUT'
                        break
                    ti += elapsed
                if ti != 'TIMEOUT':
                    ti = ti / 5
                f.write(f';{ti}')
                f.write('\n')    

def main():
    tests()

if __name__ == '__main__':
    mp.freeze_support()   # necesario en Windows; inofensivo en otros SO
    main()
