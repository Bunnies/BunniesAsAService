package io.bunnies.baas;

import com.bendb.dropwizard.redis.JedisFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;

public class BaasConfiguration extends Configuration {
    @NotEmpty
    private String metadataPath;

    @NotEmpty
    private String mediaBaseUrl;

    @JsonProperty
    @Nonnull
    private JedisFactory jedis = new JedisFactory();

    @JsonProperty
    public String getMetadataPath() {
        return this.metadataPath;
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
