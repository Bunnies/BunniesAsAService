package io.bunnies.baas.resources

import com.google.common.collect.Maps
import io.bunnies.baas.resources.types.GifMediaType
import io.bunnies.baas.resources.types.Mp4MediaType
import io.bunnies.baas.resources.types.PosterMediaType
import io.bunnies.baas.resources.types.WebmMediaType

class BunnyResource(mediaBaseUrl: String, override val bunnyID: String, override val aspectRatio: Double, source: String) : IBunnyResource {
    private val typeUrlMap: MutableMap<String, String>
    override val bunnySource: String

    init {

        if (source.isEmpty()) {
            this.bunnySource = "unknown"
        } else {
            this.bunnySource = source
        }

        this.typeUrlMap = Maps.newHashMap<String, String>()

        this.typeUrlMap.put(GifMediaType.KEY, GifMediaType(this.bunnyID, mediaBaseUrl).constructUrl())
        this.typeUrlMap.put(WebmMediaType.KEY, WebmMediaType(this.bunnyID, mediaBaseUrl).constructUrl())
        this.typeUrlMap.put(Mp4MediaType.KEY, Mp4MediaType(this.bunnyID, mediaBaseUrl).constructUrl())
        this.typeUrlMap.put(PosterMediaType.KEY, PosterMediaType(this.bunnyID, mediaBaseUrl).constructUrl())
    }

    override fun hasResourceType(resourceKey: String): Boolean {
        return this.typeUrlMap.containsKey(resourceKey)
    }

    override fun getResourceUrl(resourceKey: String): String? {
        return this.typeUrlMap[resourceKey]
    }
}
