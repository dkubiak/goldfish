package pl.daku.goldfish.server.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@NodeEntity
@JsonIgnoreProperties({"projects"})
public class Module {

    @GraphId
    private Long id;
    private String groupId;
    private String artifactId;

    @RelatedTo(type = "USED_IN", direction = Direction.OUTGOING)
    @Fetch
    private Set<Project> projects;

    public void usedInProject(Project project) {
        if (projects == null) {
            projects = new HashSet<>();
        }
        projects.add(project);
    }

    public static class Builder {
        private Long id;
        private String groupId;
        private String artifactId;
        public Set<Project> projects;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder withArtifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        public Module build() {
            Module module = new Module();
            module.id = this.id;
            module.groupId = this.groupId;
            module.artifactId = this.artifactId;
            module.projects = this.projects;
            return module;
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Module module = (Module) o;
        return Objects.equals(groupId, module.groupId) &&
                Objects.equals(artifactId, module.artifactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId);
    }

    @Override
    public String toString() {
        return "Module{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                '}';
    }

}




