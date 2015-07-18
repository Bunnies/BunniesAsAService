package io.bunnies.baas;

import com.bendb.dropwizard.redis.JedisFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.internal.NotNull;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

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
    @NotNull
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
