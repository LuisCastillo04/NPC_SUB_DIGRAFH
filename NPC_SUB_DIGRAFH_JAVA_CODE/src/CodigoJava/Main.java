package CodigoJava;

import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    static final int TIMEOUT = 5;

    private record NamedMethod(String name, BiPartitionMethod method) {

    }

    public static void main(String[] args) throws IOException {
        tests();
    }

    public static void tests() throws IOException {

        List<NamedMethod> methods = List.of(
                new NamedMethod("BF", Algoritmos::bipartiteSubsetBF_bool),
                new NamedMethod("DC", Algoritmos::bipartiteSubsetDC),
                new NamedMethod("BT", Algoritmos::bipartiteSubsetBT)
        );

        try (FileWriter fw = new FileWriter("results.csv")) {
            fw.write("vertices;aristas;n;k;tiempo BF;tiempo DC;tiempo BT\n");
            Random rd = new Random();

            for (int rep = 0; rep < 10; rep++) {
                /* 1) Grafo aleatorio */
                SimpleGraph<Integer, DefaultEdge> G = new SimpleGraph<>(DefaultEdge.class);
                int nUpper = rd.nextInt(8) + 2;           // 2-9
                List<Integer> nodes = new ArrayList<>();
                for (int v = 1; v < nUpper; v++) {
                    G.addVertex(v);
                    nodes.add(v);
                }
                int maxEdges = nodes.size() * (nodes.size() - 1) / 2;
                int k = rd.nextInt(maxEdges + 1);
                int k1 = rd.nextInt(k + 1);

                /* 2) Aristas aleatorias */
                for (int[] e : addRandomEdges(nodes, k)) {
                    G.addEdge(e[0], e[1]);
                }

                /* 3) Medir cada algoritmo (promedio de 5) ------------------------------ */
                double[] times = new double[methods.size()];
                int idx = 0;                                           // ← índice manual
                for (NamedMethod nm : methods) {                       // ← for-each
                    boolean timeout = false;
                    double acc = 0;

                    for (int r = 0; r < 5; r++) {
                        // nm no cambia dentro de la lambda → variable efectivamente final
                        Double elapsed = runWithTimeout(
                                () -> nm.method().run(G, k1), // misma llamada p/ todos
                                TIMEOUT
                        );
                        if (elapsed == null) {
                            timeout = true;
                            break;
                        }
                        acc += elapsed;
                    }
                    times[idx++] = timeout ? -1 : acc / 5.0;           // guarda y avanza índice
                }


                /* 4) Escribir fila */
                StringBuilder row = new StringBuilder();
                row.append(String.format("[1...%d];%s;%d;%d",
                        nodes.get(nodes.size() - 1),
                        G.edgeSet(), k, k1
                ));
                for (double t : times) {
                    row.append(";").append(t < 0 ? "TIMEOUT" : t);
                }
                row.append('\n');
                fw.write(row.toString());
            }
        }
    }


    /* Ejecuta una tarea con timeout (en segundos). Devuelve tiempo o null si agota el límite */
    private static Double runWithTimeout(Runnable task, int timeoutSec) {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<?> fut = exec.submit(task);
        try {
            long start = System.nanoTime();
            fut.get(timeoutSec, TimeUnit.SECONDS);
            long end = System.nanoTime();
            return (end - start) / 1e9; // segundos
        } catch (TimeoutException e) {
            fut.cancel(true);
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            exec.shutdownNow();
        }
    }

    /* Selecciona al azar numEdges pares distintos de la lista nodes */
    private static List<int[]> addRandomEdges(List<Integer> nodes, int numEdges) {
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
