package app;
import model.EdgeData;
import model.GraphData;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatasetGenerator {
    public static void main(String[] args) throws Exception {
        File dataDir = new File("data");
        if (!dataDir.exists()) Files.createDirectories(dataDir.toPath());
        Random rnd = new Random(2429);

        makeDataset(dataDir, "small_1.json", 6, true, 1, "dag_sparse", rnd, 0.3, false);
        makeDataset(dataDir, "small_2.json", 8, true, 0, "one_cycle", rnd, 0.4, true);
        makeDataset(dataDir, "small_3.json", 10, true, 4, "two_cycles", rnd, 0.45, true);

        makeDataset(dataDir, "medium_1.json", 12, true, 0, "mixed_sccs", rnd, 0.35, true);
        makeDataset(dataDir, "medium_2.json", 15, true, 2, "dag_dense", rnd, 0.6, false);
        makeDataset(dataDir, "medium_3.json", 18, true, 5, "multi_sccs", rnd, 0.4, true);

        makeDataset(dataDir, "large_1.json", 22, true, 0, "perf_dag", rnd, 0.5, false);
        makeDataset(dataDir, "large_2.json", 30, true, 4, "perf_mixed", rnd, 0.35, true);
        makeDataset(dataDir, "large_3.json", 40, true, 10, "perf_dense", rnd, 0.55, true);
        System.out.println("Datasets generated in to /data");
    }

    private static void makeDataset(File dataDir, String fileName, int n, boolean directed, int source,
                                    String model, Random rnd, double density, boolean allowCycles) throws Exception {
        List<EdgeData> edges = new ArrayList<EdgeData>();
        for (int i=0;i<n-1;i++){
            int w = 1 + rnd.nextInt(9);
            edges.add(new EdgeData(i, i+1, w));
        }
        int maxEdges = directed ? n*(n-1) : n*(n-1)/2;
        int target = Math.min(maxEdges, Math.max(n-1, (int)(density * maxEdges)));
        while (edges.size() < target){
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);
            if (u==v) continue;
            if (!allowCycles && u >= v) continue;
            boolean exists = false;
            for (int k=0;k<edges.size();k++){
                EdgeData e = edges.get(k);
                if (e.getU()==u && e.getV()==v){ exists=true; break; }
            }
            if (exists) continue;
            int w = 1 + rnd.nextInt(9);
            edges.add(new EdgeData(u, v, w));
        }

        if (allowCycles && n >= 6){
            edges.add(new EdgeData(2,1,1));
            edges.add(new EdgeData(3,2,1));
            edges.add(new EdgeData(1,3,1));
            edges.add(new EdgeData(6%n,5%n,2));
        }
        GraphData g = new GraphData();
        g.setDirected(directed);
        g.setNodes(n);
        g.setSource(Math.min(source, n-1));
        g.setModel(model);
        g.setEdges(edges);

        File out = new File(dataDir, fileName);
        try (FileWriter fw = new FileWriter(out)) {
            fw.write("{\n");
            fw.write("  \"directed\": " + directed + ",\n");
            fw.write("  \"nodes\": " + n + ",\n");
            fw.write("  \"source\": " + Math.min(source, n-1) + ",\n");
            fw.write("  \"model\": \"" + model + "\",\n");
            fw.write("  \"edges\": [\n");
            for (int i=0;i<edges.size();i++){
                EdgeData e = edges.get(i);
                fw.write("    {\"u\":" + e.getU() + ",\"v\":" + e.getV() + ",\"w\":" + e.getW() + "}");
                if (i < edges.size()-1) fw.write(",");
                fw.write("\n");
            }
            fw.write("  ]\n");
            fw.write("}\n");
        }
    }
}

