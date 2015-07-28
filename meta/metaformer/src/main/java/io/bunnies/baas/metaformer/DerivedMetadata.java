package io.bunnies.baas.metaformer;

import java.util.Map;

public class DerivedMetadata {
    private Map<String, BunnyResource> resources;

    public DerivedMetadata(Map<String, BunnyResource> resources) {
        this.resources = resources;
    }
}
