package io.bunnies.baas.metaformer;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String USAGE = "java -jar <jar file> <absolute media directory>";

    public static void main(String[] args) {
        if (args.length < 1) {
            LOGGER.error(USAGE);

            return;
        }

        Path path;

        try {
            path = Paths.get(args[0]);
        } catch (InvalidPathException e) {
            LOGGER.error("Invalid media path: {}", args[0]);

            return;
        }

        ResourceType gifResources = new ResourceType("gif", "gif");
        ResourceType posterResources = new ResourceType("poster", "png");
        ResourceType webmResources = new ResourceType("webm", "webm");
        ResourceType mp4Resources = new ResourceType("mp4", "mp4");

        List<ResourceType> resourceTypes = Lists.newArrayList(gifResources, posterResources, webmResources, mp4Resources);

        for (ResourceType resourceType : resourceTypes) {
            loadResourcesOfType(path, resourceType);
        }

        LOGGER.info("Found: ");
        LOGGER.info(" {} gifs: {}", gifResources.getResourceIds().size(), gifResources.getResourceIds());
        LOGGER.info(" {} posters: {}", posterResources.getResourceIds().size(), posterResources.getResourceIds());
        LOGGER.info(" {} webms: {}", webmResources.getResourceIds().size(), webmResources.getResourceIds());
        LOGGER.info(" {} mp4s: {}", mp4Resources.getResourceIds().size(), mp4Resources.getResourceIds());
    }

    private static void loadResourcesOfType(Path basePath, ResourceType resourceType) {
        Path resourcePath = basePath.resolve(resourceType.getId());
        File resourceFolder = resourcePath.toFile();
        if (resourceFolder == null || !resourceFolder.isDirectory()) {
            LOGGER.error("Folder for resource type '{}' not found", resourceType.getId());

            return;
        }

        File[] resourceFiles = getFilesOfType(resourceFolder, resourceType.getFileSuffix());
        int suffixLength = resourceType.getFileSuffix().length();
        for (File resourceFile : resourceFiles) {
            String rawResourceName = resourceFile.getName();
            String resourceId = rawResourceName.substring(0, rawResourceName.length() - suffixLength - 1);

            resourceType.addResourceId(resourceId);
        }
    }

    private static File[] getFilesOfType(File folder, final String fileType) {
        return folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.toString().toLowerCase().endsWith("." + fileType);
            }
        });
    }
}
