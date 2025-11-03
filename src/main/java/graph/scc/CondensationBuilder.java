package graph.scc;
import graph.common.Edge;
import model.EdgeData;
import java.util.ArrayList;
import java.util.List;

//Build the condensation graph (a DAG of components)
public class CondensationBuilder {
    public List<List<Edge>> build(int n, List<EdgeData> edges, int[] compId, int compCount, boolean directed){
        List<List<Edge>> dag = new ArrayList<List<Edge>>();
        for (int i=0;i<compCount;i++) dag.add(new ArrayList<Edge>());
        for (int j=0;j<edges.size();j++){
            EdgeData ed = edges.get(j);
            int cu = compId[ed.getU()], cv = compId[ed.getV()];
            if (cu!=cv) dag.get(cu).add(new Edge(cv, ed.getW()));
            if (!directed && cu!=cv) dag.get(cv).add(new Edge(cu, ed.getW()));
        }
        return dag;
    }
}
