package com.trivia.api.api.v1;

import com.trivia.core.service.ClientService;
import com.trivia.persistence.entity.Client;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/client")
public class ClientEndpoint {
    private @Inject ClientService clientService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response postClient(
            @QueryParam("register") boolean register,
            @QueryParam("providerKey") String providerKey,
            @QueryParam("providerSecret") String providerSecret
    ) {
        if (register) return registerClient(providerKey, providerSecret);

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private Response registerClient(String providerKey, String providerSecret) {
        Client clientEntity = clientService.registerFor(providerKey, providerSecret);
        return Response.status(Response.Status.OK).entity(clientEntity).build();
    }
}
