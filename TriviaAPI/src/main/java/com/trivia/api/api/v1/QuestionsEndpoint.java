package com.trivia.api.api.v1;

import com.trivia.core.service.QuestionBean;
import com.trivia.persistence.dto.client.QuestionClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by faust. Part of MorbidTrivia Project. All rights reserved. 2018
 */

@Path("/questions")
public class QuestionsEndpoint {
    @Inject private QuestionBean questionBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQuestions(
            @DefaultValue("") @MatrixParam("category") String category,
            @DefaultValue("dateCreated") @MatrixParam("sortField") String sortField,
            @DefaultValue("1") @QueryParam("page") int pageCurrent,
            @DefaultValue("10") @QueryParam("size") int pageSize,
            @DefaultValue("") @QueryParam("sortOrder") String sortOrder
    ) {

        //List<QuestionEntity> questions = questionBean.findAll(page, size, sortField, SortOrder.valueOf(sortOrder), null, false);
            //throw new com.trivia.api.WebApplicationException(Response.Status.NOT_FOUND);
        category = "Horor";
        List<QuestionClient> questions = questionBean.getRandomForClient(pageSize, category);



        return Response.status(Response.Status.OK).entity(questions).build();
    }
}


