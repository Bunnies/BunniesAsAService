package io.bunnies.baas.resources;

import java.util.Set;

public class BunnyResourcesSingleton {
    private static BunnyResources bunnyResources;

    public static BunnyResources getInstance(String mediaBaseUrl, Set<String> bunnyIds) {
        if (bunnyResources == null) {
            bunnyResources = new BunnyResources(mediaBaseUrl, bunnyIds);
        }

        return bunnyResources;
    }
}
