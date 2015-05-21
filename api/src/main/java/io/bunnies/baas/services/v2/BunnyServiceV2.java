package io.bunnies.baas.services.v2;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import io.bunnies.baas.resources.BunnyResources;
import io.bunnies.baas.resources.IBunnyResource;
import io.bunnies.baas.resources.types.PosterMediaType;
import io.bunnies.baas.services.v1.RequestTracker;
import io.bunnies.baas.services.v1.responses.ErrorResponseV1;
import io.bunnies.baas.services.v2.responses.BunnyResponseV2;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Set;

@Path("/v2")
@Produces(MediaType.APPLICATION_JSON)
public class BunnyServiceV2 {
    private BunnyResources bunnyResources;
    private RequestTracker requestTracker;

    private static final int MAX_MEDIA_LENGTH = 20;

    public BunnyServiceV2(BunnyResources bunnyResources, RequestTracker requestTracker) {
        this.bunnyResources = bunnyResources;
        this.requestTracker = requestTracker;
    }

    @GET
    @Path("/loop/random")
    @Timed
    public Response giveRandomBunny(@QueryParam("media") String queryMediaTypes) {
        IBunnyResource bunny = this.bunnyResources.getRandomBunnyResource();

        this.requestTracker.incrementTotalServed();

        return this.serveSpecificBunny(bunny, queryMediaTypes);
    }

    @GET
    @Path("/loop/{id}")
    @Timed
    public Response giveSpecificBunny(@PathParam("id") String id, @QueryParam("media") String queryMediaTypes) {
        if (!this.isBunnyIDSane(id)) {
            return this.constructBadRequestResponse("not a valid bunny ID");
        }

        IBunnyResource bunny = this.bunnyResources.getSpecificBunnyResource(id);
        if (bunny == null) {
            return this.constructNotFoundResponse("couldn't find that bunny");
        }

        this.requestTracker.incrementTotalServed();
        this.requestTracker.incrementSpecificsServed();

        return this.serveSpecificBunny(bunny, queryMediaTypes);
    }

    private Response serveSpecificBunny(IBunnyResource bunny, String queryMediaTypes) {
        if (Strings.isNullOrEmpty(queryMediaTypes) || queryMediaTypes.length() > MAX_MEDIA_LENGTH) {
            return this.constructBadRequestResponse("media parameter is missing or malformed");
        }

        Set<String> mediaTypes = Sets.newHashSet(Splitter.on(',').split(queryMediaTypes.toUpperCase()));

        BunnyResponseV2 bunnyResponse = new BunnyResponseV2(bunny, mediaTypes, this.requestTracker.getTotalServed());
        if (bunnyResponse.numberOfContainedFileTypes() > 0) {
            if (!mediaTypes.contains(PosterMediaType.KEY)) {
                bunnyResponse.addMediaType(bunny, PosterMediaType.KEY);
            }

            return this.constructBunnyResponse(bunnyResponse);
        }

        return this.constructNotFoundResponse("couldn't get any media for that bunny");
    }

    private boolean isBunnyIDSane(String id) {
        if (Strings.isNullOrEmpty(id)) {
            return false;
        }

        int length = id.length();
        if (length > 4) {
            return false;
        }

        if (id.charAt(0) == '-') {
            return false;
        }

        try {
            Integer.parseInt(id);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private Response constructBunnyResponse(BunnyResponseV2 bunnyResponse) {
        return Response.status(Response.Status.OK).entity(bunnyResponse).build();
    }

    private Response constructNotFoundResponse(String error) {
        return Response.status(Response.Status.NOT_FOUND).entity(new ErrorResponseV1(error)).build();
    }

    private Response constructInternalErrorResponse(String error) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorResponseV1(error)).build();
    }

    private Response constructBadRequestResponse(String error) {
        return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponseV1(error)).build();
    }

    private Response constructTempRedirectResponse(String uri) {
        return Response.temporaryRedirect(URI.create(uri)).build();
    }
}
