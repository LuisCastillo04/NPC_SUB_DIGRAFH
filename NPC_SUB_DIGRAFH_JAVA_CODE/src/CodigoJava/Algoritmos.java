package CodigoJava;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.apache.commons.math3.util.Combinations;

import java.util.*;

public class Algoritmos {

    /*–––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
      Clona un grafo (vertices + aristas), sin alterar el original
    –––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––*/
    private static <V> Graph<V,DefaultEdge> cloneGraph(Graph<V,DefaultEdge> g) {
        Graph<V,DefaultEdge> copy = new SimpleGraph<>(DefaultEdge.class);
        for (V v : g.vertexSet())            copy.addVertex(v);
        for (DefaultEdge e : g.edgeSet())    copy.addEdge(g.getEdgeSource(e), g.getEdgeTarget(e));
        return copy;
    }

    /*–––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
      Fuerza Bruta (BF)
      Prueba todas las combinaciones de k aristas hasta hallar un subgrafo
      bipartito o agotar las posibilidades.
    –––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––*/
    public static <V> boolean bipartiteSubsetBF(Graph<V,DefaultEdge> G, int k) {
        if (k < 0)        throw new IllegalArgumentException("k debe ser ≥ 0");
        if (k > G.edgeSet().size()) 
                          throw new IllegalArgumentException("k debe ser ≤ número de aristas");
        if (k <= 2)       return true;

        // Subgrafo H con mismos vértices y sin aristas
        Graph<V,DefaultEdge> H = cloneGraph(G);
        H.removeAllEdges(new HashSet<>(H.edgeSet()));

        List<DefaultEdge> edges = new ArrayList<>(G.edgeSet());
        for (int[] idx : new Combinations(edges.size(), k)) {
            // construye H con el subconjunto idx
            H.removeAllEdges(new HashSet<>(H.edgeSet()));
            for (int i : idx) {
                DefaultEdge e = edges.get(i);
                H.addEdge(G.getEdgeSource(e), G.getEdgeTarget(e));
            }
            if (GraphTests.isBipartite(H)) return true;
        }
        return false;
    }

    /*–––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
      Divide & Conquer (DC)
      Explora recursivamente “excluir/incluir” la arista actual,
      podando tablas no bipartitas.
    –––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––*/
    public static <V> boolean bipartiteSubsetDC(Graph<V,DefaultEdge> G, int k) {
        if (k < 0)        throw new IllegalArgumentException("k debe ser ≥ 0");
        if (k > G.edgeSet().size()) 
                          throw new IllegalArgumentException("k debe ser ≤ número de aristas");
        if (k <= 2)       return true;
        return bipartiteSubsetDC(cloneGraph(G), G, k, new ArrayList<>(G.edgeSet()));
    }

    private static <V> boolean bipartiteSubsetDC(
        Graph<V,DefaultEdge> H,
        Graph<V,DefaultEdge> G,
        int kLeft,
        List<DefaultEdge> edges
    ) {
        if (kLeft == 0)              return true;
        if (edges.size() < kLeft)    return false;

        // 1) Excluir la arista edges.get(0)
        if (bipartiteSubsetDC(cloneGraph(H), G, kLeft, edges.subList(1, edges.size())))
            return true;

        // 2) Incluir la arista edges.get(0), si sigue bipartito
        DefaultEdge e = edges.get(0);
        Graph<V,DefaultEdge> H2 = cloneGraph(H);
        H2.addEdge(G.getEdgeSource(e), G.getEdgeTarget(e));
        if (GraphTests.isBipartite(H2) &&
            bipartiteSubsetDC(H2, G, kLeft - 1, edges.subList(1, edges.size())))
            return true;

        return false;
    }

    /*–––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
      Backtracking (BT)
      Añade aristas una a una, retrocediendo en cuanto se rompe la bipartición.
    –––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––*/
    public static <V> boolean bipartiteSubsetBT(Graph<V,DefaultEdge> G, int k) {
        if (k < 0)        throw new IllegalArgumentException("k debe ser ≥ 0");
        if (k > G.edgeSet().size()) 
                          throw new IllegalArgumentException("k debe ser ≤ número de aristas");
        if (k <= 2 || (k == G.edgeSet().size() && GraphTests.isBipartite(G)))
                          return true;

        Graph<V,DefaultEdge> H = cloneGraph(G);
        H.removeAllEdges(new HashSet<>(G.edgeSet()));
        return bipartiteSubsetBT(H, G, k, 0, new ArrayList<>(G.edgeSet()));
    }

    private static <V> boolean bipartiteSubsetBT(
        Graph<V,DefaultEdge> H,
        Graph<V,DefaultEdge> G,
        int k,
        int chosen,
        List<DefaultEdge> edges
    ) {
        if (chosen == k) return true;
        for (DefaultEdge e : edges) {
            if (!H.containsEdge(e)) {
                Graph<V,DefaultEdge> H2 = cloneGraph(H);
                H2.addEdge(G.getEdgeSource(e), G.getEdgeTarget(e));
                if (GraphTests.isBipartite(H2) &&
                    bipartiteSubsetBT(H2, G, k, chosen + 1, edges))
                    return true;
            }
        }
        return false;
    }
}
