package io.bunnies.baas.metadata;

import java.util.Map;

public class DerivedMetadata {
    private Map<String, DerivedBunnyResourceMetadata> resources;

    public DerivedMetadata(Map<String, DerivedBunnyResourceMetadata> resources) {
        this.resources = resources;
    }

    public Map<String, DerivedBunnyResourceMetadata> getResources() {
        return this.resources;
    }
}
