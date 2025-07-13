# test_algorithms.py
import pytest
import networkx as nx

from Algoritmos import (
    BipartiteSubsetBF,
    BipartiteSubsetDC,
    BipartiteSubsetBT,
)

ALGS = [BipartiteSubsetBF, BipartiteSubsetDC, BipartiteSubsetBT]


def extract_bool(result):
    """Convierte la salida (bool o (bool, ...)) en su valor lógico."""
    return result[0] if isinstance(result, tuple) else result


# 1) Grafo bipartito completo: debe aceptar k = |E|
@pytest.mark.parametrize("alg", ALGS)
def test_full_bipartite_graph_full_k_true(alg):
    G = nx.complete_bipartite_graph(3, 4)  # 3×4 = 12 aristas
    k = G.number_of_edges()
    assert extract_bool(alg(G, k)) is True


# 2) Camino simple (siempre bipartito) con k = |E|
@pytest.mark.parametrize("alg", ALGS)
def test_path_graph_k_equal_E_true(alg):
    G = nx.path_graph(5)  # 4 aristas
    k = G.number_of_edges()
    assert extract_bool(alg(G, k)) is True


# 3) Grafo estrella, pidiendo subconjunto más pequeño que |E|
@pytest.mark.parametrize("alg", ALGS)
def test_star_graph_subset_true(alg):
    G = nx.star_graph(4)  # centro + 4 hojas → 4 aristas
    assert extract_bool(alg(G, 3)) is True   # cualquier 3 aristas sirven


# 4) Grafo sin aristas, k = 0 es permitido
@pytest.mark.parametrize("alg", ALGS)
def test_empty_graph_zero_edges_true(alg):
    G = nx.empty_graph(6)  # 6 nodos, 0 aristas
    assert extract_bool(alg(G, 0)) is True


# 5) Triángulo + hoja colgante: k = |E| obliga a usar todas → no bipartito
@pytest.mark.parametrize("alg", ALGS)
def test_triangle_with_leaf_full_k_false(alg):
    G = nx.Graph()
    G.add_edges_from([(0, 1), (1, 2), (2, 0), (0, 3)])  # 4 aristas
    k = G.number_of_edges()
    assert extract_bool(alg(G, k)) is False