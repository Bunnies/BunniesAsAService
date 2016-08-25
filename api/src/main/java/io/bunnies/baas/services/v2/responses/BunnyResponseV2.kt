package io.bunnies.baas.services.v2.responses

import com.google.common.collect.Maps
import io.bunnies.baas.resources.IBunnyResource

class BunnyResponseV2(bunnyResource: IBunnyResource, fileTypeKeys: Iterable<String>, var thisServed: Int, var totalServed: Int) {
    val id: String
    val media = mutableMapOf<String, String>()
    val source: String

    init {
        this.id = bunnyResource.bunnyID
        this.source = bunnyResource.bunnySource

        for (fileType in fileTypeKeys) {
            this.addMediaType(bunnyResource, fileType)
        }
    }

    fun addMediaType(bunnyResource: IBunnyResource, type: String) {
        val resourceUrl = bunnyResource.getResourceUrl(type)
        if (resourceUrl != null) {
            media[type.toLowerCase()] = resourceUrl
        }
    }

    fun numberOfContainedFileTypes(): Int {
        return media.keys.size
    }
}
