package io.bunnies.baas.metadata;

import java.util.Map;

public class SpecifiedMetadata {
    private Map<String, SpecifiedBunnyResourceMetadata> resources;

    public SpecifiedMetadata(Map<String, SpecifiedBunnyResourceMetadata> resources) {
        this.resources = resources;
    }

    public Map<String, SpecifiedBunnyResourceMetadata> getResources() {
        return this.resources;
    }
}
