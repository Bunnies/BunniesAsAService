package io.bunnies.baas.resources.types;

public class WebmMediaType extends ResourceMediaType {
    public static final String KEY = "WEBM";

    public WebmMediaType(String resourceID, String mediaBaseUrl) {
        super(resourceID, mediaBaseUrl);
    }

    @Override
    public String constructUrl() {
        return this.mediaBaseUrl + "webm/" + this.resourceID + ".webm";
    }
}
