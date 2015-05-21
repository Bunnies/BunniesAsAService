package io.bunnies.baas.resources.types;

import io.bunnies.baas.Constants;

public class PosterMediaType extends ResourceMediaType {
    public static final String KEY = "POSTER";

    public PosterMediaType(String resourceID) {
        super(resourceID);
    }

    @Override
    public String constructUrl() {
        return Constants.MEDIA_BASE_URL + "poster/" + this.resourceID + ".png";
    }
}
