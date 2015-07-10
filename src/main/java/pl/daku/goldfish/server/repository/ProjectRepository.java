package pl.daku.goldfish.server.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.GraphRepository;

import pl.daku.goldfish.server.model.Project;

public interface ProjectRepository extends GraphRepository<Project> {

    public List<Project> findByName(String name);

}
