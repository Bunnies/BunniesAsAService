package io.bunnies.baas.services.v1;

public class RequestTrackerSingleton {
    private static RequestTracker requestTracker;

    public static RequestTracker getInstance() {
        if (requestTracker == null) {
            requestTracker = new RequestTracker();
        }

        return requestTracker;
    }
}