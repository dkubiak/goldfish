package pl.daku.goldfish.server.model;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
public class Project {

    @GraphId
    private Long id;
    private String name;
    private String repository;

    @RelatedTo(type = "CONTAINS", direction = Direction.INCOMING)
    @Fetch
    public Set<Module> modules;

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

        public Project build() {
            Project project = new Project();
            project.id = this.id;
            project.name = this.name;
            project.repository = this.repository;
            return project;
        }
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



