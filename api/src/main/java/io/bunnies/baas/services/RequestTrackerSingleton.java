package io.bunnies.baas.services;

import io.bunnies.baas.resources.BunnyResources;
import redis.clients.jedis.JedisPool;

public class RequestTrackerSingleton {
    private static RequestTracker requestTracker;

    public static RequestTracker getInstance(JedisPool jedisPool, BunnyResources resources) {
        if (requestTracker == null) {
            requestTracker = new RequestTracker(jedisPool, resources);
        }

        return requestTracker;
    }
}