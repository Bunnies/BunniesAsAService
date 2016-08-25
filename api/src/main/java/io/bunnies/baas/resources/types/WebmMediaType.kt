package io.bunnies.baas.resources.types

class WebmMediaType(resourceID: String, mediaBaseUrl: String) : ResourceMediaType(resourceID, mediaBaseUrl) {

    override fun constructUrl(): String {
        return this.mediaBaseUrl + "webm/" + this.resourceID + ".webm"
    }

    companion object {
        val KEY = "WEBM"
    }
}
