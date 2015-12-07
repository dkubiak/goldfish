package pl.daku.goldfish.collector.maven;

import static java.util.stream.Collectors.toSet;

import java.util.Optional;
import java.util.Set;

import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;

import pl.daku.goldfish.server.model.Module;

/**
 * Created by dawid.kubiak on 02/12/15.
 */
public class ProjectReader {

    private final MavenProject project;

    public ProjectReader(MavenProject project) {
        this.project = project;
    }

    public Set<Module> getModules() {
        return project.getModules().stream()
                .map(moduleArtifactId -> new Module.Builder()
                        .withArtifactId(moduleArtifactId)
                        .withGroupId(project.getGroupId()).build()
                ).collect(toSet());
    }

    public Set<Module> getDependecies(Optional<String> groupIdPrefix) throws Exception {
        return Optional.ofNullable(project.getDependencyManagement())
                .orElse(new DependencyManagement())
                .getDependencies().stream()
                .filter(d -> d.getGroupId().startsWith(groupIdPrefix.orElseGet(String::new)))
                .map(d -> new Module.Builder()
                        .withArtifactId(d.getArtifactId())
                        .withGroupId(d.getGroupId()).build()).collect(toSet());
    }

    public Module getParent() {
        return new Module.Builder()
                .withGroupId(project.getGroupId())
                .withArtifactId(project.getArtifactId()).build();
    }

    public String getName() {
        return project.getGroupId() + ":" + project.getArtifactId();
    }

    public String getRepository() {
        return project.getScm().getConnection();
    }

}
