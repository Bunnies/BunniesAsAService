package io.bunnies.baas.metadata;

public class BunnyResourceMetadata {
    private int width;
    private int height;
    private double aspect_ratio;

    public BunnyResourceMetadata(int width, int height, double aspect_ratio) {
        this.width = width;
        this.height = height;
        this.aspect_ratio = aspect_ratio;
    }
}
