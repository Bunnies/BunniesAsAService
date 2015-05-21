package io.bunnies.baas.resources;

public class BunnyResourcesSingleton {
    private static BunnyResources bunnyResources;

    public static BunnyResources getInstance() {
        if (bunnyResources == null) {
            bunnyResources = new BunnyResources();
        }

        return bunnyResources;
    }
}
