package metrics;
//A common Metrics interface (operation counters + time)
public interface Metrics {
    void start();
    void stop();
    long getElapsedNanos();

    void inc(String name);
    long get(String name);
}