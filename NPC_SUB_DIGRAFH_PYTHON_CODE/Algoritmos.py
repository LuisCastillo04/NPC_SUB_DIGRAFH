# Algoritmos.py
import networkx as nx
from itertools import combinations


# ------------FUNCION ENCARGADA DE CONTAR OPERACIONES----------------
class OpCounter:
    """Contador de operaciones (local o compartido)."""
    def __init__(self, shared = None):
        # Si no se pasa valor compartido usamos un int normal
        if shared is None:
            self._local = 0
            self._shared = None
        else:
            self._local = None
            self._shared = shared            # mp.Value('i', 0)

    def inc(self, n: int = 1) -> None:
        if self._shared is None:
            self._local += n
        else:                               # acceso con lock
            with self._shared.get_lock():
                self._shared.value += n

    @property
    def ops(self) -> int:
        return self._local if self._shared is None else self._shared.value

# ----------------------------------------------------------------------
# 1. Fuerza bruta
# ----------------------------------------------------------------------
def BipartiteSubsetBF(G : nx.Graph, k : int, counter: OpCounter | None = None) -> tuple[bool, int]:
    counter = counter or OpCounter()


    G1 = nx.Graph(); counter.inc()
    G1.add_nodes_from(G.nodes); counter.inc(len(G))  #O(V)
    if k < 0:
        raise ValueError("k debe ser mayor o igual a 0")
    
    if k > len(G.edges()):
        raise ValueError(f"k debe ser menor o igual al numero de aristas del grafo ({len(G.edges())})")
    
    if  k <= 2:
        counter.inc()
        return True , counter.ops


    for i in combinations(G.edges(), k): #Se repite maximo E (Ningun posible subgrafo de ese tamaño)
    #Peor Caso k = |E|/2
        G1.add_edges_from(i)
        counter.inc(len(i)) # O(E) 

        counter.inc()
        if nx.is_bipartite(G1): # O(V + E)
            return True, counter.ops
        
        else:
            G1.remove_edges_from(i)
            counter.inc(len(i))  #O(E)
    return False, counter.ops

# ----------------------------------------------------------------------
# 2. Divide & Conquer
# ----------------------------------------------------------------------
def BipartiteSubsetDC(G : nx.Graph, k : int,counter: OpCounter | None = None) -> tuple[bool, int]:
    counter = counter or OpCounter()

    if k < 0:
      raise ValueError("k debe ser mayor o igual a 0")
    if k > len(G.edges()):
        raise ValueError(f"k debe ser menor o igual al numero de aristas del grafo ({len(G.edges())})")
    __G = nx.Graph()
    __G.add_nodes_from(G.nodes)
    if k <= 2:
        return True, counter.ops
    
    def __BipartiteSubsetDC(_G : nx.Graph, k_left : int,edges = list) -> bool:
        if k_left == 0 :
            return True
        if len(edges)< k_left:
            return False
        
        G1 = _G.copy(); counter.inc()
        G2 = _G.copy(); counter.inc()

        G2.add_edge(edges[0][0],edges[0][1]); counter.inc()

        counter.inc()
        a = __BipartiteSubsetDC(G1,k_left,edges[1:])
        counter.inc()
        b = __BipartiteSubsetDC(G2,k_left-1,edges[1:]) if nx.is_bipartite(G2) else False
        
        if b:
            return True
        if a:
            return True

    return __BipartiteSubsetDC(__G,k,list(G.edges())), counter.ops


# ----------------------------------------------------------------------
# 3. Backtracking
# ----------------------------------------------------------------------
def BipartiteSubsetBT(G : nx.Graph, k : int,counter: OpCounter | None = None) -> tuple[bool, int]:
    counter = counter or OpCounter()

    if k < 0:
        raise ValueError("k debe ser mayor o igual a 0")
    if k > len(G.edges()):
        raise ValueError(f"k debe ser menor o igual al numero de aristas del grafo ({len(G.edges())})")
    if k <= 2 :
        return True, counter.ops
    
    edges_list = list(G.edges())

    def __BipartiteSubsetBT(_G : nx.Graph, k_pass : int =  0, init = 0) -> bool:

        if k == k_pass:
            return True
            
        if k - k_pass > len(edges_list) - init  : #Faltan Aristas
            return False
        
        for i in range(init, len(edges_list)):
            u,v = edges_list[i]
            _G.add_edge(u,v) ; counter.inc()# +1
            if nx.is_bipartite(_G): # O (V + E)
                counter.inc()                 # cuenta la llamada
                if __BipartiteSubsetBT(_G, k_pass + 1, i + 1):
                    return True
            _G.remove_edge(u,v); counter.inc() #+1
        return False

    G1 = nx.Graph()
    G1.add_nodes_from(G.nodes); counter.inc(len(G)) #O (V)
    edge_list = list(G.edges())
    return __BipartiteSubsetBT(G1),counter.ops
