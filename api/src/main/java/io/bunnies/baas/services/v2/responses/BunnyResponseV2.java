package io.bunnies.baas.services.v2.responses;

import com.google.common.collect.Maps;
import io.bunnies.baas.resources.IBunnyResource;

import java.util.Map;

public class BunnyResponseV2 {
    public String id;
    public Map<String, String> media;
    public int totalServed;

    public BunnyResponseV2(IBunnyResource bunnyResource, Iterable<String> fileTypeKeys, int totalServed) {
        this.id = bunnyResource.getBunnyID();
        this.media = Maps.newHashMap();
        this.totalServed = totalServed;

        for (String fileType : fileTypeKeys) {
            this.addMediaType(bunnyResource, fileType);
        }
    }

    public void addMediaType(IBunnyResource bunnyResource, String type) {
        if (bunnyResource.hasResourceType(type)) {
            this.media.put(type.toLowerCase(), bunnyResource.getResourceUrl(type));
        }
    }

    public int numberOfContainedFileTypes() {
        if (this.media == null) {
            return 0;
        }

        return this.media.keySet().size();
    }
}
