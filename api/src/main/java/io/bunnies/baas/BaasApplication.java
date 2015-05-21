package io.bunnies.baas;

import io.bunnies.baas.resources.BunnyResourcesSingleton;
import io.bunnies.baas.services.v1.BunnyServiceV1;
import io.bunnies.baas.services.v1.RequestTrackerSingleton;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class BaasApplication extends Application<BaasConfiguration> {
    public static void main(String[] args) throws Exception {
        new BaasApplication().run(args);
    }

    @Override
    public String getName() {
        return "BunniesAsAService";
    }

    @Override
    public void initialize(Bootstrap<BaasConfiguration> bootstrap) {

    }

    @Override
    public void run(BaasConfiguration configuration, Environment environment) throws Exception {
        int minBunnyID = Integer.parseInt(configuration.getMinBunnyID());
        int maxBunnyID = Integer.parseInt(configuration.getMaxBunnyID());

        if (minBunnyID < 1 || maxBunnyID <= minBunnyID) {
            throw new RuntimeException("Min or max bunny IDs are malformed");
        }

        final BunnyServiceV1 resource = new BunnyServiceV1(
                BunnyResourcesSingleton.getInstance(minBunnyID, maxBunnyID),
                RequestTrackerSingleton.getInstance()
        );

        environment.jersey().register(resource);
    }
}