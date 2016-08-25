package io.bunnies.baas

import com.bendb.dropwizard.redis.JedisFactory
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import org.hibernate.validator.constraints.NotEmpty

class BaasConfiguration : Configuration() {
    @NotEmpty
    lateinit var derivedMetadataPath: String

    @NotEmpty
    lateinit var specifiedMetadataPath: String

    @NotEmpty
    lateinit var mediaBaseUrl: String

    @JsonProperty
    val jedis = JedisFactory()
}
