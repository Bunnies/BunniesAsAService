package io.bunnies.baas.metaformer;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class ResourceType {
    private final String id;
    private final String fileSuffix;
    private final Set<String> resourceIds;
    private final Map<String, File> resourceFiles;

    public ResourceType(String id, String fileSuffix) {
        this.id = id;
        this.fileSuffix = fileSuffix;
        this.resourceIds = Sets.newHashSet();
        this.resourceFiles = Maps.newHashMap();
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

    public void addResourceFile(String id, File file) {
        this.resourceFiles.put(id, file);
    }

    public File getResourceFile(String id) {
        return this.resourceFiles.get(id);
    }
}
