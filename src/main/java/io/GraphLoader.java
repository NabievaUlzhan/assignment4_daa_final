package io;
import model.EdgeData;
import model.GraphData;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

public class GraphLoader {
    public GraphData load(String path) throws Exception {
        String text = new String(Files.readAllBytes(Paths.get(path)));
        GraphData g = new GraphData();
        g.setDirected(extractBoolean(text, "\"directed\"\\s*:\\s*(true|false)"));
        g.setNodes(extractInt(text, "\"nodes\"\\s*:\\s*(\\d+)"));
        g.setSource(extractInt(text, "\"source\"\\s*:\\s*(-?\\d+)"));
        g.setModel(extractString(text, "\"model\"\\s*:\\s*\"([^\"]+)\""));
        g.setEdges(extractEdges(text));
        return g;
    }

    private boolean extractBoolean(String text, String regex){
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find() && "true".equalsIgnoreCase(m.group(1));
    }
    private int extractInt(String text, String regex){
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find()?Integer.parseInt(m.group(1)):0;
    }
    private String extractString(String text, String regex){
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find()?m.group(1):"";
    }
    private List<EdgeData> extractEdges(String text){
        List<EdgeData> list = new ArrayList<EdgeData>();
        Matcher m = Pattern.compile("\\{[^{}]*\\}").matcher(text);
        while (m.find()){
            String obj = m.group();
            boolean k1 = obj.contains("\"u\"") && obj.contains("\"v\"");
            boolean k2 = obj.contains("\"from\"") && obj.contains("\"to\"");
            if (!k1 && !k2) continue;
            int u = k1 ? extractInt(obj, "\"u\"\\s*:\\s*(-?\\d+)") : extractInt(obj, "\"from\"\\s*:\\s*(-?\\d+)");
            int v = k1 ? extractInt(obj, "\"v\"\\s*:\\s*(-?\\d+)") : extractInt(obj, "\"to\"\\s*:\\s*(-?\\d+)");
            int w = obj.contains("\"w\"")?extractInt(obj, "\"w\"\\s*:\\s*(-?\\d+)")
                    :(obj.contains("\"weight\"")?extractInt(obj, "\"weight\"\\s*:\\s*(-?\\d+)"):1);
            list.add(new EdgeData(u,v,w));
        }
        return list;
    }
}
