package io.bunnies.baas.services.v1;

public class RequestTracker {
    private int totalServed = 0;
    private int specificsServed = 0;

    public synchronized int getTotalServed() {
        return this.totalServed;
    }

    public synchronized int getSpecificsServed() {
        return this.specificsServed;
    }

    public synchronized void incrementTotalServed() {
        this.totalServed++;
    }

    public synchronized void incrementSpecificsServed() {
        this.specificsServed++;
    }
}
