package io.bunnies.baas.services.v1.responses;

import io.bunnies.baas.resources.IBunnyResource;
import io.bunnies.baas.resources.types.GifMediaType;
import io.bunnies.baas.resources.types.Mp4MediaType;
import io.bunnies.baas.resources.types.PosterMediaType;
import io.bunnies.baas.resources.types.WebmMediaType;

public class BunnyResponseV1 {
    public int total_served;
    public int specifics_served;
    public String id;
    public String location;
    public String location_poster;
    public String location_webm;
    public String location_mp4;

    public BunnyResponseV1(IBunnyResource bunnyResource, int total_served, int specifics_served) {
        this.id = bunnyResource.getBunnyID();
        this.location = bunnyResource.getResourceUrl(GifMediaType.KEY);
        this.location_poster = bunnyResource.getResourceUrl(PosterMediaType.KEY);
        this.location_webm = bunnyResource.getResourceUrl(WebmMediaType.KEY);
        this.location_mp4 = bunnyResource.getResourceUrl(Mp4MediaType.KEY);
        this.total_served = total_served;
        this.specifics_served = specifics_served;
    }
}
