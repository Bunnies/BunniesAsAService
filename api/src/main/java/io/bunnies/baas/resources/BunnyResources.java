package io.bunnies.baas.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.bunnies.baas.metadata.DerivedMetadata;
import io.bunnies.baas.metadata.SpecifiedMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class BunnyResources {
    private final Map<String, IBunnyResource> bunnyResourceMap;
    private final List<IBunnyResource> bunnyResourceList;
    private final Random random;

    private final DerivedMetadata derivedMetadata;
    private final SpecifiedMetadata specifiedMetadata;
    private final Set<String> bunnyIds;
    private final List<IBunnyResource> aspectRatiosSorted;

    private static final double ASPECT_RATIO_FUZZ = 0.1;

    private final Logger LOGGER = LoggerFactory.getLogger(BunnyResources.class);

    public BunnyResources(String mediaBaseUrl, DerivedMetadata derivedMetadata, SpecifiedMetadata specifiedMetadata) {
        this.derivedMetadata = derivedMetadata;
        this.specifiedMetadata = specifiedMetadata;
        this.bunnyIds = this.derivedMetadata.getResources().keySet();
        this.bunnyResourceMap = Maps.newHashMap();
        this.bunnyResourceList = Lists.newArrayList();
        this.aspectRatiosSorted = Lists.newArrayList();

        this.random = new Random();

        this.initialise(mediaBaseUrl, derivedMetadata, specifiedMetadata);
        Collections.sort(this.aspectRatiosSorted, new Comparator<IBunnyResource>() {
            @Override
            public int compare(IBunnyResource o1, IBunnyResource o2) {
                return Double.compare(o1.getAspectRatio(), o2.getAspectRatio());
            }
        });
    }

    private void initialise(String mediaBaseUrl, DerivedMetadata derivedMetadata, SpecifiedMetadata specifiedMetadata) {
        for (String bunnyId : this.bunnyIds) {
            BunnyResource bunnyResource = new BunnyResource(mediaBaseUrl, bunnyId, derivedMetadata.getResources().get(bunnyId).getAspectRatio(), specifiedMetadata.getResources().get(bunnyId).getSource());

            this.bunnyResourceMap.put(bunnyId, bunnyResource);
            this.bunnyResourceList.add(bunnyResource);
            this.aspectRatiosSorted.add(bunnyResource);
        }
    }

    @Nullable
    public IBunnyResource getSpecificBunnyResource(String bunnyID) {
        return this.bunnyResourceMap.getOrDefault(bunnyID, null);
    }

    @Nonnull
    public IBunnyResource getRandomBunnyResource() {
        int numberOfBunnies = this.bunnyResourceList.size();
        int randomBunnyID = this.random.nextInt(numberOfBunnies);

        return this.bunnyResourceList.get(randomBunnyID);
    }

    @Nonnull
    public IBunnyResource getRandomBunnyResource(int widthHint, int heightHint) {
        double aspectRatio = ((double) widthHint) / heightHint;

        LOGGER.info("Finding resource closest to aspect ratio {}", aspectRatio);

        final int[] comparisons = {0};

        int index = this.binarySearchForAspectRatio(aspectRatio);

        if (index >= 0) {
            List<IBunnyResource> resources = Lists.newArrayList(this.aspectRatiosSorted.get(index));

            int leftIndex = index - 1;
            while (leftIndex >= 0) {
                IBunnyResource leftResource = this.aspectRatiosSorted.get(leftIndex);
                if (this.doesResourceMatchAspectRatio(leftResource, aspectRatio)) {
                    resources.add(leftResource);
                } else {
                    break;
                }

                leftIndex--;
            }

            int rightIndex = index + 1;
            int aspectRatiosSize = this.aspectRatiosSorted.size();
            while (rightIndex < aspectRatiosSize) {
                IBunnyResource rightResource = this.aspectRatiosSorted.get(rightIndex);
                if (this.doesResourceMatchAspectRatio(rightResource, aspectRatio)) {
                    resources.add(rightResource);
                } else {
                    break;
                }

                rightIndex++;
            }

            int matchedResources = resources.size();
            IBunnyResource resource = resources.get(this.random.nextInt(matchedResources));
            LOGGER.info("Found {} bunnies with aspect ratio {} in {} comparisons", matchedResources, resource.getAspectRatio(), comparisons);

            return resource;
        }

        LOGGER.info("Failed to find suitable bunny in {} comparisons", comparisons);
        return this.getRandomBunnyResource();
    }

    private int binarySearchForAspectRatio(double aspectRatio) {
        int low = 0;
        int high = this.aspectRatiosSorted.size() - 1;

        int mid = 0;
        while (low <= high) {
            mid = (low + high) >>> 1;
            IBunnyResource midVal = this.aspectRatiosSorted.get(mid);

            int comparison;
            double midValAspectRatio = midVal.getAspectRatio();
            if (this.doesResourceMatchAspectRatio(midVal, aspectRatio)) {
                comparison = 0;
            } else {
                comparison = Double.compare(midValAspectRatio, aspectRatio);
            }

            if (comparison < 0) {
                low = mid + 1;
            } else if (comparison > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }

        // Return last closest bunny
        return mid;
    }

    private boolean doesResourceMatchAspectRatio(IBunnyResource resource, double aspectRatio) {
        double upper = aspectRatio + ASPECT_RATIO_FUZZ;
        double lower = aspectRatio - ASPECT_RATIO_FUZZ;

        double resourceAspectRatio = resource.getAspectRatio();

        return (resourceAspectRatio >= lower && resourceAspectRatio <= upper);
    }

    public List<IBunnyResource> getAllResources() {
        return this.bunnyResourceList;
    }
}
