package graph.dagsp;
import graph.common.Edge;
import metrics.Metrics;
import java.util.ArrayList;
import java.util.List;

public class DAGShortestPaths {
    public static class Result {
        public int[] dist;
        public int[] prev;
    }

    public Result run(List<List<Edge>> dag, int sourceComp, List<Integer> topo, Metrics metrics){
        int n = dag.size();
        int[] dist = new int[n], prev = new int[n];
        for (int i=0;i<n;i++){
            dist[i]=Integer.MAX_VALUE/4;
            prev[i]=-1;
        }
        dist[sourceComp]=0;

        for (int k=0;k<topo.size();k++){
            int u = topo.get(k);
            if (dist[u] >= Integer.MAX_VALUE/8) continue;
            List<Edge> list = dag.get(u);
            for (int t=0;t<list.size();t++){
                Edge e = list.get(t);
                int v=e.getTo(), nd=dist[u]+e.getW();
                if (nd<dist[v]){
                    dist[v]=nd;
                    prev[v]=u;
                    if (metrics!=null) metrics.inc("dagsp.relax"); }
            }
        }
        Result r=new Result();
        r.dist=dist;
        r.prev=prev;
        return r;
    }

    public List<Integer> reconstructPath(int targetComp, int[] prev){
        List<Integer> path = new ArrayList<Integer>();
        int cur=targetComp;
        while (cur!=-1){
            path.add(0, cur);
            cur=prev[cur];
        }
        return path;
    }
}
