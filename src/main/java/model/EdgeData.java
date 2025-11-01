package model;

public class EdgeData {
    private int u;
    private int v;
    private int w;

    public EdgeData() {}
    public EdgeData(int u, int v, int w){
        this.u=u;
        this.v=v;
        this.w=w;
    }

    public int getU(){
        return u;
    }
    public int getV(){
        return v;
    }
    public int getW(){
        return w;
    }
    public void setU(int u){
        this.u=u;
    }
    public void setV(int v){
        this.v=v;
    }
    public void setW(int w){
        this.w=w;
    }
}
