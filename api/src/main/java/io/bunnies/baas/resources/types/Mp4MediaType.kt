package io.bunnies.baas.resources.types

class Mp4MediaType(resourceID: String, mediaBaseUrl: String) : ResourceMediaType(resourceID, mediaBaseUrl) {

    override fun constructUrl(): String {
        return this.mediaBaseUrl + "mp4/" + this.resourceID + ".mp4"
    }

    companion object {
        val KEY = "MP4"
    }
}
