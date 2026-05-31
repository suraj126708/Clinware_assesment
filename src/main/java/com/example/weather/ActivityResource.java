package com.example.weather;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.util.Map;

@Path("/activity")
public class ActivityResource {

    @Inject
    ActivityService activityService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getRecommendation(
            @QueryParam("lat") double lat,
            @QueryParam("lon") double lon) {
        return activityService.getRecommendation(lat, lon);
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response status() {
        return Response.ok(
            Map.of(
                "status", "UP",
                "service", "Weather Activity Recommender",
                "timestamp", Instant.now().toString()
            )
        ).build();
    }
}
