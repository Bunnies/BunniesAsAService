package io.bunnies.baas.services.v2;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import io.bunnies.baas.resources.BunnyResources;
import io.bunnies.baas.resources.IBunnyResource;
import io.bunnies.baas.resources.types.PosterMediaType;
import io.bunnies.baas.services.RequestTracker;
import io.bunnies.baas.services.v2.responses.BunnyResponseV2;
import io.bunnies.baas.services.v2.responses.ErrorResponseV2;
import redis.clients.jedis.Jedis;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
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
    public Response giveRandomBunny(@QueryParam("media") String queryMediaTypes, @Context Jedis jedis) {
        IBunnyResource bunny = this.bunnyResources.getRandomBunnyResource();

        this.requestTracker.incrementServedAndTotal(bunny.getBunnyID());

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

        this.requestTracker.incrementServedAndTotal(bunny.getBunnyID());

        return this.serveSpecificBunny(bunny, queryMediaTypes);
    }

    @GET
    @Path("/loop/random/redirect")
    @Timed
    public Response redirectToRandomBunny(@QueryParam("media") String queryMediaTypes) {
        IBunnyResource bunny = this.bunnyResources.getRandomBunnyResource();

        this.requestTracker.incrementServedAndTotal(bunny.getBunnyID());

        return this.serveTemporaryRedirectToSpecificBunny(bunny, queryMediaTypes);
    }

    @GET
    @Path("/loop/{id}/redirect")
    @Timed
    public Response redirectToSpecificBunny(@PathParam("id") String id, @QueryParam("media") String queryMediaTypes) {
        if (!this.isBunnyIDSane(id)) {
            return this.constructBadRequestResponse("not a valid bunny ID");
        }

        IBunnyResource bunny = this.bunnyResources.getSpecificBunnyResource(id);
        if (bunny == null) {
            return this.constructNotFoundResponse("couldn't find that bunny");
        }

        this.requestTracker.incrementServedAndTotal(bunny.getBunnyID());

        return this.serveTemporaryRedirectToSpecificBunny(bunny, queryMediaTypes);
    }

    private Response serveTemporaryRedirectToSpecificBunny(IBunnyResource bunny, String queryMediaTypes) {
        Set<String> mediaTypes = this.extractMediaTypes(queryMediaTypes);
        if (mediaTypes.isEmpty()) {
            return this.constructBadRequestResponse("media parameter is missing or malformed");
        }

        if (mediaTypes.size() > 1) {
            return this.constructBadRequestResponse("only 1 media type is permitted for redirects");
        }

        String mediaType = (String) mediaTypes.toArray()[0];
        if (bunny.hasResourceType(mediaType)) {
            return this.constructTempRedirectResponse(bunny.getResourceUrl(mediaType));
        }

        return this.constructNotFoundResponse("couldn't get that type of media for that bunny");
    }

    private Response serveSpecificBunny(IBunnyResource bunny, String queryMediaTypes) {
        Set<String> mediaTypes = this.extractMediaTypes(queryMediaTypes);
        if (mediaTypes.isEmpty()) {
            return this.constructBadRequestResponse("media parameter is missing or malformed");
        }

        BunnyResponseV2 bunnyResponse = new BunnyResponseV2(bunny, mediaTypes, this.requestTracker.getServed(bunny.getBunnyID()), this.requestTracker.getServed(RequestTracker.TOTAL_IDENTIFIER));
        if (bunnyResponse.numberOfContainedFileTypes() > 0) {
            if (!mediaTypes.contains(PosterMediaType.KEY)) {
                bunnyResponse.addMediaType(bunny, PosterMediaType.KEY);
            }

            return this.constructBunnyResponse(bunnyResponse);
        }

        return this.constructNotFoundResponse("couldn't get any media for that bunny");
    }

    private Set<String> extractMediaTypes(String queryMediaTypes) {
        Set<String> mediaTypes = Sets.newHashSet();

        if (Strings.isNullOrEmpty(queryMediaTypes) || queryMediaTypes.length() > MAX_MEDIA_LENGTH) {
            return mediaTypes;
        }

        if (!this.isQueryMediaTypesValid(queryMediaTypes)) {
            return mediaTypes;
        }

        return Sets.newHashSet(Splitter.on(',').split(queryMediaTypes.toUpperCase()));
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

    private boolean isQueryMediaTypesValid(String queryMediaTypes) {
        for (int i = 0, length = queryMediaTypes.length(); i < length; i++) {
            char c = queryMediaTypes.charAt(i);

            if (!Character.isDigit(c) && (!Character.isLetter(c) && c != ',')) {
                return false;
            }
        }

        return true;
    }

    private Response constructNotFoundResponse(String message) {
        return Response.status(Response.Status.NOT_FOUND).entity(
                new ErrorResponseV2(Response.Status.NOT_FOUND.getStatusCode(), message)
        ).build();
    }

    private Response constructBunnyResponse(BunnyResponseV2 bunnyResponse) {
        return Response.status(Response.Status.OK).entity(bunnyResponse).build();
    }

    private Response constructInternalErrorResponse(String message) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                new ErrorResponseV2(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message)
        ).build();
    }

    private Response constructBadRequestResponse(String message) {
        return Response.status(Response.Status.BAD_REQUEST).entity(
                new ErrorResponseV2(Response.Status.BAD_REQUEST.getStatusCode(), message)
        ).build();
    }

    private Response constructTempRedirectResponse(String uri) {
        return Response.temporaryRedirect(URI.create(uri)).build();
    }
}
