package graph.common;

public class Edge {
    private int to;
    private int w;
    public Edge(int to, int w){
        this.to=to; this.w=w;
    }
    public int getTo(){
        return to;
    }
    public int getW(){
        return w;
    }
    public void setTo(int to){
        this.to=to;
    }
    public void setW(int w){
        this.w=w;
    }
}
