package CodigoJava;

import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.DefaultEdge;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Ejecuta pruebas de los algoritmos BF, DC y BT sobre grafos aleatorios,
 * midiendo tiempo y operaciones con timeout.
 */
public class Main {

    static final int TIMEOUT_SEC = 15;
    static final int SEED_BASE = 215;
    static final int N_TESTS = 20;
    static final int REPS = 5;

    /**
     * Interfaz funcional para llamar a los métodos de Algoritmos.
     */
    @FunctionalInterface
    interface BiPartitionMethod {

        boolean run(SimpleGraph<Integer, DefaultEdge> G, int k, Algoritmos.OpCounter counter);
    }

    /**
     * Asocia un nombre al método.
     */
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
            fw.write("seed;vertices;aristas;n;k;tiempo BF;ops BF;tiempo DC;ops DC;tiempo BT;ops BT\n");
            Random rd = new Random();

            for (int t = 0; t < N_TESTS; t++) {
                int seed = SEED_BASE + t;
                rd.setSeed(seed);

                // Generar grafo reproducible
                SimpleGraph<Integer, DefaultEdge> G = new SimpleGraph<>(DefaultEdge.class);
                List<Integer> nodes = new ArrayList<>();
                int nUpper = rd.nextInt(8) + 2;  // rango [2..9]
                for (int i = 1; i <= nUpper; i++) {
                    G.addVertex(i);
                    nodes.add(i);
                }
                int maxEdges = nodes.size() * (nodes.size() - 1) / 2;
                int k = rd.nextInt(maxEdges + 1);
                int k1 = rd.nextInt(k + 1);
                for (int[] e : addRandomEdges(nodes, k)) {
                    G.addEdge(e[0], e[1]);
                }

                List<String> row = new ArrayList<>(List.of(
                        String.valueOf(seed),
                        String.format("[1...%d]", nUpper),
                        G.edgeSet().toString(),
                        String.valueOf(k),
                        String.valueOf(k1)
                ));

                // Medir cada algoritmo
                for (NamedMethod nm : methods) {
                    double timeSum = 0;
                    long opsSum = 0;
                    boolean timeout = false;

                    for (int r = 0; r < REPS; r++) {
                        TimeOps res = runWithTimeout(() -> {
                            Algoritmos.OpCounter counter = new Algoritmos.OpCounter();
                            boolean ok = nm.method().run(G, k1, counter);
                            return new TimeOps(ok, counter.get());
                        }, TIMEOUT_SEC);

                        if (res.timedOut) {
                            timeout = true;
                            opsSum += res.ops;
                            break;
                        }
                        timeSum += res.time;
                        opsSum += res.ops;
                    }

                    if (timeout) {
                        row.add("TIMEOUT");
                        row.add(String.valueOf(opsSum));
                    } else {
                        row.add(String.format("%.6f", timeSum / REPS));
                        row.add(String.valueOf(opsSum / REPS));
                    }
                }

                fw.write(String.join(";", row) + "\n");
            }
        }
    }

    /**
     * Wrapper para tiempo y operaciones, o timeout.
     */
    private static class TimeOps {

        boolean timedOut;
        double time;
        long ops;

        TimeOps() {
            this.timedOut = true;
        }

        TimeOps(boolean ok, long ops) {
            this.timedOut = false;
            this.ops = ops;
        }
    }

    /**
     * Ejecuta la tarea con timeout y mide tiempo de ejecución.
     */
    private static TimeOps runWithTimeout(Callable<TimeOps> task, int timeoutSec) {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<TimeOps> fut = exec.submit(task);
        try {
            long start = System.nanoTime();
            TimeOps res = fut.get(timeoutSec, TimeUnit.SECONDS);
            long end = System.nanoTime();
            res.time = (end - start) / 1e9;
            return res;
        } catch (TimeoutException e) {
            fut.cancel(true);
            return new TimeOps();
        } catch (Exception e) {
            fut.cancel(true);
            return new TimeOps();
        } finally {
            exec.shutdownNow();
        }
    }

    /**
     * Selecciona al azar numEdges pares de nodos.
     */
    private static List<int[]> addRandomEdges(List<Integer> nodes, int numEdges) {
        List<int[]> pool = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                pool.add(new int[]{nodes.get(i), nodes.get(j)});
            }
        }
        Collections.shuffle(pool);
        return pool.subList(0, Math.min(numEdges, pool.size()));
    }
}
