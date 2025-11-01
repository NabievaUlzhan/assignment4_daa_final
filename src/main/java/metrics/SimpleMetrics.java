package metrics;
import java.util.HashMap;
import java.util.Map;

public class SimpleMetrics implements Metrics {
    private long start;
    private long end;
    private final Map<String, Long> counters = new HashMap<String, Long>();

    @Override public void start() {
        start = System.nanoTime();
    }
    @Override public void stop()  {
        end = System.nanoTime();
    }
    @Override public long getElapsedNanos() {
        return end - start;
    }

    @Override public void inc(String name) {
        Long v = counters.get(name);
        counters.put(name, v == null ? 1L : v + 1L);
    }
    @Override public long get(String name) {
        Long v = counters.get(name);
        return v == null ? 0L : v;
    }
}
