package io.bunnies.baas.services

import com.google.common.collect.Maps
import com.google.common.collect.Sets
import io.bunnies.baas.resources.BunnyResources
import io.bunnies.baas.resources.IBunnyResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.exceptions.JedisConnectionException
import java.util.Timer
import java.util.TimerTask

class RequestTracker(private val jedisPool: JedisPool, resources: BunnyResources) {

    private lateinit var resourceCounters: MutableMap<String, Int>
    private var updatingRedis = false

    private val queuedResources: MutableSet<String>
    private val queuedResourcesLock: Any

    private lateinit var redisUpdateTimer: Timer

    init {
        this.queuedResources = Sets.newConcurrentHashSet<String>()
        this.queuedResourcesLock = Any()

        this.initialiseResourceCounters(resources)
        this.initialiseFromRedis()
        this.pushCountersToRedis()

        if (this.updatingRedis) {
            this.redisUpdateTimer = Timer()
            this.startRedisTimer()
        }
    }

    private fun initialiseResourceCounters(resources: BunnyResources) {
        this.resourceCounters = Maps.newConcurrentMap<String, Int>()

        for (resource in resources.allResources) {
            this.resourceCounters.put(resource.bunnyID, 0)
        }

        this.resourceCounters.put(TOTAL_IDENTIFIER, 0)
        this.resourceCounters.put(RANDOM_IDENTIFIER, 0)
    }

    fun initialiseFromRedis() {
        try {
            jedisPool.resource.use { jedis ->
                this.updatingRedis = true

                for (resource in this.resourceCounters.keys) {
                    val redisValue = jedis.get(this.formSpecificBunnyCountKey(resource))

                    var value = 0
                    if (redisValue != null) {
                        value = Integer.parseInt(redisValue)
                        if (value < 0) {
                            value = 0
                        }
                    }

                    this.resourceCounters.put(resource, value)
                }
            }
        } catch (e: JedisConnectionException) {
            LOGGER.warn("Could not initialise resource counters from Redis")
        }

    }

    private fun formSpecificBunnyCountKey(id: String): String {
        return String.format(REDIS_BUNNY_SPECIFIC_COUNT_KEY_FORMAT, id)
    }

    private fun pushCountersToRedis() {
        for (resource in this.resourceCounters.keys) {
            this.queuedResources.add(resource)
        }

        this.updateRedisCounts()
    }

    private fun updateRedisCounts() {
        var updatedResourcesCopy = setOf<String>()

        synchronized(this.queuedResourcesLock) {
            updatedResourcesCopy = Sets.newHashSet(this.queuedResources)
            this.queuedResources.clear()
        }

        try {
            this.jedisPool.resource.use { jedis ->
                for (resource in updatedResourcesCopy) {
                    val resourceCounterValue = this.resourceCounters[resource] ?: 0
                    val status = jedis.set(this.formSpecificBunnyCountKey(resource), "$resourceCounterValue")
                    LOGGER.info("$resource -> $resourceCounterValue: $status")
                }
            }
        } catch (e: JedisConnectionException) {
            LOGGER.warn("Could not update Redis counts")
        }

    }

    private fun startRedisTimer() {
        this.redisUpdateTimer.schedule(UpdateRedisTask(), REDIS_UPDATE_INTERVAL_MS.toLong())
    }

    internal inner class UpdateRedisTask : TimerTask() {
        override fun run() {
            updateRedisCounts()
            startRedisTimer()
        }
    }

    // IRequestTracker

    @Synchronized fun getServed(id: String): Int {
        return resourceCounters[id] ?: 0
    }

    @Synchronized fun incrementServedAndTotal(id: String) {
        this.resourceCounters[TOTAL_IDENTIFIER] = (resourceCounters[TOTAL_IDENTIFIER] ?: 0) + 1
        this.resourceCounters[id] = (resourceCounters[id] ?: 0) + 1

        synchronized(this.queuedResourcesLock) {
            this.queuedResources.add(TOTAL_IDENTIFIER)
            this.queuedResources.add(id)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RequestTracker::class.java)

        private val REDIS_BUNNY_SPECIFIC_COUNT_KEY_FORMAT = "bunnies.%s.count"

        var TOTAL_IDENTIFIER = "total"
        var RANDOM_IDENTIFIER = "random"

        private val REDIS_UPDATE_INTERVAL_MS = 1 * 1000
    }
}
