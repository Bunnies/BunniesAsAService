package io.bunnies.baas.services.v2

import com.codahale.metrics.annotation.Timed
import com.google.common.base.Splitter
import com.google.common.base.Strings
import com.google.common.collect.Sets
import io.bunnies.baas.resources.BunnyResources
import io.bunnies.baas.resources.IBunnyResource
import io.bunnies.baas.resources.types.PosterMediaType
import io.bunnies.baas.services.RequestTracker
import io.bunnies.baas.services.v2.responses.BunnyResponseV2
import io.bunnies.baas.services.v2.responses.ErrorResponseV2

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.net.URI

@Path("/v2")
@Produces(MediaType.APPLICATION_JSON)
class BunnyServiceV2(private val bunnyResources: BunnyResources, private val requestTracker: RequestTracker) {

    @GET
    @Path("/loop/random")
    @Timed
    fun giveRandomBunny(@QueryParam("media") queryMediaTypes: String?,
                        @QueryParam("widthHint") widthHint: String?,
                        @QueryParam("heightHint") heightHint: String?): Response {
        val bunny: IBunnyResource

        val hasWidthHint: Boolean
        val hasHeightHint: Boolean
        hasWidthHint = widthHint != null && !widthHint.isEmpty()
        hasHeightHint = heightHint != null && !heightHint.isEmpty()

        if (!hasWidthHint && !hasHeightHint) {
            bunny = this.bunnyResources.randomBunnyResource
        } else if (hasWidthHint && hasHeightHint) {
            val width: Int
            val height: Int

            try {
                width = Integer.parseInt(widthHint ?: "")
                height = Integer.parseInt(heightHint ?: "")

                if (width <= 0 || height <= 0 || width > 999 || height > 999) {
                    throw NumberFormatException()
                }
            } catch (e: NumberFormatException) {
                return this.constructBadRequestResponse("widthHint or heightHint were malformed")
            }

            bunny = this.bunnyResources.getRandomBunnyResource(width, height)
        } else {
            return this.constructBadRequestResponse("both widthHint and heightHint must either be present or omitted")
        }

        this.requestTracker.incrementServedAndTotal(bunny.bunnyID)

        return this.serveSpecificBunny(bunny, queryMediaTypes)
    }

    @GET
    @Path("/loop/{id}")
    @Timed
    fun giveSpecificBunny(@PathParam("id") id: String, @QueryParam("media") queryMediaTypes: String?): Response {
        if (!this.isBunnyIDSane(id)) {
            return this.constructBadRequestResponse("not a valid bunny ID")
        }

        val bunny = this.bunnyResources.getSpecificBunnyResource(id) ?: return this.constructNotFoundResponse("couldn't find that bunny")

        this.requestTracker.incrementServedAndTotal(bunny.bunnyID)

        return this.serveSpecificBunny(bunny, queryMediaTypes)
    }

    @GET
    @Path("/loop/random/redirect")
    @Timed
    fun redirectToRandomBunny(@QueryParam("media") queryMediaTypes: String?,
                              @QueryParam("widthHint") widthHint: String?,
                              @QueryParam("heightHint") heightHint: String?): Response {
        val bunny: IBunnyResource

        val hasWidthHint: Boolean
        val hasHeightHint: Boolean
        hasWidthHint = widthHint != null && !widthHint.isEmpty()
        hasHeightHint = heightHint != null && !heightHint.isEmpty()

        if (!hasWidthHint && !hasHeightHint) {
            bunny = this.bunnyResources.randomBunnyResource
        } else if (hasWidthHint && hasHeightHint) {
            val width: Int
            val height: Int

            try {
                width = Integer.parseInt(widthHint ?: "")
                height = Integer.parseInt(heightHint ?: "")

                if (width <= 0 || height <= 0 || width > 999 || height > 999) {
                    throw NumberFormatException()
                }
            } catch (e: NumberFormatException) {
                return this.constructBadRequestResponse("widthHint or heightHint were malformed")
            }

            bunny = this.bunnyResources.getRandomBunnyResource(width, height)
        } else {
            return this.constructBadRequestResponse("both widthHint and heightHint must either be present or omitted")
        }

        this.requestTracker.incrementServedAndTotal(bunny.bunnyID)

        return this.serveTemporaryRedirectToSpecificBunny(bunny, queryMediaTypes)
    }

    @GET
    @Path("/loop/{id}/redirect")
    @Timed
    fun redirectToSpecificBunny(@PathParam("id") id: String, @QueryParam("media") queryMediaTypes: String?): Response {
        if (!this.isBunnyIDSane(id)) {
            return this.constructBadRequestResponse("not a valid bunny ID")
        }

        val bunny = this.bunnyResources.getSpecificBunnyResource(id) ?: return this.constructNotFoundResponse("couldn't find that bunny")

        this.requestTracker.incrementServedAndTotal(bunny.bunnyID)

        return this.serveTemporaryRedirectToSpecificBunny(bunny, queryMediaTypes)
    }

    private fun serveTemporaryRedirectToSpecificBunny(bunny: IBunnyResource, queryMediaTypes: String?): Response {
        val mediaTypes = this.extractMediaTypes(queryMediaTypes)
        if (mediaTypes.isEmpty()) {
            return this.constructBadRequestResponse("media parameter is missing or malformed")
        }

        if (mediaTypes.size > 1) {
            return this.constructBadRequestResponse("only 1 media type is permitted for redirects")
        }

        val resourceUrl = bunny.getResourceUrl(mediaTypes.first()) ?: return this.constructNotFoundResponse("couldn't get that type of media for that bunny")

        return this.constructTempRedirectResponse(resourceUrl)
    }

    private fun serveSpecificBunny(bunny: IBunnyResource, queryMediaTypes: String?): Response {
        val mediaTypes = this.extractMediaTypes(queryMediaTypes)
        if (mediaTypes.isEmpty()) {
            return this.constructBadRequestResponse("media parameter is missing or malformed")
        }

        val bunnyResponse = BunnyResponseV2(bunny, mediaTypes, this.requestTracker.getServed(bunny.bunnyID), this.requestTracker.getServed(RequestTracker.TOTAL_IDENTIFIER))
        if (bunnyResponse.numberOfContainedFileTypes() > 0) {
            if (!mediaTypes.contains(PosterMediaType.KEY)) {
                bunnyResponse.addMediaType(bunny, PosterMediaType.KEY)
            }

            return this.constructBunnyResponse(bunnyResponse)
        }

        return this.constructNotFoundResponse("couldn't get any media for that bunny")
    }

    private fun extractMediaTypes(queryMediaTypes: String?): Set<String> {
        val mediaTypes = Sets.newHashSet<String>()

        if (queryMediaTypes == null || queryMediaTypes.isEmpty() || queryMediaTypes.length > MAX_MEDIA_LENGTH) {
            return mediaTypes
        }

        if (!this.isQueryMediaTypesValid(queryMediaTypes)) {
            return mediaTypes
        }

        return Sets.newHashSet(Splitter.on(',').split(queryMediaTypes.toUpperCase()))
    }

    private fun isBunnyIDSane(id: String): Boolean {
        if (Strings.isNullOrEmpty(id)) {
            return false
        }

        val length = id.length
        if (length > 4) {
            return false
        }

        if (id[0] == '-') {
            return false
        }

        try {
            Integer.parseInt(id)
        } catch (e: Exception) {
            return false
        }

        return true
    }

    private fun isQueryMediaTypesValid(queryMediaTypes: String): Boolean {
        var i = 0
        val length = queryMediaTypes.length
        while (i < length) {
            val c = queryMediaTypes[i]

            if (!Character.isDigit(c) && !Character.isLetter(c) && c != ',') {
                return false
            }
            i++
        }

        return true
    }

    private fun constructNotFoundResponse(message: String): Response {
        return Response.status(Response.Status.NOT_FOUND).entity(
                ErrorResponseV2(Response.Status.NOT_FOUND.statusCode, message)).build()
    }

    private fun constructBunnyResponse(bunnyResponse: BunnyResponseV2): Response {
        return Response.status(Response.Status.OK).entity(bunnyResponse).build()
    }

    private fun constructInternalErrorResponse(message: String): Response {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                ErrorResponseV2(Response.Status.INTERNAL_SERVER_ERROR.statusCode, message)).build()
    }

    private fun constructBadRequestResponse(message: String): Response {
        return Response.status(Response.Status.BAD_REQUEST).entity(
                ErrorResponseV2(Response.Status.BAD_REQUEST.statusCode, message)).build()
    }

    private fun constructTempRedirectResponse(uri: String): Response {
        return Response.temporaryRedirect(URI.create(uri)).build()
    }

    companion object {

        private val MAX_MEDIA_LENGTH = 20
    }
}
