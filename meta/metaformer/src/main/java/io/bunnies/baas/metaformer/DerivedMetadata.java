package io.bunnies.baas.metaformer;

import java.util.Map;

public class DerivedMetadata {
    private Map<String, DerivedBunnyResource> resources;

    public DerivedMetadata(Map<String, DerivedBunnyResource> resources) {
        this.resources = resources;
    }
}
