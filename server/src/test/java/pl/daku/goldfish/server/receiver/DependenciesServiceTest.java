package pl.daku.goldfish.server.receiver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import pl.daku.goldfish.server.model.Module;
import pl.daku.goldfish.server.model.Project;
import pl.daku.goldfish.server.repository.ModuleRepository;
import pl.daku.goldfish.server.repository.ProjectRepository;

@RunWith(MockitoJUnitRunner.class)
public class DependenciesServiceTest {

    public static final String REPO_1 = "Repo1";
    public static final String PROJECT_1 = "Project1";
    @InjectMocks
    DependenciesService dependenciesService;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    ModuleRepository moduleRepository;

    @Test
    public void shouldAddNewProjectIfNotExists() {
        //given
        final Set<Module> modules = Sets.newSet(
                new Module.Builder().withArtifactId("a1").withGroupId("g1").build(),
                new Module.Builder().withArtifactId("a2").withGroupId("g2").build());

        Project project = new Project.Builder()
                .withName(PROJECT_1)
                .withRepository(REPO_1)
                .withModules(modules).build();

        when(projectRepository.findByNameAndRepository(PROJECT_1, REPO_1)).thenReturn(null);
        //when
        final Project newProject = dependenciesService.buildProject(project);

        //then;
        assertThat(newProject.getId()).isNull();

    }

    @Test
    public void shouldNotAddNewProjectBecauseExists() {
        //given
        final Set<Module> modules = Sets.newSet(
                new Module.Builder().withArtifactId("a1").withGroupId("g1").build(),
                new Module.Builder().withArtifactId("a2").withGroupId("g2").build());

        Project project = new Project.Builder()
                .withName(PROJECT_1)
                .withRepository(REPO_1)
                .withModules(modules).build();

        Project findProject = new Project.Builder()
                .withName(PROJECT_1)
                .withRepository(REPO_1)
                .withModules(modules).build();


        when(projectRepository.findByNameAndRepository(PROJECT_1, REPO_1)).thenReturn(findProject);
        //when
        final Project newProject = dependenciesService.buildProject(project);

        //then;
        assertThat(newProject.getId()).isEqualTo(findProject.getId());
    }

    @Test
    public void shouldAddNewModulesIfNotExists() {
        //given
        final Module module1 = new Module.Builder().withArtifactId("a1").withGroupId("g1").build();
        final Module module2 = new Module.Builder().withArtifactId("a2").withGroupId("g2").build();
        final Module module2Old = new Module.Builder().withId(2l).withArtifactId("a2").withGroupId("g2").build();
        final Module module3 = new Module.Builder().withId(3l).withArtifactId("a3").withGroupId("g3").build();

        final Set<Module> oldModules = Sets.newSet(module1, module2Old);
        final Set<Module> newModules = Sets.newSet(module3, module2);

        Project project = new Project.Builder()
                .withName(PROJECT_1)
                .withRepository(REPO_1)
                .withModules(oldModules).build();

        when(moduleRepository.findByGroupIdAndArtifactId("g2", "g2")).thenReturn(module2Old);
        when(moduleRepository.findByGroupIdAndArtifactId("g3", "g3")).thenReturn(module3);

        //when
        final Project projectWithNewModules = dependenciesService.addModulesToProject(project, newModules);

        //then;
        assertThat(projectWithNewModules.getModules()).hasSize(2);
        assertThat(projectWithNewModules.getModules()).containsExactly(module3, module2);
    }

    @Test
    public void shouldEachModulesUseInTheSameProject() {
        //given
        final Set<Module> modules = Sets.newSet(
                new Module.Builder().withArtifactId("a1").withGroupId("g1").build(),
                new Module.Builder().withArtifactId("a2").withGroupId("g2").build());

        Project project = new Project.Builder()
                .withName(PROJECT_1)
                .withRepository(REPO_1)
                .withModules(modules).build();
        //when
        dependenciesService.setProjectToEachModules(project);
        //then
        project.getModules().stream().forEach(m -> Assertions.assertThat(m.getProjects()).contains(project));
    }

    @Test
    public void shouldReturnFalsePojectExist(){
        //given
        Project project = new Project.Builder()
                .withName(PROJECT_1)
                .withRepository(REPO_1).build();

        when(projectRepository.findByNameAndRepository(PROJECT_1, REPO_1)).thenReturn(project);
        //when
        Assertions.assertThat(dependenciesService.isProject(project)).isTrue();

    }

}