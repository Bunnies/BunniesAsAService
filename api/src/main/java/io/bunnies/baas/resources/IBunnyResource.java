package io.bunnies.baas.resources;

import javax.annotation.Nullable;

public interface IBunnyResource {
    String getBunnyID();

    boolean hasResourceType(String resourceKey);

    @Nullable
    String getResourceUrl(String resourceKey);
}
