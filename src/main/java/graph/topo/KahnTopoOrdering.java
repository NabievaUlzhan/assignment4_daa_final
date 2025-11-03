package graph.topo;

import graph.common.Edge;
import metrics.Metrics;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class KahnTopoOrdering {
    public List<Integer> order(List<List<Edge>> adj, Metrics metrics){
        int n = adj.size();
        //Compute a topological order of the condensation DAG
        int[] indeg = new int[n];
        for (int i=0;i<n;i++){
            List<Edge> list = adj.get(i);
            for (int k=0;k<list.size();k++)
                indeg[list.get(k).getTo()]++;
        }

        Deque<Integer> dq = new ArrayDeque<Integer>();
        for (int i=0;i<n;i++) if (indeg[i]==0){
            dq.add(i);
            if (metrics!=null) metrics.inc("kahn.push");
        }

        List<Integer> topo = new ArrayList<Integer>();
        while (!dq.isEmpty()){
            int u = dq.removeFirst();
            if (metrics!=null) metrics.inc("kahn.pop");
            topo.add(u);
            List<Edge> list = adj.get(u);
            for (int k=0;k<list.size();k++){
                int v = list.get(k).getTo();
                if (--indeg[v]==0){
                    dq.add(v);
                    if (metrics!=null) metrics.inc("kahn.push");
                }
            }
        }
        return topo;
    }
}
