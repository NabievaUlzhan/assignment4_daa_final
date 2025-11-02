package graph.scc;

import graph.common.Edge;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class TarjanSCCTest {
    @Test
    public void simpleCycle() {
        //simple cyclic graph: 0 -> 1 -> 2 -> 0
        List<List<Edge>> adj = new ArrayList<>();
        int n = 3;
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        adj.get(0).add(new Edge(1, 1));
        adj.get(1).add(new Edge(2, 1));
        adj.get(2).add(new Edge(0, 1));
        TarjanSCC tarjan = new TarjanSCC(adj, null);
        tarjan.run();

        Assert.assertEquals(1, tarjan.getCompCount());
        Assert.assertEquals(1, tarjan.getComponents().size());
        Assert.assertTrue(tarjan.getComponents().get(0).contains(0));
        Assert.assertTrue(tarjan.getComponents().get(0).contains(1));
        Assert.assertTrue(tarjan.getComponents().get(0).contains(2));
    }

    @Test
    public void disconnectedGraph() {
        //disconnected graph: 0 -> 1 and 2 -> 3
        List<List<Edge>> adj = new ArrayList<>();
        int n = 4;
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        adj.get(0).add(new Edge(1, 1));
        adj.get(2).add(new Edge(3, 1));
        TarjanSCC tarjan = new TarjanSCC(adj, null);
        tarjan.run();

        Assert.assertEquals(2, tarjan.getCompCount());
        Assert.assertEquals(2, tarjan.getComponents().size());
        List<Integer> firstSCC = tarjan.getComponents().get(0);
        List<Integer> secondSCC = tarjan.getComponents().get(1);
        Assert.assertTrue(firstSCC.contains(0));
        Assert.assertTrue(firstSCC.contains(1));
        Assert.assertTrue(secondSCC.contains(2));
        Assert.assertTrue(secondSCC.contains(3));
    }

    @Test
    public void singleNodeGraph() {
        //graph with a single node and no edges
        List<List<Edge>> adj = new ArrayList<>();
        adj.add(new ArrayList<>());
        TarjanSCC tarjan = new TarjanSCC(adj, null);
        tarjan.run();

        Assert.assertEquals(1, tarjan.getCompCount());
        Assert.assertEquals(1, tarjan.getComponents().size());
        Assert.assertTrue(tarjan.getComponents().get(0).contains(0));
    }

    @Test
    public void multipleSCCs() {
        //graph with multiple disconnected SCCs 0 -> 1 -> 2 and 3 -> 4
        List<List<Edge>> adj = new ArrayList<>();
        int n = 5;
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        adj.get(0).add(new Edge(1, 1));
        adj.get(1).add(new Edge(2, 1));
        adj.get(3).add(new Edge(4, 1));
        TarjanSCC tarjan = new TarjanSCC(adj, null);
        tarjan.run();

        Assert.assertEquals(3, tarjan.getCompCount());
        Assert.assertEquals(3, tarjan.getComponents().size());
        List<Integer> firstSCC = tarjan.getComponents().get(0);
        List<Integer> secondSCC = tarjan.getComponents().get(1);
        List<Integer> thirdSCC = tarjan.getComponents().get(2);
        Assert.assertTrue(firstSCC.contains(0));
        Assert.assertTrue(firstSCC.contains(1));
        Assert.assertTrue(firstSCC.contains(2));
        Assert.assertTrue(secondSCC.contains(3));
        Assert.assertTrue(thirdSCC.contains(4));
    }
}
