package io.bunnies.baas.metaformer;

import com.google.common.collect.Sets;

import java.util.Set;

public class ResourceType {
    private final String id;
    private final String fileSuffix;
    private final Set<String> resourceIds;

    public ResourceType(String id, String fileSuffix) {
        this.id = id;
        this.fileSuffix = fileSuffix;
        this.resourceIds = Sets.newHashSet();
    }

    public String getId() {
        return this.id;
    }

    public String getFileSuffix() {
        return this.fileSuffix;
    }

    public void addResourceId(String id) {
        this.resourceIds.add(id);
    }

    public Set<String> getResourceIds() {
        return this.resourceIds;
    }
}
