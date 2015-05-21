package io.bunnies.baas.resources;

public class BunnyResourcesSingleton {
    private static BunnyResources bunnyResources;

    public static BunnyResources getInstance(String mediaBaseUrl, int minBunnyID, int maxBunnyID) {
        if (bunnyResources == null) {
            bunnyResources = new BunnyResources(mediaBaseUrl, minBunnyID, maxBunnyID);
        }

        return bunnyResources;
    }
}
