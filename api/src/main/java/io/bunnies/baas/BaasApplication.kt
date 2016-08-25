package io.bunnies.baas

import com.bendb.dropwizard.redis.JedisBundle
import com.bendb.dropwizard.redis.JedisFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.bunnies.baas.metadata.DerivedMetadata
import io.bunnies.baas.metadata.SpecifiedMetadata
import io.bunnies.baas.resources.BunnyResources
import io.bunnies.baas.resources.BunnyResourcesSingleton
import io.bunnies.baas.services.RequestTracker
import io.bunnies.baas.services.RequestTrackerSingleton
import io.bunnies.baas.services.v2.BunnyServiceV2
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Files
import java.nio.file.Paths

class BaasApplication : Application<BaasConfiguration>() {
    private val LOGGER = LoggerFactory.getLogger(BaasApplication::class.java)

    override fun getName(): String {
        return "BunniesAsAService"
    }

    override fun initialize(bootstrap: Bootstrap<BaasConfiguration>) {
        bootstrap.addBundle(object : JedisBundle<BaasConfiguration>() {
            override fun getJedisFactory(configuration: BaasConfiguration): JedisFactory {
                return configuration.jedis
            }
        })
    }

    @Throws(Exception::class)
    override fun run(configuration: BaasConfiguration, environment: Environment) {
        val gson = GsonBuilder().create()
        val derivedMetadata = gson.fromJson(Files.newBufferedReader(Paths.get("").resolve(configuration.derivedMetadataPath)), DerivedMetadata::class.java)
        val specifiedMetadata = gson.fromJson(Files.newBufferedReader(Paths.get("").resolve(configuration.specifiedMetadataPath)), SpecifiedMetadata::class.java)

        LOGGER.info("Loaded {} resources from derived metadata: {}", derivedMetadata.resources.size, derivedMetadata.resources.keys)
        LOGGER.info("Loaded {} resources from specified metadata: {}", specifiedMetadata.resources.size, specifiedMetadata.resources.keys)

        val resources = BunnyResourcesSingleton.getInstance(configuration.mediaBaseUrl, derivedMetadata, specifiedMetadata)
        val tracker = RequestTrackerSingleton.getInstance(configuration.jedis.build(environment), resources)

        val resourcev2 = BunnyServiceV2(
                resources,
                tracker)

        environment.applicationContext.errorHandler = JsonErrorHandler()
        environment.jersey().register(resourcev2)
    }

    companion object {

        @Throws(Exception::class)
        @JvmStatic fun main(args: Array<String>) {
            BaasApplication().run(*args)
        }
    }
}
