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

        counter = (counter != null) ? counter : new OpCounter();
        // Crear grafo auxiliar vacío con todos los vértices
        Graph<V, DefaultEdge> H = new SimpleGraph<>(DefaultEdge.class);
        counter.inc();  // Creación del grafo vacío
        for (V v : G.vertexSet()) {
            H.addVertex(v);
            counter.inc();  // Añadir cada vértice
        }

        if (k <= 2) {
            counter.inc();  // Caso trivial (0, 1 ó 2 aristas siempre forman subgrafo bipartito)
            return new BFResult<>(true, Collections.emptySet());
        }

        List<DefaultEdge> edgesList = new ArrayList<>(G.edgeSet());
        // Recorrer todas las combinaciones de k aristas
        Combinations combIterator = new Combinations(edgesList.size(), k);
        for (int[] combination : combIterator) {
            // Añadir las aristas de esta combinación al grafo H
            for (int idx : combination) {
                DefaultEdge e = edgesList.get(idx);
                H.addEdge(G.getEdgeSource(e), G.getEdgeTarget(e));
            }
            counter.inc(combination.length);  // Añadidas k aristas al subgrafo

            counter.inc();  // Operación de comprobar bipartito
            if (GraphTests.isBipartite(H)) {
                // Si encontramos un subgrafo bipartito de k aristas, devolvemos resultado
                return new BFResult<>(true, new HashSet<>(H.edgeSet()));
            }
            // Si no es bipartito, remover las aristas añadidas antes de probar la siguiente combinación
            for (int idx : combination) {
                DefaultEdge e = edgesList.get(idx);
                // Remover arista añadida (buscándola por sus extremos, ya que H tiene aristas distintas a G)
                H.removeEdge(G.getEdgeSource(e), G.getEdgeTarget(e));
            }
            counter.inc(combination.length);  // Removidas k aristas del subgrafo
        }
        // Si ninguna combinación produce un subgrafo bipartito de tamaño k
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

        counter = (counter != null) ? counter : new OpCounter();
        // Grafo auxiliar H vacío (con todos los vértices de G, sin aristas)
        Graph<V, DefaultEdge> H = new SimpleGraph<>(DefaultEdge.class);
        counter.inc();  // Creación del grafo H
        for (V v : G.vertexSet()) {
            H.addVertex(v);
            counter.inc();  // Añadir cada vértice a H
        }

        if (k <= 2) {
            return true;
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
        if (kLeft == 0) {
            return true;
        }
        if (edges.size() < kLeft) {
            return false;
        }

        // Rama EXCLUIR la primera arista
        Graph<V, DefaultEdge> H_exclude = cloneGraph(H, counter);  // Copia del grafo actual
        boolean exclResult = dcRec(H_exclude, G, kLeft, edges.subList(1, edges.size()), counter);
        counter.inc();  // Operación tras completar rama "excluir"
        if (exclResult) {
            return true;
        }

        // Rama INCLUIR la primera arista
        DefaultEdge firstEdge = edges.get(0);
        Graph<V, DefaultEdge> H_include = cloneGraph(H, counter);  // Copia del grafo actual
        H_include.addEdge(G.getEdgeSource(firstEdge), G.getEdgeTarget(firstEdge));
        counter.inc();  // Añadir una arista al subgrafo
        boolean inclResult = false;
        if (GraphTests.isBipartite(H_include)) {
            inclResult = dcRec(H_include, G, kLeft - 1, edges.subList(1, edges.size()), counter);
        }
        counter.inc();  // Operación tras completar rama "incluir"
        if (inclResult) {
            return true;
        }
        // Si ninguna rama encontró solución bipartita de tamaño k, retrocede
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

        counter = (counter != null) ? counter : new OpCounter();
        if (k <= 2) {
            counter.inc();  // Caso trivial (subgrafo de 0,1,2 aristas es bipartito)
            return true;
        }

        // Grafo auxiliar H vacío (con todos los vértices de G, sin aristas)
        Graph<V, DefaultEdge> H = new SimpleGraph<>(DefaultEdge.class);
        counter.inc();  // Creación del grafo H
        for (V v : G.vertexSet()) {
            H.addVertex(v);
            counter.inc();  // Añadir cada vértice a H
        }

        List<DefaultEdge> edges = new ArrayList<>(G.edgeSet());
        // Llamada inicial a la función recursiva de backtracking
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
        if (chosen == k) {
            counter.inc();  // k aristas elegidas (solución completa encontrada)
            return true;
        }
        if (edges.size() - start < k - chosen) {
            counter.inc();  // No hay suficientes aristas restantes para completar k
            return false;
        }

        for (int i = start; i < edges.size(); i++) {
            DefaultEdge e = edges.get(i);
            V src = G.getEdgeSource(e);
            V tgt = G.getEdgeTarget(e);
            // Añadir la arista edges[i] al grafo H
            DefaultEdge addedEdge = H.addEdge(src, tgt);
            counter.inc();  // Añadir arista actual
            if (GraphTests.isBipartite(H)) {
                counter.inc();  // Preparar llamada recursiva
                if (btRec(H, G, k, chosen + 1, edges, i + 1, counter)) {
                    return true;
                }
            }
            // Remover la arista añadida (backtrack)
            H.removeEdge(addedEdge);
            counter.inc();  // Remover arista actual
        }
        counter.inc();  // Fin de esta rama sin encontrar solución
        return false;
    }

    /**
     * Test de bipartición utilizando JGraphT (utilidad auxiliar opcional).
     */
    private static <V> boolean isBipartite(Graph<V, DefaultEdge> g) {
        return GraphTests.isBipartite(g);
    }
}
