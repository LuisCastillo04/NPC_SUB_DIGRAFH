package CodigoJava;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.apache.commons.math3.util.Combinations;

import java.util.*;

public class Algoritmos {

    
    /**
     * Contador de operaciones interno para medir pasos.
     */
    public static class OpCounter {
        private long count = 0;
        /** Incrementa en 1 */
        public void inc() { count++; }
        /** Incrementa en n */
        public void inc(long n) { count += n; }
        /** Obtiene el total */
        public long get() { return count; }
    }

    
    /**
     * Clona un grafo (vértices + aristas) contando operaciones.
     */
    private static <V> Graph<V, DefaultEdge> cloneGraph(
            Graph<V, DefaultEdge> g,
            OpCounter counter
    ) {
        counter.inc();
        Graph<V, DefaultEdge> copy = new SimpleGraph<>(DefaultEdge.class);
        for (V v : g.vertexSet()) {
            copy.addVertex(v);
            counter.inc();
        }
        for (DefaultEdge e : g.edgeSet()) {
            copy.addEdge(g.getEdgeSource(e), g.getEdgeTarget(e));
            counter.inc();
        }
        return copy;
    }

    /**
     * Resultado de Fuerza Bruta, con flag y conjunto de aristas.
     */
    public static class BFResult<V> {
        public final boolean found;
        public final Set<DefaultEdge> edges;
        public BFResult(boolean f, Set<DefaultEdge> e) {
            this.found = f;
            this.edges = e;
        }
    }

    /**
     * 1) Fuerza Bruta: prueba todas las combinaciones de k aristas.
     */
    public static <V> BFResult<V> bipartiteSubsetBF(
            Graph<V, DefaultEdge> G,
            int k,
            OpCounter counter
    ) {
        if (k < 0) throw new IllegalArgumentException("k debe ser ≥ 0");
        if (k > G.edgeSet().size()) throw new IllegalArgumentException("k debe ser ≤ número de aristas");
        counter.inc();  // comprobaciones iniciales
        if (k <= 2) {
            return new BFResult<>(true, Collections.emptySet());
        }

        // grafo auxiliar vacío
        Graph<V, DefaultEdge> H = cloneGraph(G, counter);
        H.removeAllEdges(new HashSet<>(H.edgeSet()));
        counter.inc(G.edgeSet().size());

        List<DefaultEdge> edges = new ArrayList<>(G.edgeSet());
        for (int[] idx : new Combinations(edges.size(), k)) {
            // restablecer H
            H.removeAllEdges(new HashSet<>(H.edgeSet()));
            counter.inc(G.edgeSet().size());
            // añadir aristas de la combinación
            for (int i : idx) {
                DefaultEdge e = edges.get(i);
                H.addEdge(G.getEdgeSource(e), G.getEdgeTarget(e));
                counter.inc();
            }
            counter.inc();  // antes de test de bipartición
            if (GraphTests.isBipartite(H)) {
                return new BFResult<>(true, new HashSet<>(H.edgeSet()));
            }
        }
        return new BFResult<>(false, Collections.emptySet());
    }

    /**
     * Versión booleana de BF (devuelve solo flag).
     */
    public static <V> boolean bipartiteSubsetBF_bool(
            Graph<V, DefaultEdge> G,
            int k,
            OpCounter counter
    ) {
        return bipartiteSubsetBF(G, k, counter).found;
    }

    /**
     * 2) Divide & Conquer: inclusión/exclusión recursiva.
     */
    public static <V> boolean bipartiteSubsetDC(
            Graph<V, DefaultEdge> G,
            int k,
            OpCounter counter
    ) {
        if (k < 0) throw new IllegalArgumentException("k debe ser ≥ 0");
        if (k > G.edgeSet().size()) throw new IllegalArgumentException("k debe ser ≤ número de aristas");
        counter.inc();
        if (k <= 2) return true;

        Graph<V, DefaultEdge> H = new SimpleGraph<>(DefaultEdge.class);
        counter.inc();
        for (V v : G.vertexSet()) {
            H.addVertex(v);
            counter.inc();
        }
        List<DefaultEdge> edges = new ArrayList<>(G.edgeSet());
        return dcRec(H, G, k, edges, counter);
    }

    private static <V> boolean dcRec(
            Graph<V, DefaultEdge> H,
            Graph<V, DefaultEdge> G,
            int kLeft,
            List<DefaultEdge> edges,
            OpCounter counter
    ) {
        if (kLeft == 0) return true;
        if (edges.size() < kLeft) return false;

        // excluir primera arista
        counter.inc();
        if (dcRec(cloneGraph(H, counter), G, kLeft, edges.subList(1, edges.size()), counter)) {
            return true;
        }

        // incluir primera arista
        DefaultEdge e = edges.get(0);
        Graph<V, DefaultEdge> H2 = cloneGraph(H, counter);
        H2.addEdge(G.getEdgeSource(e), G.getEdgeTarget(e));
        counter.inc();
        if (GraphTests.isBipartite(H2)
                && dcRec(H2, G, kLeft - 1, edges.subList(1, edges.size()), counter)) {
            return true;
        }
        return false;
    }

    /**
     * 3) Backtracking: añade aristas y retrocede si no bipartito.
     */
    public static <V> boolean bipartiteSubsetBT(
            Graph<V, DefaultEdge> G,
            int k,
            OpCounter counter
    ) {
        if (k < 0) throw new IllegalArgumentException("k debe ser ≥ 0");
        if (k > G.edgeSet().size()) throw new IllegalArgumentException("k debe ser ≤ número de aristas");
        counter.inc();
        if (k <= 2) return true;

        Graph<V, DefaultEdge> H = new SimpleGraph<>(DefaultEdge.class);
        counter.inc();
        for (V v : G.vertexSet()) {
            H.addVertex(v);
            counter.inc();
        }
        List<DefaultEdge> edges = new ArrayList<>(G.edgeSet());
        return btRec(H, G, k, 0, edges, 0, counter);
    }

    private static <V> boolean btRec(
            Graph<V, DefaultEdge> H,
            Graph<V, DefaultEdge> G,
            int k,
            int chosen,
            List<DefaultEdge> edges,
            int start,
            OpCounter counter
    ) {
        if (chosen == k) return true;
        if (edges.size() - start < k - chosen) return false;

        for (int i = start; i < edges.size(); i++) {
            DefaultEdge e = edges.get(i);
            Graph<V, DefaultEdge> H2 = cloneGraph(H, counter);
            H2.addEdge(G.getEdgeSource(e), G.getEdgeTarget(e));
            counter.inc();
            if (GraphTests.isBipartite(H2)
                    && btRec(H2, G, k, chosen + 1, edges, i + 1, counter)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test de bipartición utilizando JGraphT.
     */
    private static <V> boolean isBipartite(Graph<V, DefaultEdge> g) {
        return GraphTests.isBipartite(g);
    }
}
