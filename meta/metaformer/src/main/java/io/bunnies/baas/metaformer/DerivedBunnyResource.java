package io.bunnies.baas.metaformer;

public class DerivedBunnyResource {
    private int width;
    private int height;
    private double aspect_ratio;

    public DerivedBunnyResource(int width, int height, double aspect_ratio) {
        this.width = width;
        this.height = height;
        this.aspect_ratio = aspect_ratio;
    }
}
