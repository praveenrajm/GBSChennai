package com.identv.voxxy;

import com.identv.voxxy.common.utils.constants.IHttpHeaderNames;
import com.identv.voxxy.common.utils.RestAuthenticator;
import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by Praveen on 04-01-2016.
 */

public class RestRequestFilter implements ContainerRequestFilter {

    @InjectParam
    RestAuthenticator restAuthenticator;

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        // do not filter requests that do not use OAuth authentication
        String path = request.getPath();

        System.out.println("Filtering request path: " + path);

        // IMPORTANT!!! First, Acknowledge any pre-flight test from browsers for this case before validating the headers (CORS stuff)
        if (request.getMethod().equals("OPTIONS")) {
            Response.ResponseBuilder builder = null;
            String response = "";
            builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
            throw new WebApplicationException(builder.build());

        }

        // Then check is the service key exists and is valid.
        // RestAuthenticator restAuthenticator = RestAuthenticator.getInstance();
        String serviceKey = request.getHeaderValue(IHttpHeaderNames.SERVICE_KEY);

        if (!restAuthenticator.isServiceKeyValid(serviceKey)) {
            // Kick anyone without a valid service key
            Response.ResponseBuilder builder = null;
            String response = "Invalid Service key";
            builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
            throw new WebApplicationException(builder.build());
        }

        // For any other methods besides login, the authToken must be verified
        if (!path.startsWith("voxxyservice/login")) {
            String authToken = request.getHeaderValue(IHttpHeaderNames.AUTH_TOKEN);

            // if it isn't valid, just kick them out.
            if (!restAuthenticator.isAuthTokenValid(serviceKey, authToken)) {
                Response.ResponseBuilder builder = null;
                String response = "Invalid Token";
                builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
                throw new WebApplicationException(builder.build());
            }
        }
        return request;
    }
}

