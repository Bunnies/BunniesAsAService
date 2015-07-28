package io.bunnies.baas.metadata;

import java.util.Map;

public class DerivedMetadata {
    private Map<String, BunnyResourceMetadata> resources;

    public DerivedMetadata(Map<String, BunnyResourceMetadata> resources) {
        this.resources = resources;
    }

    public Map<String, BunnyResourceMetadata> getResources() {
        return this.resources;
    }
}
