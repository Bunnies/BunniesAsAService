package io.bunnies.baas;

import com.bendb.dropwizard.redis.JedisFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;

public class BaasConfiguration extends Configuration {
    @NotEmpty
    @Length(max = 1)
    private String minBunnyID;

    @NotEmpty
    @Length(max = 3)
    private String maxBunnyID;

    @NotEmpty
    private String mediaBaseUrl;

    @JsonProperty
    @Nonnull
    private JedisFactory jedis = new JedisFactory();

    @JsonProperty
    public String getMinBunnyID() {
        return this.minBunnyID;
    }

    @JsonProperty
    public String getMaxBunnyID() {
        return this.maxBunnyID;
    }

    @JsonProperty
    public String getMediaBaseUrl() {
        return this.mediaBaseUrl;
    }

    @JsonProperty
    public JedisFactory getJedis() {
        return this.jedis;
    }
}
