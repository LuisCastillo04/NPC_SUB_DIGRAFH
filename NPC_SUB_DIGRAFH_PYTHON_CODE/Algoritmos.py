import networkx as nx
from itertools import combinations

def BipartiteSubsetBF(G : nx.Graph, k : int) -> bool:

  G1 = nx.Graph()
  G1.add_nodes_from(G.nodes) #O(V)
  if k < 0:
    raise ValueError("k debe ser mayor o igual a 0")
  if k > len(G.edges()):
    raise ValueError(f"k debe ser menor o igual al numero de aristas del grafo ({len(G.edges())})")
  if  k <= 2:
    return True
  for i in combinations(G.edges(), k): #Se repite maximo E (Ningun posible subgrafo de ese tamaño)
  #Peor Caso k = |E|/2
    G1.add_edges_from(i) # O(E)
    if nx.is_bipartite(G1): # O(V + E)
        return True, G1.edges
    else:
        G1.remove_edges_from(i)  #O(E)
  return False

def BipartiteSubsetDC(G : nx.Graph, k : int) -> bool:

    if k < 0:
      raise ValueError("k debe ser mayor o igual a 0")
    if k > len(G.edges()):
        raise ValueError(f"k debe ser menor o igual al numero de aristas del grafo ({len(G.edges())})")
    __G = nx.Graph()
    __G.add_nodes_from(G.nodes)
    if k <= 2:
        return True
    def __BipartiteSubsetDC(_G : nx.Graph, k_left : int,edges = list) -> bool:
        if k_left == 0 :
            return True
        if len(edges)< k_left:
            return False
        G1 = _G.copy()
        G2 = _G.copy()
        G2.add_edge(edges[0][0],edges[0][1])
        a = __BipartiteSubsetDC(G1,k_left,edges[1:])
        b = __BipartiteSubsetDC(G2,k_left-1,edges[1:]) if nx.is_bipartite(G2) else False
        if b:
            return True
        if a:
            return True

    return __BipartiteSubsetDC(__G,k,list(G.edges()))

# BackTrackin
def BipartiteSubsetBT(G : nx.Graph, k : int) -> bool:

  if k < 0:
      raise ValueError("k debe ser mayor o igual a 0")
  if k > len(G.edges()):
      raise ValueError(f"k debe ser menor o igual al numero de aristas del grafo ({len(G.edges())})")
  if k <= 2 :
      return True

  def __BipartiteSubsetBT(_G : nx.Graph, k_pass : int =  0, init = 0) -> bool:

      if k == k_pass:
        return True
        
      if k - k_pass > len(edges_list) - init  : #Faltan Aristas
          return False
      
      for i in range(init, len(edges_list)):
          u,v = edges_list[i]
          _G.add_edge(u,v) # +1
          if nx.is_bipartite(_G): # O (V + E)
              if __BipartiteSubsetBT(_G, k_pass + 1, i + 1):
                  return True
          _G.remove_edge(u,v) #+1
      return False

  G1 = nx.Graph()
  G1.add_nodes_from(G.nodes) #O (V)
  return __BipartiteSubsetBT(G1)
