package com.trivia.api.exception;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationException implements ExceptionMapper<javax.validation.ConstraintViolationException> {
    @Override
    public Response toResponse(javax.validation.ConstraintViolationException e) {
        ConstraintViolation cv = (ConstraintViolation) e.getConstraintViolations();
        return Response.status(Response.Status.PAYMENT_REQUIRED).entity(cv).build();
    }
}
