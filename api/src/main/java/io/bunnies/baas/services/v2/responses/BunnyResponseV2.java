package io.bunnies.baas.services.v2.responses;

import com.google.common.collect.Maps;
import io.bunnies.baas.resources.IBunnyResource;

import java.util.Map;

public class BunnyResponseV2 {
    public String id;
    public Map<String, String> media;
    public String source;
    public int thisServed;
    public int totalServed;

    public BunnyResponseV2(IBunnyResource bunnyResource, Iterable<String> fileTypeKeys, int thisServed, int totalServed) {
        this.id = bunnyResource.getBunnyID();
        this.media = Maps.newHashMap();
        this.source = bunnyResource.getBunnySource();
        this.totalServed = totalServed;
        this.thisServed = thisServed;

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
