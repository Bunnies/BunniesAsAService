package io.bunnies.baas.services;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.bunnies.baas.resources.BunnyResources;
import io.bunnies.baas.resources.IBunnyResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class RequestTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTracker.class);

    private Map<String, Integer> resourceCounters;

    private JedisPool jedisPool;

    private static String REDIS_BUNNY_SPECIFIC_COUNT_KEY_FORMAT = "bunnies.%s.count";

    public static String TOTAL_IDENTIFIER = "total";
    public static String RANDOM_IDENTIFIER = "random";

    private static int REDIS_UPDATE_INTERVAL_MS = 1 * 1000;
    private boolean updatingRedis = false;

    private Set<String> queuedResources;
    private final Object queuedResourcesLock;

    private Timer redisUpdateTimer;

    public RequestTracker(JedisPool jedisPool, BunnyResources resources) {
        this.jedisPool = jedisPool;
        this.queuedResources = Sets.newConcurrentHashSet();
        this.queuedResourcesLock = new Object();

        this.initialiseResourceCounters(resources);
        this.initialiseFromRedis();
        this.pushCountersToRedis();

        if (this.updatingRedis) {
            this.redisUpdateTimer = new Timer();
            this.startRedisTimer();
        }
    }

    private void initialiseResourceCounters(BunnyResources resources) {
        this.resourceCounters = Maps.newConcurrentMap();

        for (IBunnyResource resource : resources.getAllResources()) {
            this.resourceCounters.put(resource.getBunnyID(), 0);
        }

        this.resourceCounters.put(TOTAL_IDENTIFIER, 0);
        this.resourceCounters.put(RANDOM_IDENTIFIER, 0);
    }

    public void initialiseFromRedis() {
        try (Jedis jedis = jedisPool.getResource()) {
            this.updatingRedis = true;

            for (String resource : this.resourceCounters.keySet()) {
                String redisValue = jedis.get(this.formSpecificBunnyCountKey(resource));

                int value = 0;
                if (redisValue != null) {
                    value = Integer.parseInt(redisValue);
                    if (value < 0) {
                        value = 0;
                    }
                }

                this.resourceCounters.put(resource, value);
            }
        } catch (JedisConnectionException e) {
            LOGGER.warn("Could not initialise resource counters from Redis");
        }
    }

    private String formSpecificBunnyCountKey(String id) {
        return String.format(REDIS_BUNNY_SPECIFIC_COUNT_KEY_FORMAT, id);
    }

    private void pushCountersToRedis() {
        for (String resource : this.resourceCounters.keySet()) {
            this.queuedResources.add(resource);
        }

        this.updateRedisCounts();
    }

    private void updateRedisCounts() {
        Set<String> updatedResourcesCopy;

        synchronized (this.queuedResourcesLock) {
            updatedResourcesCopy = Sets.newHashSet(this.queuedResources);
            this.queuedResources.clear();
        }

        try (Jedis jedis = this.jedisPool.getResource()) {
            for (String resource : updatedResourcesCopy) {
                String resourceCounterValue = this.resourceCounters.getOrDefault(resource, 0).toString();
                String status = jedis.set(this.formSpecificBunnyCountKey(resource), resourceCounterValue);
                LOGGER.info(resource + " -> " + resourceCounterValue + ": " + status);
            }
        } catch (JedisConnectionException e) {
            LOGGER.warn("Could not update Redis counts");
        }
    }

    private void startRedisTimer() {
        this.redisUpdateTimer.schedule(new UpdateRedisTask(), REDIS_UPDATE_INTERVAL_MS);
    }

    class UpdateRedisTask extends TimerTask {
        @Override
        public void run() {
            updateRedisCounts();
            startRedisTimer();
        }
    }

    // IRequestTracker

    public synchronized int getServed(String id) {
        return this.resourceCounters.getOrDefault(id, 0);
    }

    public synchronized void incrementServedAndTotal(String id) {
        this.resourceCounters.put(TOTAL_IDENTIFIER, this.resourceCounters.getOrDefault(TOTAL_IDENTIFIER, 0) + 1);
        this.resourceCounters.put(id, this.resourceCounters.getOrDefault(id, 0) + 1);

        synchronized (this.queuedResourcesLock) {
            this.queuedResources.add(TOTAL_IDENTIFIER);
            this.queuedResources.add(id);
        }
    }
}
