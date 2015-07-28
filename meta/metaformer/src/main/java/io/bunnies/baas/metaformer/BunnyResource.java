package io.bunnies.baas.metaformer;

public class BunnyResource {
    private int width;
    private int height;
    private double aspect_ratio;

    public BunnyResource(int width, int height, double aspect_ratio) {
        this.width = width;
        this.height = height;
        this.aspect_ratio = aspect_ratio;
    }
}
