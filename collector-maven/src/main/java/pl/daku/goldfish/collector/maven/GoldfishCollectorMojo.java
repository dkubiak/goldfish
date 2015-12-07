package pl.daku.goldfish.collector.maven;


import java.util.Optional;
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

    @Parameter(property = "serverURL", defaultValue = "http://localhost:8080")
    private String serverURL;

    @Parameter(property = "groupIdMask", defaultValue = "com.payu.")
    private String groupIdMask;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            new GoldfishServiceManager(serverURL).addProject(getProjectStructure());
            getLog().info("Goldfish plugin finish with SUCCESS!");
        } catch (Exception e) {
            getLog().warn("Goldfish plugin failed!");
            getLog().warn(e.getMessage() + " (more information run with +X)");
            getLog().debug(e.getCause());
        }

    }

    private Project getProjectStructure() throws Exception {

        final ProjectReader projectReader = new ProjectReader(project);
        final Set<Module> modules = projectReader.getModules();

        //Add parent as module
        modules.add(projectReader.getParent());

        Project projectStructure = new Project.Builder()
                .withName(projectReader.getName())
                .withRepository(projectReader.getRepository())
                .withModules(modules)
                .withDependecies(projectReader.getDependecies(Optional.ofNullable(groupIdMask))).build();

        getLog().warn("Project structure was prepared!");
        getLog().info(projectStructure.toString());

        return projectStructure;
    }
}
