package io.bunnies.baas.metaformer;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String USAGE = "java -jar <jar file> <absolute media directory>";
    private static final int MIN_WIDTH = 50;
    private static final int MIN_HEIGHT = 50;

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

        Map<String, DerivedBunnyResource> resources = Maps.newHashMap();
        Map<String, SpecifiedBunnyResource> specifiedResources = Maps.newHashMap();

        for (String id : gifResources.getResourceIds()) {
            BufferedImage image;

            try {
                LOGGER.info("Reading {}: {}", posterResources.getId(), id);
                image = ImageIO.read(posterResources.getResourceFile(id));
            } catch (IOException e) {
                LOGGER.error("Failed to read '{}': {}", id, e);

                return;
            }

            int width = image.getWidth();
            int height = image.getHeight();
            double aspect_ratio = ((double) width) / height;

            if (width < MIN_WIDTH || height < MIN_HEIGHT || aspect_ratio < 0) {
                LOGGER.error("Failed to read '{}': {}: ", width, height, aspect_ratio);

                return;
            }

            resources.put(id, new DerivedBunnyResource(width, height, aspect_ratio));
            specifiedResources.put(id, new SpecifiedBunnyResource(""));
        }

        DerivedMetadata derivedMetadata = new DerivedMetadata(resources);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        LOGGER.info("Attempting to load existing specified metadata...");

        try {
            String existingSpecifiedMetadataString = new String(Files.readAllBytes(Paths.get("").resolve("specified_metadata.json")));

            LOGGER.info("Loaded existing specified metadata file successfully");
            SpecifiedMetadata existingSpecifiedMetadata = gson.fromJson(existingSpecifiedMetadataString, SpecifiedMetadata.class);

            if (existingSpecifiedMetadata.getResources().size() > 0) {
                specifiedResources.putAll(existingSpecifiedMetadata.getResources());

                LOGGER.info("Loaded existing specified metadata successfully");
            }
        } catch (IOException e) {
            LOGGER.warn("Couldn't find or load existing specified metadata, starting from scratch");
        }

        SpecifiedMetadata specifiedMetadata = new SpecifiedMetadata(specifiedResources);

        String derivedMetadataString = gson.toJson(derivedMetadata);
        String specifiedMetadataString = gson.toJson(specifiedMetadata);

        LOGGER.info("Writing derived_metadata.json");

        try {
            Files.write(Paths.get("").resolve("derived_metadata.json"), derivedMetadataString.getBytes(Charsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Failed to write out derived_metadata.json: {}", e);
        }

        LOGGER.info("Writing specified_metadata.json");

        try {
            Files.write(Paths.get("").resolve("specified_metadata.json"), specifiedMetadataString.getBytes(Charsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("Failed to write out derived_metadata.json: {}", e);
        }
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
            String resourceId = rawResourceName.substring(0, rawResourceName.length() - suffixLength - 1).toLowerCase();

            resourceType.addResourceId(resourceId);
            resourceType.addResourceFile(resourceId, resourceFile);
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
