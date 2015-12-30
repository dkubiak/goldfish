package com.github.dkubiak.goldfish.server.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.github.dkubiak.goldfish.server.model.Module;

public interface ModuleRepository extends GraphRepository<Module> {

    public Module findByGroupIdAndArtifactId(String groupId, String artifactId);

}
