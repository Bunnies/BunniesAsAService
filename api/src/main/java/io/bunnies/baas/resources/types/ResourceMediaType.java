package io.bunnies.baas.resources.types;

public abstract class ResourceMediaType {
    protected String resourceID;

    public ResourceMediaType(String resourceID) {
        this.resourceID = resourceID;
    }

    public abstract String constructUrl();
}
