package CodigoJava;

import CodigoJava.Algoritmos;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;



public class Main {

    static final int TIMEOUT = 50;

    private record NamedMethod(String name, BiPartitionMethod method) {}

    
    public static void main(String[] args) throws IOException {
        tests();
    }

    public static void tests() throws IOException {
        Timer t = new Timer();
        List<NamedMethod> methods = List.of(
            new NamedMethod("bipartiteSubsetBF", Algoritmos::bipartiteSubsetBF),
            new NamedMethod("bipartiteSubsetDC", Algoritmos::bipartiteSubsetDC),
            new NamedMethod("bipartiteSubsetBT", Algoritmos::bipartiteSubsetBT)
        );

        FileWriter fw = new FileWriter("results.csv");
        fw.write("vertices;aristas;n;k;metodo;tiempo\n");

        Random rd = new Random();

        for (int rep = 0; rep < 10; rep++) {
            SimpleGraph<Integer, DefaultEdge> G = new SimpleGraph<>(DefaultEdge.class);
            int verticesCount = rd.nextInt(8) + 2; // 2 a 9 vértices
            List<Integer> nodes = new ArrayList<>();
            for (int i = 1; i <= verticesCount; i++) {
                G.addVertex(i);
                nodes.add(i);
            }

            int maxEdges = verticesCount * (verticesCount - 1) / 2;
            int k = rd.nextInt(maxEdges + 1);
            int k1 = rd.nextInt(k + 1);

            List<int[]> edges = addRandomEdges(nodes, k);
            for (int[] edge : edges) {
                G.addEdge(edge[0], edge[1]);
            }

            for (NamedMethod nm : methods) {
                    double ti = 0;
                    boolean timeout = false;
                    for (int i = 0; i < 5; i++) {
                        Double elapsed = runWithTimeout(
                            () -> nm.method().run(G, k1),
                            TIMEOUT
                        );
                        if (elapsed == null) {
                            ti = -1;
                            timeout = true;
                            break;
                        }
                        ti += elapsed;
                    }
                    if (!timeout) ti /= 5.0;

                    fw.write(String.format(
                        "[1...%d];%s;%d;%d;%s;%s\n",
                        verticesCount,
                        G.edgeSet(),
                        k,
                        k1,
                        nm.name(),                       // <-- aquí tu nombre
                        (ti < 0) ? "TIMEOUT" : String.valueOf(ti)
                    ));
                }
            }
        fw.close();
    }

    static Double runWithTimeout(Runnable task, int timeoutSeconds) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(task);
        try {
            long start = System.nanoTime();
            future.get(timeoutSeconds, TimeUnit.SECONDS);
            long end = System.nanoTime();
            return (end - start) / 1e9;
        } catch (TimeoutException e) {
            future.cancel(true);
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            executor.shutdownNow();
        }
    }

    public static List<int[]> addRandomEdges(List<Integer> nodes, int numEdges) {
        List<int[]> possibles = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                possibles.add(new int[]{nodes.get(i), nodes.get(j)});
            }
        }
        Collections.shuffle(possibles);
        return possibles.subList(0, Math.min(numEdges, possibles.size()));
    }

    @FunctionalInterface
    interface BiPartitionMethod {
        boolean run(SimpleGraph<Integer, DefaultEdge> G, int k);
    }
}
