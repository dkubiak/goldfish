package com.github.dkubiak.goldfish.server.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.github.dkubiak.goldfish.server.model.Project;

public interface ProjectRepository extends GraphRepository<Project> {

    public Project findByNameAndRepository(String name, String repository);

}
