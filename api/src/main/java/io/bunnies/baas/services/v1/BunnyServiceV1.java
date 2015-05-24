package io.bunnies.baas.services.v1;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Strings;
import io.bunnies.baas.resources.BunnyResources;
import io.bunnies.baas.resources.IBunnyResource;
import io.bunnies.baas.resources.types.GifMediaType;
import io.bunnies.baas.services.v1.responses.BunnyResponseV1;
import io.bunnies.baas.services.v1.responses.ErrorResponseV1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class BunnyServiceV1 {
    private final BunnyResources bunnyResources;
    private final RequestTracker requestTracker;

    public BunnyServiceV1(BunnyResources bunnyResources, RequestTracker requestTracker) {
        this.bunnyResources = bunnyResources;
        this.requestTracker = requestTracker;
    }

    @GET
    @Path("/gif/")
    @Timed
    public Response giveRandomBunny() {
        IBunnyResource bunny = this.bunnyResources.getRandomBunnyResource();

        if (bunny.hasResourceType(GifMediaType.KEY)) {
            this.requestTracker.incrementTotalServed();

            return this.constructBunnyResponse(bunny);
        }

        return this.constructInternalErrorResponse("couldn't find a random bunny");
    }

    @GET
    @Path("/gif/{id}")
    @Timed
    public Response giveSpecificBunny(@PathParam("id") String id) {
        if (!this.isBunnyIDSane(id)) {
            return this.constructBadRequestResponse("not a valid bunny ID");
        }

        IBunnyResource bunny = this.bunnyResources.getSpecificBunnyResource(id);
        if (bunny == null) {
            return this.constructNotFoundResponse("couldn't find that bunny");
        }

        if (bunny.hasResourceType(GifMediaType.KEY)) {
            this.requestTracker.incrementTotalServed();
            this.requestTracker.incrementSpecificsServed();

            return this.constructBunnyResponse(bunny);
        }

        return this.constructNotFoundResponse("couldn't get a gif for that bunny");
    }

    @GET
    @Path("/gif/random.gif")
    @Timed
    public Response temporaryRedirectToRandomBunny() {
        IBunnyResource bunny = this.bunnyResources.getRandomBunnyResource();

        if (bunny.hasResourceType(GifMediaType.KEY)) {
            this.requestTracker.incrementTotalServed();

            return this.constructTempRedirectResponse(bunny.getResourceUrl(GifMediaType.KEY));
        }

        return this.constructInternalErrorResponse("couldn't find a random bunny");
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

    private Response constructBunnyResponse(IBunnyResource bunnyResource) {
        int totalServed = this.requestTracker.getTotalServed();
        int specificsServed = this.requestTracker.getSpecificsServed();

        return Response.status(Response.Status.OK).entity(new BunnyResponseV1(bunnyResource, totalServed, specificsServed)).build();
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
