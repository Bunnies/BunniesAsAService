package io.bunnies.baas.resources.types;

import io.bunnies.baas.Constants;

public class GifMediaType extends ResourceMediaType {
    public static final String KEY = "GIF";

    public GifMediaType(String resourceID) {
        super(resourceID);
    }

    @Override
    public String constructUrl() {
        return Constants.MEDIA_BASE_URL + "gif/" + this.resourceID + ".gif";
    }
}
