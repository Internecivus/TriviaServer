package com.trivia.api;

import javax.ws.rs.core.Response;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */

public class WebApplicationException extends RuntimeException {
    private Response.Status statusCode;

    public WebApplicationException(Response.Status statusCode) {
        this.statusCode = statusCode;
        getResponse();
    }

    private Response getResponse() {

        return Response.status(statusCode).build();
    }
}