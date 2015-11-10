package pl.daku.goldfish.server.model;

import java.util.Objects;
import java.util.Set;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@NodeEntity
@JsonIgnoreProperties({"projects"})
public class Module {

    @GraphId
    private Long id;
    private String groupId;
    private String artifactId;

    public Long getId() {
        return id;
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
            return module;
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
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
                "id=" + id +
                ", groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                '}';
    }
}




