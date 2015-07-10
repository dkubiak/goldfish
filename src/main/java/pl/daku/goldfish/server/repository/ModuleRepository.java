package pl.daku.goldfish.server.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import pl.daku.goldfish.server.model.Module;

public interface ModuleRepository extends GraphRepository<Module> {

}
