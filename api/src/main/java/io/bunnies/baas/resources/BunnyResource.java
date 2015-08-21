package io.bunnies.baas.resources;

import com.google.common.collect.Maps;
import io.bunnies.baas.resources.types.GifMediaType;
import io.bunnies.baas.resources.types.Mp4MediaType;
import io.bunnies.baas.resources.types.PosterMediaType;
import io.bunnies.baas.resources.types.WebmMediaType;

import java.util.Map;

public class BunnyResource implements IBunnyResource {
    private final Map<String, String> typeUrlMap;
    private final String bunnyID;
    private final double aspectRatio;
    private final String source;

    public BunnyResource(String mediaBaseUrl, String bunnyID, double aspectRatio, String source) {
        this.bunnyID = bunnyID;
        this.aspectRatio = aspectRatio;

        if (source.isEmpty()) {
            this.source = "unknown";
        } else {
            this.source = source;
        }

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
    public double getAspectRatio() {
        return this.aspectRatio;
    }

    @Override
    public String getResourceUrl(String resourceKey) {
        return this.typeUrlMap.getOrDefault(resourceKey, null);
    }

    @Override
    public String getBunnySource() {
        return this.source;
    }
}
