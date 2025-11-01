package metrics;

public interface Metrics {
    void start();
    void stop();
    long getElapsedNanos();

    void inc(String name);
    long get(String name);
}