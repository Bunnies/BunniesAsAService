package io.bunnies.baas.resources.types;

public class PosterMediaType extends ResourceMediaType {
    public static final String KEY = "POSTER";

    public PosterMediaType(String resourceID, String mediaBaseUrl) {
        super(resourceID, mediaBaseUrl);
    }

    @Override
    public String constructUrl() {
        return this.mediaBaseUrl + "poster/" + this.resourceID + ".png";
    }
}
