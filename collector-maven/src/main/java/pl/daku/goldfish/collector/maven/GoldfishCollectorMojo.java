package pl.daku.goldfish.collector.maven;


import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.function.Function;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import pl.daku.goldfish.server.model.Module;
import pl.daku.goldfish.server.model.Project;
import retrofit.RetrofitError;

/**
 * Collect dependancy relation
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.NONE, aggregator = true)
public class GoldfishCollectorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Class.forName("retrofit.Callback");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        new GoldfishServiceManager("http://localhost:8080").addProject(getProjectStructure());
    }

    private Project getProjectStructure() {
        System.out.printf(RetrofitError.Kind.UNEXPECTED.name());
        Set<Module> modules = project.getModules().stream().map(new Function<String, Module>() {
            @Override
            public Module apply(String moduleArtifactId) {
                return new Module.Builder()
                        .withArtifactId(moduleArtifactId)
                        .withGroupId(project.getGroupId()).build();
            }
        }).collect(toSet());

        Set<Module> dependecies = project
                .getDependencyManagement()
                .getDependencies().stream()
                .filter(d->d.getGroupId().startsWith("com.payu."))
                .map(new Function<Dependency, Module>() {
                    @Override
                    public Module apply(Dependency dependency) {
                        return new Module.Builder()
                                .withArtifactId(dependency.getArtifactId())
                                .withGroupId(dependency.getGroupId()).build();
                    }
                }).collect(toSet());

        modules.addAll(dependecies);
        //TODO: Add parent as module
        modules.add(new Module.Builder().withGroupId(project.getGroupId()).withArtifactId(project.getArtifactId())
                .build());

        Project projectStructure = new Project.Builder()
                .withName(project.getGroupId() + ":" + project.getArtifactId())
                .withRepository(project.getScm().getConnection())
                .withModules(modules).build();

        getLog().info("Build project structure");
        getLog().info(projectStructure.toString());

        return projectStructure;
    }
}
