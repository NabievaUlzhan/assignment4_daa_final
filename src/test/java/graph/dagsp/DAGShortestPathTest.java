package graph.dagsp;

import graph.common.Edge;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class DAGShortestPathTest {
    @Test
    public void simpleDAG() {
        //simple DAG: 0 → 1 → 2 → 3
        List<List<Edge>> dag = new ArrayList<>();
        int n = 4;
        for (int i = 0; i < n; i++) dag.add(new ArrayList<>());
        dag.get(0).add(new Edge(1, 2));
        dag.get(1).add(new Edge(2, 3));
        dag.get(2).add(new Edge(3, 1));

        DAGShortestPaths dsp = new DAGShortestPaths();
        List<Integer> topo = new ArrayList<>();
        topo.add(0); topo.add(1); topo.add(2); topo.add(3);
        DAGShortestPaths.Result result = dsp.run(dag, 0, topo, null);

        //shortest path from 0 to 3: 0 -> 1 -> 2 -> 3
        Assert.assertEquals(6, result.dist[3]);
        Assert.assertEquals(0, result.prev[3]);
    }

    @Test
    public void disconnectedDAG() {
        //disconnected DAG: 0 -> 1, 2 -> 3
        List<List<Edge>> dag = new ArrayList<>();
        int n = 4;
        for (int i = 0; i < n; i++) dag.add(new ArrayList<>());
        dag.get(0).add(new Edge(1, 5));
        dag.get(2).add(new Edge(3, 2));

        DAGShortestPaths dsp = new DAGShortestPaths();
        List<Integer> topo = new ArrayList<>();
        topo.add(0); topo.add(1); topo.add(2); topo.add(3);
        DAGShortestPaths.Result result = dsp.run(dag, 0, topo, null);
        Assert.assertEquals(Integer.MAX_VALUE / 4, result.dist[3]);
    }

    @Test
    public void singleNodeDAG() {
        //single node graph (no edges)
        List<List<Edge>> dag = new ArrayList<>();
        int n = 1;
        dag.add(new ArrayList<>());

        DAGShortestPaths dsp = new DAGShortestPaths();
        List<Integer> topo = new ArrayList<>();
        topo.add(0);
        DAGShortestPaths.Result result = dsp.run(dag, 0, topo, null);
        //from node 0 to node 0
        Assert.assertEquals(0, result.dist[0]);
        Assert.assertEquals(-1, result.prev[0]);
    }

    @Test
    public void complexDAG() {
        //complex DAG: 0 -> 1, 0 -> 2, 1 -> 3, 2 -> 3
        List<List<Edge>> dag = new ArrayList<>();
        int n = 4;
        for (int i = 0; i < n; i++) dag.add(new ArrayList<>());
        dag.get(0).add(new Edge(1, 2));
        dag.get(0).add(new Edge(2, 3));
        dag.get(1).add(new Edge(3, 1));
        dag.get(2).add(new Edge(3, 4));

        DAGShortestPaths dsp = new DAGShortestPaths();
        List<Integer> topo = new ArrayList<>();
        topo.add(0); topo.add(1); topo.add(2); topo.add(3);
        DAGShortestPaths.Result result = dsp.run(dag, 0, topo, null);
        //shortest path from 0 to 3: 0 -> 1 -> 3
        Assert.assertEquals(3, result.dist[3]);
        Assert.assertEquals(1, result.prev[3]);
    }

    @Test
    public void performanceTest() {
        //performance test with a larger DAG: 100 nodes with linear structure
        List<List<Edge>> dag = new ArrayList<>();
        int n = 100;
        for (int i = 0; i < n; i++) dag.add(new ArrayList<>());
        for (int i = 0; i < n - 1; i++) {
            dag.get(i).add(new Edge(i + 1, 1));
        }

        DAGShortestPaths dsp = new DAGShortestPaths();
        List<Integer> topo = new ArrayList<>();
        for (int i = 0; i < n; i++) topo.add(i);
        long startTime = System.nanoTime();
        DAGShortestPaths.Result result = dsp.run(dag, 0, topo, null);
        long endTime = System.nanoTime();
        System.out.println("Performance test (100 nodes, linear DAG) completed in " + (endTime - startTime) + " ns.");
        Assert.assertTrue(endTime - startTime < 1000000000L);
    }
}
