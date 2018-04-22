package com.trivia.api.api.v1;

import com.trivia.core.service.CategoryBean;
import com.trivia.persistence.entity.CategoryEntity;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */

@Path("/categories")
public class CategoriesEndpoint {
    @Inject private CategoryBean categoryBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategories() {
        List<CategoryEntity> categories = categoryBean.getAll();



        return Response.status(Response.Status.OK).entity(categories).build();
    }
}