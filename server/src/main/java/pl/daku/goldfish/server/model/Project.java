package pl.daku.goldfish.server.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
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

    @RelatedTo(type = "CONTAINS", direction = Direction.OUTGOING)
    @Fetch
    public Set<Module> modules;

    @RelatedTo(type = "USED_IN", direction = Direction.INCOMING)
    @Fetch
    public Set<Module> dependecies;

    public void usedDependencie(Module module) {
        if (dependecies == null) {
            dependecies = new HashSet<>();
        }
        dependecies.add(module);
    }

    public void containModule(Module module) {
        if (modules == null) {
            modules = new HashSet<>();
        }
        modules.add(module);
    }

    public static class Builder {
        private Long id;
        private String name;
        private String repository;
        private Set<Module> modules;
        private Set<Module> dependecies;

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

        public Builder withDependecies(Set<Module> dependecies) {
            this.dependecies = dependecies;
            return this;
        }

        public Project build() {
            Project project = new Project();
            project.id = this.id;
            project.name = this.name;
            project.repository = this.repository;
            project.modules = this.modules;
            project.dependecies = this.dependecies;
            return project;
        }
    }

    public Project copyWithouModulesAndDependecies() {
        return new Builder().withId(id).withName(name).withRepository(repository).build();
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

    public Set<Module> getDependecies() {
        return Optional.ofNullable(dependecies).orElse(Collections.emptySet());
    }

    @Override
    public String toString() {
        return "Project{" +
                ", name='" + name + '\'' +
                ", repository='" + repository + '\'' +
                ", modules=" + modules +
                ", dependecies=" + dependecies +
                '}';
    }
}



