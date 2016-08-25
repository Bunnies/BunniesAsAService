package io.bunnies.baas.resources.types

abstract class ResourceMediaType(protected val resourceID: String, protected val mediaBaseUrl: String) {

    abstract fun constructUrl(): String
}
