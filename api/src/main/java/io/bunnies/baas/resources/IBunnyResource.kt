package io.bunnies.baas.resources

interface IBunnyResource {
    val bunnyID: String

    fun hasResourceType(resourceKey: String): Boolean

    val aspectRatio: Double

    fun getResourceUrl(resourceKey: String): String?

    val bunnySource: String
}
