package io.bunnies.baas;

import com.bendb.dropwizard.redis.JedisBundle;
import com.bendb.dropwizard.redis.JedisFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.bunnies.baas.metadata.DerivedMetadata;
import io.bunnies.baas.metadata.SpecifiedMetadata;
import io.bunnies.baas.resources.BunnyResources;
import io.bunnies.baas.resources.BunnyResourcesSingleton;
import io.bunnies.baas.services.RequestTracker;
import io.bunnies.baas.services.RequestTrackerSingleton;
import io.bunnies.baas.services.v2.BunnyServiceV2;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

public class BaasApplication extends Application<BaasConfiguration> {
    private final Logger LOGGER = LoggerFactory.getLogger(BaasApplication.class);

    public static void main(String[] args) throws Exception {
        new BaasApplication().run(args);
    }

    @Override
    public String getName() {
        return "BunniesAsAService";
    }

    @Override
    public void initialize(Bootstrap<BaasConfiguration> bootstrap) {
        bootstrap.addBundle(new JedisBundle<BaasConfiguration>() {
            @Override
            public JedisFactory getJedisFactory(BaasConfiguration configuration) {
                return configuration.getJedis();
            }
        });
    }

    @Override
    public void run(BaasConfiguration configuration, Environment environment) throws Exception {
        Gson gson = new GsonBuilder().create();
        DerivedMetadata derivedMetadata = gson.fromJson(Files.newBufferedReader(Paths.get("").resolve(configuration.getDerivedMetadataPath())), DerivedMetadata.class);
        SpecifiedMetadata specifiedMetadata = gson.fromJson(Files.newBufferedReader(Paths.get("").resolve(configuration.getSpecifiedMetadataPath())), SpecifiedMetadata.class);

        LOGGER.info("Loaded {} resources from derived metadata: {}", derivedMetadata.getResources().size(), derivedMetadata.getResources().keySet());
        LOGGER.info("Loaded {} resources from specified metadata: {}", specifiedMetadata.getResources().size(), specifiedMetadata.getResources().keySet());

        BunnyResources resources = BunnyResourcesSingleton.getInstance(configuration.getMediaBaseUrl(), derivedMetadata, specifiedMetadata);
        RequestTracker tracker = RequestTrackerSingleton.getInstance(configuration.getJedis().build(environment), resources);

        final BunnyServiceV2 resourcev2 = new BunnyServiceV2(
                resources,
                tracker
        );

        environment.getApplicationContext().setErrorHandler(new JsonErrorHandler());
        environment.jersey().register(resourcev2);
    }
}
