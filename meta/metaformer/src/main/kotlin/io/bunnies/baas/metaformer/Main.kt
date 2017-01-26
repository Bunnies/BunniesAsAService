package io.bunnies.baas.metaformer

import com.google.common.base.Charsets
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileFilter
import java.io.IOException
import java.lang.reflect.Type
import java.nio.file.*

object Main {
    private val LOGGER = LoggerFactory.getLogger(Main::class.java)

    private val USAGE = "java -jar <jar file> <absolute media directory>"
    private val MIN_WIDTH = 50
    private val MIN_HEIGHT = 50

    @JvmStatic fun main(args: Array<String>) {
        if (args.size < 1) {
            LOGGER.error(USAGE)

            return
        }

        val path: Path

        try {
            path = Paths.get(args[0])
        } catch (e: InvalidPathException) {
            LOGGER.error("Invalid media path: {}", args[0])

            return
        }

        val gifResources = ResourceType("gif", "gif")
        val posterResources = ResourceType("poster", "png")
        val webmResources = ResourceType("webm", "webm")
        val mp4Resources = ResourceType("mp4", "mp4")

        val resourceTypes = Lists.newArrayList(gifResources, posterResources, webmResources, mp4Resources)

        for (resourceType in resourceTypes) {
            loadResourcesOfType(path, resourceType)
        }

        LOGGER.info("Found: ")
        LOGGER.info(" ${gifResources.resourceIds.size} gifs: ${gifResources.resourceIds}")
        LOGGER.info(" ${posterResources.resourceIds.size} posters: ${posterResources.resourceIds}")
        LOGGER.info(" ${webmResources.resourceIds.size} webms: ${webmResources.resourceIds}")
        LOGGER.info(" ${mp4Resources.resourceIds.size} mp4s: ${mp4Resources.resourceIds}")

        val resources = Maps.newHashMap<String, DerivedBunnyResource>()
        val specifiedResources = Maps.newHashMap<String, SpecifiedBunnyResource>()

        for (id in gifResources.resourceIds) {
            val image = try {
                LOGGER.info("Reading ${posterResources.id}: $id")

                ImageIO.read(posterResources.resourceFiles[id])
            } catch (e: IOException) {
                LOGGER.error("Failed to read '$id': $e")

                return
            }

            val width = image.width
            val height = image.height
            val aspect_ratio = width.toDouble() / height

            if (width < MIN_WIDTH || height < MIN_HEIGHT || aspect_ratio < 0) {
                LOGGER.error("Failed to read '$width': $height: $aspect_ratio")

                return
            }

            val sizes = mutableMapOf<String, Long>()

            val gif = gifResources.resourceFiles[id]
            val webm = webmResources.resourceFiles[id]
            val mp4 = mp4Resources.resourceFiles[id]

            if(gif != null) { sizes += ("gif" to gif.length()) }
            if(webm != null) { sizes += ("webm" to webm.length()) }
            if(mp4 != null) { sizes += ("mp4" to mp4.length()) }

            resources.put(id, DerivedBunnyResource(width, height, aspect_ratio, sizes))
            specifiedResources.put(id, SpecifiedBunnyResource(""))
        }

        val derivedMetadata = DerivedMetadata(resources)

        val gson = GsonBuilder().setPrettyPrinting().create()

        LOGGER.info("Attempting to load existing specified metadata...")

        try {
            val existingSpecifiedMetadataString = String(Files.readAllBytes(Paths.get("").resolve("specified_metadata.json")))

            LOGGER.info("Loaded existing specified metadata file successfully")
            val existingSpecifiedMetadata = gson.fromJson<SpecifiedMetadata>(existingSpecifiedMetadataString, SpecifiedMetadata::class.java)

            if (existingSpecifiedMetadata.resources.size > 0) {
                specifiedResources.putAll(existingSpecifiedMetadata.resources)

                LOGGER.info("Loaded existing specified metadata successfully")
            }
        } catch (e: IOException) {
            LOGGER.warn("Couldn't find or load existing specified metadata, starting from scratch")
        }

        val specifiedMetadata = SpecifiedMetadata(specifiedResources)

        val derivedMetadataString = gson.toJson(derivedMetadata)
        val specifiedMetadataString = gson.toJson(specifiedMetadata)

        LOGGER.info("Writing derived_metadata.json")

        try {
            Files.write(Paths.get("").resolve("derived_metadata.json"), derivedMetadataString.toByteArray(Charsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
        } catch (e: IOException) {
            LOGGER.error("Failed to write out derived_metadata.json: $e")
        }

        LOGGER.info("Writing specified_metadata.json")

        try {
            Files.write(Paths.get("").resolve("specified_metadata.json"), specifiedMetadataString.toByteArray(Charsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
        } catch (e: IOException) {
            LOGGER.error("Failed to write out derived_metadata.json: $e")
        }

    }

    private fun loadResourcesOfType(basePath: Path, resourceType: ResourceType) {
        val resourcePath = basePath.resolve(resourceType.id)
        val resourceFolder = resourcePath.toFile()
        if (resourceFolder == null || !resourceFolder.isDirectory) {
            LOGGER.error("Folder for resource type '${resourceType.id}' not found")

            return
        }

        val resourceFiles = getFilesOfType(resourceFolder, resourceType.fileSuffix)
        val suffixLength = resourceType.fileSuffix.length
        for (resourceFile in resourceFiles) {
            val rawResourceName = resourceFile.name
            val resourceId = rawResourceName.substring(0, rawResourceName.length - suffixLength - 1).toLowerCase()

            resourceType.resourceIds += resourceId
            resourceType.resourceFiles += resourceId to resourceFile
        }
    }

    private fun getFilesOfType(folder: File, fileType: String): Array<File> {
        return folder.listFiles { pathname -> pathname.toString().toLowerCase().endsWith("." + fileType) }
    }
}
