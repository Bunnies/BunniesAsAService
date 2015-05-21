package io.bunnies.baas;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty
    public String getMinBunnyID() {
        return this.minBunnyID;
    }

    @JsonProperty
    public String getMaxBunnyID() {
        return this.maxBunnyID;
    }
}
