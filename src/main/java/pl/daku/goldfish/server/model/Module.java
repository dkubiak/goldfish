package pl.daku.goldfish.server.model;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@NodeEntity
public class Module {

    @GraphId
    private Long id;
    private String groupId;
    private String artifactId;

    @RelatedTo(type = "USED_IN", direction = Direction.OUTGOING)
    @Fetch
    public Set<Project> projects;

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
}




