package pl.daku.goldfish.collector.maven;


import static java.util.stream.Collectors.toSet;

import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import pl.daku.goldfish.server.model.Module;
import pl.daku.goldfish.server.model.Project;

/**
 * Collect dependancy relations
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.NONE, aggregator = true)
public class GoldfishCollectorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(property = "ServerURL", defaultValue = "http://localhost:8080")
    private String serverURL;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        new GoldfishServiceManager(serverURL).addProject(getProjectStructure());
    }

    private Project getProjectStructure() {
        Set<Module> modules = project.getModules().stream()
                .map(moduleArtifactId -> new Module.Builder()
                        .withArtifactId(moduleArtifactId)
                        .withGroupId(project.getGroupId()).build()
                ).collect(toSet());

        Set<Module> dependecies = project
                .getDependencyManagement()
                .getDependencies().stream()
                .filter(d -> d.getGroupId().startsWith("com.payu."))
                .map(d -> new Module.Builder()
                        .withArtifactId(d.getArtifactId())
                        .withGroupId(d.getGroupId()).build()).collect(toSet());

        //Add parent as module
        modules.add(new Module.Builder()
                .withGroupId(project.getGroupId())
                .withArtifactId(project.getArtifactId()).build());

        Project projectStructure = new Project.Builder()
                .withName(project.getGroupId() + ":" + project.getArtifactId())
                .withRepository(project.getScm().getConnection())
                .withModules(modules)
                .withDependecies(dependecies).build();

        getLog().info("Project structure was prepared");
        getLog().info(projectStructure.toString());

        return projectStructure;
    }
}
