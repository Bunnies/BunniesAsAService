package io.bunnies.baas.resources.types

class PosterMediaType(resourceID: String, mediaBaseUrl: String) : ResourceMediaType(resourceID, mediaBaseUrl) {

    override fun constructUrl(): String {
        return this.mediaBaseUrl + "poster/" + this.resourceID + ".png"
    }

    companion object {
        val KEY = "POSTER"
    }
}
