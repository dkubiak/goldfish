package pl.daku.goldfish.server.model;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
public class Project {

    @GraphId
    private Long id;
    private String name;
    private String repository;

    @RelatedTo(type = "CONTAINS", direction = Direction.INCOMING)
    @Fetch
    public Set<Module> modules;

    public void containModule(Module module) {
        if (modules == null) {
            modules = new HashSet<>();
        }
        modules.add(module);
    }

    public void removeModule(Module module) {
        if (modules == null) {
            modules = new HashSet<>();
        }
        modules.remove(module);
    }

    public void removeAllModules() {
        modules.clear();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String repository;
        private Set<Module> modules;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withRepository(String repository) {
            this.repository = repository;
            return this;
        }

        public Builder withModules(Set<Module> modules) {
            this.modules = modules;
            return this;
        }

        public Project build() {
            Project project = new Project();
            project.id = this.id;
            project.name = this.name;
            project.repository = this.repository;
            project.modules = this.modules;
            return project;
        }
    }

    public Project copyWithouModules() {
        return new Builder().withName(name).withRepository(repository).build();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRepository() {
        return repository;
    }

    public Set<Module> getModules() {
        return modules;
    }
}



