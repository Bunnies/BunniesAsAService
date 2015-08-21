package io.bunnies.baas.metadata;

public class DerivedBunnyResourceMetadata {
    private int width;
    private int height;
    private double aspect_ratio;

    public DerivedBunnyResourceMetadata(int width, int height, double aspect_ratio) {
        this.width = width;
        this.height = height;
        this.aspect_ratio = aspect_ratio;
    }

    public double getAspectRatio() {
        return this.aspect_ratio;
    }
}
