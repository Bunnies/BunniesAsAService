package io.bunnies.baas.resources.types;

public class GifMediaType extends ResourceMediaType {
    public static final String KEY = "GIF";

    public GifMediaType(String resourceID, String mediaBaseUrl) {
        super(resourceID, mediaBaseUrl);
    }

    @Override
    public String constructUrl() {
        return this.mediaBaseUrl + "gif/" + this.resourceID + ".gif";
    }
}
