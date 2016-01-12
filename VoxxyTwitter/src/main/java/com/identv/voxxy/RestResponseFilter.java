package com.identv.voxxy;

import com.identv.voxxy.common.utils.constants.IHttpHeaderNames;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Created by Praveen on 04-01-2016.
 */


public class RestResponseFilter implements ContainerResponseFilter {
    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        response.getHttpHeaders().add( "Access-Control-Allow-Origin", "*" );    // You may further limit certain client IPs with Access-Control-Allow-Origin instead of '*'
        response.getHttpHeaders().add( "Access-Control-Allow-Credentials", "true" );
        response.getHttpHeaders().add( "Access-Control-Allow-Methods", "GET, POST, DELETE, PUT" );
        response.getHttpHeaders().add( "Access-Control-Allow-Headers", IHttpHeaderNames.SERVICE_KEY + ", " + IHttpHeaderNames.AUTH_TOKEN );
        return response;
    }
}