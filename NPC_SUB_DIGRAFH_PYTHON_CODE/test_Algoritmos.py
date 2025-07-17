import pytest
import networkx as nx
from Algoritmos import *

def OutBounds(G):

    with pytest.raises(ValueError):
        BipartiteSubsetBF(G, -1)
    with pytest.raises(ValueError):    
        BipartiteSubsetBT(G, -1)
    with pytest.raises(ValueError): 
        BipartiteSubsetDC(G, -1)
    for i in range(1, 4):
        with pytest.raises(ValueError): 
            BipartiteSubsetBF(G, len(G.edges())+i)
        with pytest.raises(ValueError):     
            BipartiteSubsetBT(G, len(G.edges())+i)
        with pytest.raises(ValueError): 
            BipartiteSubsetDC(G, len(G.edges())+i)

def test_DisconnectedGraph():

    G = nx.Graph()
    G.add_nodes_from([1, 2, 3, 4, 5, 6, 7, 8])
    G.add_edges_from([(1, 2), (3, 4), (5, 6), (7, 8)])
    ValueBipartite(G, 4)

def ValueBipartite(G, k, value = True):
    
    assert BipartiteSubsetBF(G, k) == value
    assert BipartiteSubsetDC(G, k) == value
    assert BipartiteSubsetBT(G, k) == value

def test_BipartiteSubgraphVoid():
    G = nx.Graph()
    ValueBipartite(G, 0)
    OutBounds(G)

def test_BipartiteSubgraphNoEdges():
    G = nx.Graph()
    G.add_nodes_from([1, 2, 3])
    ValueBipartite(G, 0)
    OutBounds(G)
     
        
def test_NoBipartiteSubgraph():
    triangle_graph = nx.Graph()
    pentagon_graph = nx.Graph()
    pentagon_graph.add_nodes_from(range(1, 6))
    triangle_graph.add_nodes_from([1, 2, 3])
    pentagon_graph.add_edges_from([(1,2), (2,3), (3,4), (4,5), (5,1)])
    triangle_graph.add_edges_from([(1, 2), (2, 3), (3, 1)]) 
    ValueBipartite(triangle_graph, 3, False)
    ValueBipartite(pentagon_graph, 5, False)

def  test_BipartiteSubgraph():
    G1 = nx.Graph()
    G1.add_edges_from([
        (1, 2), (2, 3), (3, 1),  
        (3, 4), (4, 5), (5, 6)   
    ])
    K6 = nx.complete_graph(6)  
    ValueBipartite(G1, 4)
    ValueBipartite(K6, 3)
  

