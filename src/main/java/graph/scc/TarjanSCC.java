package graph.scc;
import graph.common.Edge;
import metrics.Metrics;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TarjanSCC {
    private final List<List<Edge>> adj;
    private final int n;
    private final Metrics metrics;
    private int time;
    private final int[] disc, low, compId;
    private final boolean[] inStack;
    private final Stack<Integer> st;
    private int compCount;
    private final List<List<Integer>> components;

    public TarjanSCC(List<List<Edge>> adj, Metrics metrics) {
        this.adj = adj;
        this.n = adj.size();
        this.metrics = metrics;
        disc = new int[n];
        low = new int[n];
        compId = new int[n];
        inStack = new boolean[n];
        st = new Stack<Integer>();
        components = new ArrayList<List<Integer>>();
        for (int i=0;i<n;i++){
            disc[i]=-1;
            low[i]=-1;
            compId[i]=-1;
        }
    }

    public void run() {
        for (int i=0;i<n;i++) if (disc[i]==-1) dfs(i);
    }

    private void dfs(int u){
        if (metrics!=null) metrics.inc("scc.visit");
        disc[u]=time;
        low[u]=time;
        time++;
        st.push(u);
        inStack[u]=true;
        List<Edge> list = adj.get(u);
        for (int k=0;k<list.size();k++){
            if (metrics!=null) metrics.inc("scc.edge");
            int v = list.get(k).getTo();
            if (disc[v]==-1){
                dfs(v);
                low[u]=Math.min(low[u], low[v]);
            }
            else if (inStack[v]){
                low[u]=Math.min(low[u], disc[v]);
            }
        }
        if (low[u]==disc[u]){
            List<Integer> comp = new ArrayList<Integer>();
            while (true){
                int v = st.pop();
                inStack[v]=false;
                compId[v]=compCount;
                comp.add(v);
                if (v==u) break;
            }
            components.add(comp); compCount++;
        }
    }

    public int[] getCompId(){
        return compId;
    }
    public int getCompCount(){
        return compCount;
    }
    public List<List<Integer>> getComponents(){
        return components;
    }
}
