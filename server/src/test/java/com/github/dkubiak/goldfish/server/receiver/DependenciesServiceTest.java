package com.github.dkubiak.goldfish.server.receiver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;
import org.neo4j.graphdb.Transaction;
import org.springframework.data.neo4j.core.GraphDatabase;

import com.github.dkubiak.goldfish.server.model.Module;
import com.github.dkubiak.goldfish.server.model.Project;
import com.github.dkubiak.goldfish.server.repository.ModuleRepository;
import com.github.dkubiak.goldfish.server.repository.ProjectRepository;

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

    @Mock
    GraphDatabase graphDatabase;

    @Test
    public void shouldAddNewProjectIfNotExists() {
        //given
        final Set<Module> modules = Sets.newSet(
                new Module.Builder().withArtifactId("a1").withGroupId("g1").build(),
                new Module.Builder().withArtifactId("a2").withGroupId("g2").build());

        Project project = getProjectOne(modules);

        when(projectRepository.findByNameAndRepository(PROJECT_1, REPO_1)).thenReturn(null);
        //when
        final Project newProject = dependenciesService.buildProject(project);

        //then;
        assertThat(newProject.getId()).isNull();

    }

    @Test
    public void shouldNotAddNewProjectAlreadyExists() {
        //given
        final Set<Module> modules = Sets.newSet(
                new Module.Builder().withArtifactId("a1").withGroupId("g1").build(),
                new Module.Builder().withArtifactId("a2").withGroupId("g2").build());

        Project project = getProjectOne(modules);
        Project findProject = getProjectOne(modules);


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

        Project project = getProjectOne(oldModules).copyWithouModulesAndDependecies();

        when(moduleRepository.findByGroupIdAndArtifactId("g2", "g2")).thenReturn(module2Old);
        when(moduleRepository.findByGroupIdAndArtifactId("g3", "g3")).thenReturn(module3);

        //when
        final Project projectWithNewModules = dependenciesService.addModulesToProject(project, newModules);

        //then;
        assertThat(projectWithNewModules.getModules()).hasSize(2);
        assertThat(projectWithNewModules.getModules()).containsExactly(module3, module2);
        assertThat(projectWithNewModules.getModules()
                .stream()
                .filter(m -> m.getArtifactId() == "a2")
                .filter(m -> m.getGroupId() == "g2")
                .findFirst().get().getId()).isNull();
    }

    @Test
    public void shoulAddDependecies() {
        //given
        final Set<Module> dependecies = Sets.newSet(
                new Module.Builder().withArtifactId("a1").withGroupId("g1").build(),
                new Module.Builder().withArtifactId("a2").withGroupId("g2").build());
        final Project project = getProjectOneWithDefaultModules();

        //when
        final Project projectWithDependecies = dependenciesService.addDependeciesToProject(project, dependecies);
        //then
        assertThat(projectWithDependecies.getDependecies()).hasSameElementsAs(dependecies);
    }

    @Test
    public void shouldReturnFalsePojectExist() {
        //given
        Project project = new Project.Builder()
                .withName(PROJECT_1)
                .withRepository(REPO_1).build();

        when(projectRepository.findByNameAndRepository(PROJECT_1, REPO_1)).thenReturn(project);
        //when
        assertThat(dependenciesService.isProject(project)).isTrue();

    }

    @Before
    public void setUp() {
        Mockito.when(graphDatabase.beginTx()).thenReturn(Mockito.mock(Transaction.class));
    }

    private Project getProjectOne(Set<Module> modules) {
        return new Project.Builder()
                .withName(PROJECT_1)
                .withRepository(REPO_1)
                .withModules(modules).build();
    }

    private Project getProjectOneWithDefaultModules() {
        final Set<Module> modules = Sets.newSet(
                new Module.Builder().withArtifactId("a1").withGroupId("g1").build(),
                new Module.Builder().withArtifactId("a2").withGroupId("g2").build());
        return new Project.Builder()
                .withName(PROJECT_1)
                .withRepository(REPO_1)
                .withModules(modules).build();
    }
}