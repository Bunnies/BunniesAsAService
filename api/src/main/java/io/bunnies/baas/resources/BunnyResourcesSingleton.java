package io.bunnies.baas.resources;

public class BunnyResourcesSingleton {
    private static BunnyResources bunnyResources;

    public static BunnyResources getInstance(int minBunnyID, int maxBunnyID) {
        if (bunnyResources == null) {
            bunnyResources = new BunnyResources(minBunnyID, maxBunnyID);
        }

        return bunnyResources;
    }
}
