package io.bunnies.baas.resources

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import io.bunnies.baas.metadata.DerivedMetadata
import io.bunnies.baas.metadata.SpecifiedMetadata
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class BunnyResources(mediaBaseUrl: String, private val derivedMetadata: DerivedMetadata, private val specifiedMetadata: SpecifiedMetadata) {
    private val bunnyResourceMap: MutableMap<String, IBunnyResource>
    private val bunnyResourceList: MutableList<IBunnyResource>
    private val random: Random
    private val bunnyIds: Set<String>
    private val aspectRatiosSorted: MutableList<IBunnyResource>

    private val LOGGER = LoggerFactory.getLogger(BunnyResources::class.java)

    init {
        this.bunnyIds = this.derivedMetadata.resources.keys
        this.bunnyResourceMap = Maps.newHashMap<String, IBunnyResource>()
        this.bunnyResourceList = Lists.newArrayList<IBunnyResource>()
        this.aspectRatiosSorted = Lists.newArrayList<IBunnyResource>()

        this.random = Random()

        this.initialise(mediaBaseUrl, derivedMetadata, specifiedMetadata)
        Collections.sort(this.aspectRatiosSorted) { o1, o2 -> java.lang.Double.compare(o1.aspectRatio, o2.aspectRatio) }
    }

    private fun initialise(mediaBaseUrl: String, derivedMetadata: DerivedMetadata, specifiedMetadata: SpecifiedMetadata) {
        for (bunnyId in this.bunnyIds) {
            val aspectRatio = derivedMetadata.resources[bunnyId]?.aspect_ratio ?: 0.0
            val source = specifiedMetadata.resources[bunnyId]?.source ?: ""
            val bunnyResource = BunnyResource(mediaBaseUrl, bunnyId, aspectRatio, source)

            this.bunnyResourceMap.put(bunnyId, bunnyResource)
            this.bunnyResourceList.add(bunnyResource)
            this.aspectRatiosSorted.add(bunnyResource)
        }
    }

    fun getSpecificBunnyResource(bunnyID: String): IBunnyResource? {
        return bunnyResourceMap[bunnyID]
    }

    val randomBunnyResource: IBunnyResource
        get() {
            val numberOfBunnies = this.bunnyResourceList.size
            val randomBunnyID = this.random.nextInt(numberOfBunnies)

            return this.bunnyResourceList[randomBunnyID]
        }

    fun getRandomBunnyResource(widthHint: Int, heightHint: Int): IBunnyResource {
        val aspectRatio = widthHint.toDouble() / heightHint

        LOGGER.info("Finding resource closest to aspect ratio {}", aspectRatio)

        val comparisons = intArrayOf(0)

        val index = this.binarySearchForAspectRatio(aspectRatio)

        if (index >= 0) {
            val resources = Lists.newArrayList(this.aspectRatiosSorted[index])

            var leftIndex = index - 1
            while (leftIndex >= 0) {
                val leftResource = this.aspectRatiosSorted[leftIndex]
                if (this.doesResourceMatchAspectRatio(leftResource, aspectRatio)) {
                    resources.add(leftResource)
                } else {
                    break
                }

                leftIndex--
            }

            var rightIndex = index + 1
            val aspectRatiosSize = this.aspectRatiosSorted.size
            while (rightIndex < aspectRatiosSize) {
                val rightResource = this.aspectRatiosSorted[rightIndex]
                if (this.doesResourceMatchAspectRatio(rightResource, aspectRatio)) {
                    resources.add(rightResource)
                } else {
                    break
                }

                rightIndex++
            }

            val matchedResources = resources.size
            val resource = resources[this.random.nextInt(matchedResources)]
            LOGGER.info("Found {} bunnies with aspect ratio {} in {} comparisons", matchedResources, resource.aspectRatio, comparisons)

            return resource
        }

        LOGGER.info("Failed to find suitable bunny in {} comparisons", comparisons)
        return this.randomBunnyResource
    }

    private fun binarySearchForAspectRatio(aspectRatio: Double): Int {
        var low = 0
        var high = this.aspectRatiosSorted.size - 1

        var mid = 0
        while (low <= high) {
            mid = (low + high).ushr(1)
            val midVal = this.aspectRatiosSorted[mid]

            val comparison: Int
            val midValAspectRatio = midVal.aspectRatio
            if (this.doesResourceMatchAspectRatio(midVal, aspectRatio)) {
                comparison = 0
            } else {
                comparison = java.lang.Double.compare(midValAspectRatio, aspectRatio)
            }

            if (comparison < 0) {
                low = mid + 1
            } else if (comparison > 0) {
                high = mid - 1
            } else {
                return mid
            }
        }

        // Return last closest bunny
        return mid
    }

    private fun doesResourceMatchAspectRatio(resource: IBunnyResource, aspectRatio: Double): Boolean {
        val upper = aspectRatio + ASPECT_RATIO_FUZZ
        val lower = aspectRatio - ASPECT_RATIO_FUZZ

        val resourceAspectRatio = resource.aspectRatio

        return resourceAspectRatio >= lower && resourceAspectRatio <= upper
    }

    val allResources: List<IBunnyResource>
        get() = this.bunnyResourceList

    companion object {

        private val ASPECT_RATIO_FUZZ = 0.1
    }
}
