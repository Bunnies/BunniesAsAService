package io.bunnies.baas.resources;

import io.bunnies.baas.metadata.DerivedMetadata;
import io.bunnies.baas.metadata.SpecifiedMetadata;

import java.util.Set;

public class BunnyResourcesSingleton {
    private static BunnyResources bunnyResources;

    public static BunnyResources getInstance(String mediaBaseUrl, DerivedMetadata derivedMetadata, SpecifiedMetadata specifiedMetadata) {
        if (bunnyResources == null) {
            bunnyResources = new BunnyResources(mediaBaseUrl, derivedMetadata, specifiedMetadata);
        }

        return bunnyResources;
    }
}
