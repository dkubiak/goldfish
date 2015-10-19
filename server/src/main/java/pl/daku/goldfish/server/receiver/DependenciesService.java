package pl.daku.goldfish.server.receiver;

import static java.util.Optional.ofNullable;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.daku.goldfish.server.model.Module;
import pl.daku.goldfish.server.model.Project;
import pl.daku.goldfish.server.repository.ModuleRepository;
import pl.daku.goldfish.server.repository.ProjectRepository;

@Component
public class DependenciesService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ModuleRepository moduleRepository;

    public Project buildProject(Project project) {
        return ofNullable(projectRepository.findByNameAndRepository(project.getName(), project.getRepository()))
                .orElse(project);
    }

    public Project addModulesToProject(Project project, Set<Module> newModules) {
        Project finalProject = project.copyWithouModules();
        newModules.stream()
                .forEach(m -> {
                    finalProject.containModule(
                            ofNullable(
                                    moduleRepository.findByGroupIdAndArtifactId(m.getGroupId(), m.getArtifactId()))
                                    .orElse(m));
                });
        return finalProject;
    }

    public Project setProjectToEachModules(Project project) {
        project.getModules().stream()
                .forEach(m -> m.usedInProject(project));
        return project;
    }

    public Boolean isProject(Project project) {
        return projectRepository.findByNameAndRepository(project.getName(),
                project.getRepository()) != null ? true : false;
    }

    public void saveDependecies(Project project) {
        projectRepository.save(project);
    }
}
