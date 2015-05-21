package io.bunnies.baas.resources.types;

import io.bunnies.baas.Constants;

public class Mp4MediaType extends ResourceMediaType {
    public static final String KEY = "MP4";

    public Mp4MediaType(String resourceID) {
        super(resourceID);
    }

    @Override
    public String constructUrl() {
        return Constants.MEDIA_BASE_URL + "mp4/" + this.resourceID + ".mp4";
    }
}
