package io.bunnies.baas

import com.fasterxml.jackson.databind.ObjectMapper
import io.bunnies.baas.services.v2.responses.ErrorResponseV2
import org.eclipse.jetty.http.HttpMethod
import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.http.MimeTypes
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Response
import org.eclipse.jetty.server.handler.ErrorHandler
import org.eclipse.jetty.util.ByteArrayISO8859Writer

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.io.Writer

internal class JsonErrorHandler : ErrorHandler() {

    @Throws(IOException::class)
    override fun handle(target: String?, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
        // Much of this is copied from the parent class
        // There was no way to modify the response type without doing so
        val method = request.method
        if (!HttpMethod.GET.`is`(method) && !HttpMethod.POST.`is`(method) && !HttpMethod.HEAD.`is`(method)) {
            baseRequest.isHandled = true
            return
        }

        baseRequest.isHandled = true
        response.contentType = MimeTypes.Type.APPLICATION_JSON.asString()

        val writer = ByteArrayISO8859Writer(4096)
        val reason = if (response is Response) response.reason else null
        handleErrorPage(request, writer, response.status, reason)
        writer.flush()
        response.setContentLength(writer.size())
        writer.writeTo(response.outputStream)
        writer.destroy()
    }

    @Throws(IOException::class)
    override fun writeErrorPage(request: HttpServletRequest, writer: Writer, code: Int, message: String, showStacks: Boolean) {
        var message = message
        if (message == null) {
            message = HttpStatus.getMessage(code)
        }

        val response = ErrorResponseV2(code, message)
        val mapper = ObjectMapper()
        writer.write(mapper.writeValueAsString(response))
    }
}
