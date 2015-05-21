package io.bunnies.baas.resources.types;

public abstract class ResourceMediaType {
    protected String resourceID;
    protected String mediaBaseUrl;

    public ResourceMediaType(String resourceID, String mediaBaseUrl) {
        this.resourceID = resourceID;
        this.mediaBaseUrl = mediaBaseUrl;
    }

    public abstract String constructUrl();
}
