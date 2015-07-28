package io.bunnies.baas.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class BunnyResources {
    private final Map<String, IBunnyResource> bunnyResourceMap;
    private final List<IBunnyResource> bunnyResourceList;
    private final Random random;

    private final Set<String> bunnyIds;

    public BunnyResources(String mediaBaseUrl, Set<String> bunnyIds) {
        this.bunnyIds = bunnyIds;
        this.bunnyResourceMap = Maps.newHashMap();
        this.bunnyResourceList = Lists.newArrayList();
        this.random = new Random();

        this.initialise(mediaBaseUrl);
    }

    private void initialise(String mediaBaseUrl) {
        for (String bunnyId : this.bunnyIds) {
            BunnyResource bunnyResource = new BunnyResource(mediaBaseUrl, bunnyId);

            this.bunnyResourceMap.put(bunnyId, bunnyResource);
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

    public List<IBunnyResource> getAllResources() {
        return this.bunnyResourceList;
    }
}
