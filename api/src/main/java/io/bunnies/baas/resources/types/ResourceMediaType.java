package io.bunnies.baas.resources.types;

public abstract class ResourceMediaType {
    protected final String resourceID;
    protected final String mediaBaseUrl;

    public ResourceMediaType(String resourceID, String mediaBaseUrl) {
        this.resourceID = resourceID;
        this.mediaBaseUrl = mediaBaseUrl;
    }

    public abstract String constructUrl();
}
