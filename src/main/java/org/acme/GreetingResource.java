package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
//@Consumes(MediaType.APPLICATION_JSON)
public class GreetingResource {

    @Inject
    ServiceConfig serviceConfig;

    @Inject SimpleConfig simpleConfig;

    @GET
    public String hello() {
        final StringBuilder result = new StringBuilder("Hello from:\n");

        serviceConfig.environments()
            .forEach(environment -> {
                result.append("Source: ").append(environment.source()).append("\n");
                result.append("Service: ").append(environment.service()).append("\n");
                result.append("Destination: ").append(environment.destination()).append("\n");
                result.append("---------------\n");
            });

        return result.toString();
    }

    @PUT
    @Path("/{id}")
    public void delete(@PathParam("id") final String id, @HeaderParam("Content-Type") final MediaType contentType, final JsonBody jsonBody) {
        System.out.println("Content-Type: " + contentType.getParameters());

        System.out.println("jsonBody: " + jsonBody);
    }


    @POST
    @Path("/post")
    public String doPost(final JsonBody jsonBody,  @HeaderParam("Content-Type") final MediaType contentType) {
        System.out.println("Content-Type: " + contentType.getParameters());

        System.out.println("jsonBody: " + jsonBody);

        return "Hello from RESTEasy";
    }
}
