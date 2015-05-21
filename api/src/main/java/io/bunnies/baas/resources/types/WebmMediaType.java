package io.bunnies.baas.resources.types;

import io.bunnies.baas.Constants;

public class WebmMediaType extends ResourceMediaType {
    public static final String KEY = "WEBM";

    public WebmMediaType(String resourceID) {
        super(resourceID);
    }

    @Override
    public String constructUrl() {
        return Constants.MEDIA_BASE_URL + "webm/" + this.resourceID + ".webm";
    }
}
