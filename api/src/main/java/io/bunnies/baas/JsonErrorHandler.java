package io.bunnies.baas;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.util.ByteArrayISO8859Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

public class JsonErrorHandler extends ErrorHandler {
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Much of this is copied from the parent class
        // There was no way to modify the response type without doing so
        String method = request.getMethod();
        if (!HttpMethod.GET.is(method) && !HttpMethod.POST.is(method) && !HttpMethod.HEAD.is(method)) {
            baseRequest.setHandled(true);
            return;
        }

        baseRequest.setHandled(true);
        response.setContentType(MimeTypes.Type.APPLICATION_JSON.asString());

        ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer(4096);
        String reason = (response instanceof Response) ? ((Response) response).getReason() : null;
        handleErrorPage(request, writer, response.getStatus(), reason);
        writer.flush();
        response.setContentLength(writer.size());
        writer.writeTo(response.getOutputStream());
        writer.destroy();
    }

    @Override
    protected void writeErrorPage(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks)
            throws IOException {
        if (message == null) {
            message = HttpStatus.getMessage(code);
        }

        writer.write("{\"code\":\"" + code + "\",\"message\"=\"" + message + "\"}");
    }
}
