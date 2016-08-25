package io.bunnies.baas.services

import io.bunnies.baas.resources.BunnyResources
import redis.clients.jedis.JedisPool

object RequestTrackerSingleton {
    private var requestTracker: RequestTracker? = null

    fun getInstance(jedisPool: JedisPool, resources: BunnyResources): RequestTracker {
        return requestTracker ?: createNewRequestTracker(jedisPool, resources)
    }

    private fun createNewRequestTracker(jedisPool: JedisPool, resources: BunnyResources): RequestTracker {
        val requestTracker = RequestTracker(jedisPool, resources)
        this.requestTracker = requestTracker

        return requestTracker
    }
}