package io.bunnies.baas.resources.types

class GifMediaType(resourceID: String, mediaBaseUrl: String) : ResourceMediaType(resourceID, mediaBaseUrl) {

    override fun constructUrl(): String {
        return this.mediaBaseUrl + "gif/" + this.resourceID + ".gif"
    }

    companion object {
        val KEY = "GIF"
    }
}
