package io.bunnies.baas.resources;

import com.google.common.collect.Maps;
import io.bunnies.baas.resources.types.GifMediaType;
import io.bunnies.baas.resources.types.Mp4MediaType;
import io.bunnies.baas.resources.types.PosterMediaType;
import io.bunnies.baas.resources.types.WebmMediaType;

import java.util.Map;

public class BunnyResource implements IBunnyResource {
    private Map<String, String> typeUrlMap;
    private String bunnyID;

    public BunnyResource(String mediaBaseUrl, String bunnyID) {
        this.bunnyID = bunnyID;

        this.typeUrlMap = Maps.newHashMap();

        this.typeUrlMap.put(GifMediaType.KEY, new GifMediaType(this.bunnyID, mediaBaseUrl).constructUrl());
        this.typeUrlMap.put(WebmMediaType.KEY, new WebmMediaType(this.bunnyID, mediaBaseUrl).constructUrl());
        this.typeUrlMap.put(Mp4MediaType.KEY, new Mp4MediaType(this.bunnyID, mediaBaseUrl).constructUrl());
        this.typeUrlMap.put(PosterMediaType.KEY, new PosterMediaType(this.bunnyID, mediaBaseUrl).constructUrl());
    }

    // IBunnyResource

    @Override
    public String getBunnyID() {
        return this.bunnyID;
    }

    @Override
    public boolean hasResourceType(String resourceKey) {
        return this.typeUrlMap.containsKey(resourceKey);
    }

    @Override
    public String getResourceUrl(String resourceKey) {
        return this.typeUrlMap.getOrDefault(resourceKey, null);
    }
}
