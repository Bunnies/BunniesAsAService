package io.bunnies.baas.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BunnyResources {
    private Map<String, IBunnyResource> bunnyResourceMap;
    private List<IBunnyResource> bunnyResourceList;
    private Random random;

    private int minBunnyID;
    private int maxBunnyID;

    public BunnyResources(String mediaBaseUrl, int minBunnyID, int maxBunnyID) {
        this.minBunnyID = minBunnyID;
        this.maxBunnyID = maxBunnyID;

        this.bunnyResourceMap = Maps.newHashMap();
        this.bunnyResourceList = Lists.newArrayList();
        this.random = new Random();

        this.initialise(mediaBaseUrl);
    }

    private void initialise(String mediaBaseUrl) {
        for (int i = minBunnyID; i <= maxBunnyID; i++) {
            String bunnyID = Integer.toString(i);
            BunnyResource bunnyResource = new BunnyResource(mediaBaseUrl, bunnyID);

            this.bunnyResourceMap.put(bunnyID, bunnyResource);
            this.bunnyResourceList.add(bunnyResource);
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
}
