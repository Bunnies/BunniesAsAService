package io.bunnies.baas.resources

import io.bunnies.baas.metadata.DerivedMetadata
import io.bunnies.baas.metadata.SpecifiedMetadata

object BunnyResourcesSingleton {
    private var bunnyResources: BunnyResources? = null

    fun getInstance(mediaBaseUrl: String, derivedMetadata: DerivedMetadata, specifiedMetadata: SpecifiedMetadata): BunnyResources {
        return bunnyResources ?: createInstance(mediaBaseUrl, derivedMetadata, specifiedMetadata)
    }

    private fun createInstance(mediaBaseUrl: String, derivedMetadata: DerivedMetadata, specifiedMetadata: SpecifiedMetadata): BunnyResources {
        val bunnyResources = BunnyResources(mediaBaseUrl, derivedMetadata, specifiedMetadata)
        this.bunnyResources = bunnyResources

        return bunnyResources
    }
}
