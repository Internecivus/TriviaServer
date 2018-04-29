package com.trivia.api.api.v1;

import com.trivia.core.exception.BusinessException;
import com.trivia.persistence.entity.CategoryEntity;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/register")
public class ClientEndpoint {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response register() {
        return null;
    }
}
