package io.bunnies.baas.resources;

import javax.annotation.Nullable;

public interface IBunnyResource {
    String getBunnyID();

    boolean hasResourceType(String resourceKey);

    double getAspectRatio();

    @Nullable
    String getResourceUrl(String resourceKey);

    String getBunnySource();
}
