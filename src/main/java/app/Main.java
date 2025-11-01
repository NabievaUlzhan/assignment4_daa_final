package app;
import graph.common.Edge;
import graph.dagsp.DAGLongestPath;
import graph.dagsp.DAGShortestPaths;
import graph.scc.CondensationBuilder;
import graph.scc.TarjanSCC;
import graph.topo.KahnTopoOrdering;
import io.GraphLoader;
import model.EdgeData;
import model.GraphData;
import metrics.Metrics;
import metrics.SimpleMetrics;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0){
            System.out.println("Usage: mvn exec:java -Dexec.mainClass=app.Main -Dexec.args=\"data\"");
            return;
        }
        File input = new File(args[0]);
        if (!input.exists()){
            System.out.println("Input not found: " + input.getPath());
            return;
        }

        if (input.isDirectory()){
            File[] files = input.listFiles((dir,name)->name.endsWith(".json"));
            if (files==null || files.length==0){
                System.out.println("No JSON in folder");
                return;
            }
            for (File f : files) processOne(f);
        } else {
            processOne(input);
        }
        System.out.println("Done. See /output/");
    }

    private static void processOne(File jsonFile) throws Exception {
        GraphLoader loader = new GraphLoader();
        GraphData data = loader.load(jsonFile.getPath());

        String datasetName = jsonFile.getName().replaceAll("\\.json$","");
        File outDirBase = new File("output"); if (!outDirBase.exists()) Files.createDirectories(outDirBase.toPath());
        File outDir = new File(outDirBase, datasetName);
        if (outDir.exists()) {
            File[] toDel = outDir.listFiles();
            if (toDel!=null) for (File f:toDel) f.delete();
        } else {
            Files.createDirectories(outDir.toPath());
        }

        int n = data.getNodes();
        List<List<Edge>> adj = new ArrayList<List<Edge>>();
        for (int i=0;i<n;i++) adj.add(new ArrayList<Edge>());
        List<EdgeData> edges = data.getEdges();
        for (int i=0;i<edges.size();i++){
            EdgeData ed = edges.get(i);
            adj.get(ed.getU()).add(new Edge(ed.getV(), ed.getW()));
            if (!data.isDirected()) adj.get(ed.getV()).add(new Edge(ed.getU(), ed.getW()));
        }

        Metrics mScc = new SimpleMetrics();
        mScc.start();
        TarjanSCC tarjan = new TarjanSCC(adj, mScc);
        tarjan.run();
        mScc.stop();
        int[] compId = tarjan.getCompId();
        List<List<Integer>> comps = tarjan.getComponents();
        int compCount = tarjan.getCompCount();

        try (FileWriter fw = new FileWriter(new File(outDir, "scc.txt"))) {
            fw.write("SCC count: " + compCount + "\n");
            for (int c = 0; c < comps.size(); c++) {
                List<Integer> list = new ArrayList<Integer>(comps.get(c));
                Collections.sort(list);
                fw.write("Component " + c + ": " + list + "\n");
            }
            fw.write("Tarjan time (ns): " + mScc.getElapsedNanos() + "\n");
            fw.write("DFS visits: " + mScc.get("scc.visit") + "\n");
            fw.write("Edge scans: " + mScc.get("scc.edge") + "\n");
        }

        try (FileWriter fw = new FileWriter(new File(outDir, "component_map.csv"))) {
            fw.write("node,component\n");
            for (int v = 0; v < compId.length; v++) fw.write(v + "," + compId[v] + "\n");
        }

        List<List<Edge>> dag = new CondensationBuilder().build(n, data.getEdges(), compId, compCount, data.isDirected());

        try (FileWriter fw = new FileWriter(new File(outDir, "condensation.csv"))) {
            fw.write("from_comp,to_comp,weight\n");
            for (int u=0; u<dag.size(); u++){
                List<Edge> list = dag.get(u);
                for (int k=0;k<list.size();k++){
                    Edge e = list.get(k);
                    fw.write(u + "," + e.getTo() + "," + e.getW() + "\n");
                }
            }
        }

        Metrics mKahn = new SimpleMetrics();
        mKahn.start();
        List<Integer> topo = new KahnTopoOrdering().order(dag, mKahn);
        mKahn.stop();
        try (FileWriter fw = new FileWriter(new File(outDir, "topo_components.txt"))) {
            fw.write(topo.toString() + "\n");
            fw.write("Kahn pops: " + mKahn.get("kahn.pop") + "\n");
            fw.write("Kahn pushes: " + mKahn.get("kahn.push") + "\n");
            fw.write("Kahn time: " + mKahn.getElapsedNanos() + "\n");
        }
        List<Integer> expandedTopo = expandTopoToOriginal(topo, comps);
        try (FileWriter fw = new FileWriter(new File(outDir, "topo_expanded.txt"))) {
            fw.write(expandedTopo.toString());
        }
        int srcNode = Math.max(0, Math.min(data.getSource(), n-1));
        int sourceComp = compId[srcNode];
        Metrics mSp = new SimpleMetrics(); mSp.start();
        DAGShortestPaths dsp = new DAGShortestPaths();
        DAGShortestPaths.Result spr = dsp.run(dag, sourceComp, topo, mSp);
        mSp.stop();
        try (FileWriter fw = new FileWriter(new File(outDir, "dag_shortest.csv"))) {
            fw.write("comp,dist,prev\n");
            for (int i=0;i<spr.dist.length;i++){
                String d = spr.dist[i] >= (Integer.MAX_VALUE/8) ? "INF" : String.valueOf(spr.dist[i]);
                fw.write(i + "," + d + "," + spr.prev[i] + "\n");
            }
        }
        try (FileWriter fw = new FileWriter(new File(outDir, "dag_shortest_metrics.txt"))) {
            fw.write("Relaxations: " + mSp.get("dagsp.relax") + "\n");
            fw.write("Time (ns): " + mSp.getElapsedNanos() + "\n");
        }

        Metrics mLp = new SimpleMetrics(); mLp.start();
        DAGLongestPath dlp = new DAGLongestPath();
        DAGLongestPath.Result lpr = dlp.run(dag, sourceComp, topo, mLp);
        mLp.stop();
        try (FileWriter fw = new FileWriter(new File(outDir, "dag_longest.csv"))) {
            fw.write("comp,dist,prev\n");
            for (int i=0;i<lpr.dist.length;i++){
                String d = lpr.dist[i] <= (Integer.MIN_VALUE/8) ? "-INF" : String.valueOf(lpr.dist[i]);
                fw.write(i + "," + d + "," + lpr.prev[i] + "\n");
            }
        }
        try (FileWriter fw = new FileWriter(new File(outDir, "dag_longest_metrics.txt"))) {
            fw.write("Relaxations: " + mLp.get("daglp.relax") + "\n");
            fw.write("Time (ns): " + mLp.getElapsedNanos() + "\n");
        }
    }

    private static List<Integer> expandTopoToOriginal(List<Integer> topo, List<List<Integer>> comps){
        List<Integer> order = new ArrayList<Integer>();
        for (int i=0;i<topo.size();i++){
            int comp = topo.get(i);
            List<Integer> nodes = new ArrayList<Integer>(comps.get(comp));
            Collections.sort(nodes);
            order.addAll(nodes);
        }
        return order;
    }
}
