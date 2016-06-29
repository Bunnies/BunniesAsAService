package io.bunnies.baas.metaformer

import java.io.File

data class ResourceType(val id: String, val fileSuffix: String, val resourceIds: MutableSet<String> = mutableSetOf(), val resourceFiles: MutableMap<String, File> = mutableMapOf())