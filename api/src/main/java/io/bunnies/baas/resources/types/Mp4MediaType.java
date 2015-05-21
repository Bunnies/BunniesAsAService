package io.bunnies.baas.resources.types;

public class Mp4MediaType extends ResourceMediaType {
    public static final String KEY = "MP4";

    public Mp4MediaType(String resourceID, String mediaBaseUrl) {
        super(resourceID, mediaBaseUrl);
    }

    @Override
    public String constructUrl() {
        return this.mediaBaseUrl + "mp4/" + this.resourceID + ".mp4";
    }
}
