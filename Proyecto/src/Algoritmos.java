    import org.jgrapht.Graph;
    import org.jgrapht.graph.DefaultEdge;
    import org.jgrapht.graph.SimpleGraph;
    import org.jgrapht.GraphTests;
    import org.apache.commons.math3.util.Combinations;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.random.*;


    public class Algoritmos {

    public static <V> boolean BipartiteSubsetBT(Graph<V, DefaultEdge> G, int k) {
            // ExceptionsGraph ex = new ExceptionsGraph();
            // ex.LessLedges(k, G.vertexSet().size());
            Graph<V, DefaultEdge> G1 = new SimpleGraph<>(DefaultEdge.class);
            List<DefaultEdge> edges = new ArrayList<>(G.edgeSet());
            for (V v : G.vertexSet()) {
                G1.addVertex(v);
            }
            
            return BipartiteSubsetBTAux(G1, 0,k,edges,G);        
    }
    
    private static <V> boolean BipartiteSubsetBTAux(Graph<V, DefaultEdge> G1, int kpass,int k,List<DefaultEdge> edges,Graph<V, DefaultEdge> G) {
            if (kpass == k) {
                    return true;
                }
 
            for (DefaultEdge edge : edges) {
                V u = G.getEdgeSource(edge);
                V v = G.getEdgeTarget(edge);
                if (G1.containsEdge(u, v) ){
                    continue; // ya está añadida
                }   

                G1.addEdge(u, v);
                if (GraphTests.isBipartite(G2)) {
                    if (BipartiteSubsetBTAux(G2, kpass + 1, k, edges,G)) {
                        return true;
                    }    
                    } 
                G1.removeEdge(u, v);
                }
            return false;
        }    
        public static <V> boolean BipartiteSubsetBF(Graph<V, DefaultEdge> G, int k) {
            // ExceptionsGraph ex = new ExceptionsGraph();
            // ex.LessLedges(k, G.vertexSet().size());
            Graph<V, DefaultEdge> G1 = new SimpleGraph<>(DefaultEdge.class);
            List<DefaultEdge> edges = new ArrayList<>(G.edgeSet());
            Combinations comb = new Combinations(edges.size(), k);
            if (k<= 2){
                return true ; // Si k es 0 o 1, siempre se puede formar un conjunto bipartito
            }
            for (V v : G.vertexSet()) {
                G1.addVertex(v);
            }



            for (int[] com : comb) {
                for (int i : com) { 
                    DefaultEdge Edge = edges.get(i);
                    V u = G.getEdgeSource(Edge);
                    V v = G.getEdgeTarget(Edge);
                    G1.addEdge(u, v);
                }

                if (GraphTests.isBipartite(G1)) {
                    System.out.println("G1: " + G1);
                    return true;
                }
                G1.removeAllEdges(new ArrayList<>(G1.edgeSet()));
            }
            return false;
        }
        public static <V> boolean BipartiteSubsetDC(Graph<V, DefaultEdge> G, int k) {
            // ExceptionsGraph ex = new ExceptionsGraph();
            // ex.LessLedges(k, G.vertexSet().size());
            Graph<V, DefaultEdge> G1 = new SimpleGraph<>(DefaultEdge.class);
            List<DefaultEdge> edges = new ArrayList<>(G.edgeSet());
            for (V v : G.vertexSet()) {
                G1.addVertex(v);
            }
            
            return BipartiteSubsetDCAux(G1, k,k,edges,G);     

        }    
        private static <V> boolean BipartiteSubsetDCAux(Graph<V, DefaultEdge> G1, int kleft,int k,List<DefaultEdge> edges,Graph<V, DefaultEdge> G) {
            if (kleft == 0) {
                return true; // Se ha encontrado un conjunto bipartito de tamaño k
            }
            
            if(kleft > edges.size()){
                return false; // No hay suficientes aristas para formar un conjunto bipartito
            }
            boolean Noadded ;
            boolean Added  = false;

            Graph<V, DefaultEdge> G2 = new SimpleGraph<>(DefaultEdge.class);
            Graph<V, DefaultEdge> G3 = new SimpleGraph<>(DefaultEdge.class);
            Graphs.addGraph(G2, G1); 
            Graphs.addGraph(G3, G1);  
            
  
            
            Noadded = BipartiteSubsetDCAux(G2, kleft, k, edges.subList(1, edges.size()), G);
            
            G3.addEdge(G.getEdgeSource(edges.get(0)), G.getEdgeTarget(edges.get(0)));
            
            if (GraphTests.isBipartite(G3)) {
                    Added = BipartiteSubsetDCAux(G3, kleft - 1, k, edges.subList(1, edges.size()), G);      
            }
            return Noadded || Added;
        
        }

        public static void main(String[] args) {
        
            Graph<Integer, DefaultEdge> G = new SimpleGraph<>(DefaultEdge.class);

            // Agregar vértices
            for (int i = 1; i <= 8; i++) {
                G.addVertex(i);
            }

            G.addEdge(1, 2);
            G.addEdge(2, 3);
            G.addEdge(3, 4);
            G.addEdge(4, 5);
            G.addEdge(5, 1); 
            G.addEdge(6, 2);
            G.addEdge(6, 7);
            G.addEdge(7, 8);
            System.out.println("G: " + G);
            System.out.println(Algoritmos.<Integer>BipartiteSubsetBF(G,8));

        }
    }
