package io.bunnies.baas.metaformer;

import java.util.Map;

public class SpecifiedMetadata {
    private Map<String, SpecifiedBunnyResource> resources;

    public SpecifiedMetadata(Map<String, SpecifiedBunnyResource> resources) {
        this.resources = resources;
    }

    public Map<String, SpecifiedBunnyResource> getResources() {
        return this.resources;
    }
}
