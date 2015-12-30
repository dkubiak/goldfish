package com.github.dkubiak.goldfish.collector.maven;


import com.github.dkubiak.goldfish.server.model.Project;

import retrofit.http.Body;
import retrofit.http.PUT;

public interface GoldfishService {

    @PUT("/project/add")
    String addProject(@Body Project project);
}
