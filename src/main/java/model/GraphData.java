package model;
import java.util.ArrayList;
import java.util.List;

public class GraphData {
    private boolean directed;
    private int nodes;
    private int source;
    private String model;
    private List<EdgeData> edges = new ArrayList<EdgeData>();

    public boolean isDirected(){
        return directed;
    }
    public int getNodes(){
        return nodes;
    }
    public int getSource(){
        return source;
    }
    public String getModel(){
        return model;
    }
    public List<EdgeData> getEdges(){
        return edges;
    }

    public void setDirected(boolean directed){
        this.directed=directed;
    }
    public void setNodes(int nodes){
        this.nodes=nodes;
    }
    public void setSource(int source){
        this.source=source;
    }
    public void setModel(String model){
        this.model=model;
    }
    public void setEdges(List<EdgeData> edges){
        this.edges=edges;
    }
}
