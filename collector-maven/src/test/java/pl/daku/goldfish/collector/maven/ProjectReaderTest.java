package pl.daku.goldfish.collector.maven;

import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

import pl.daku.goldfish.server.model.Module;

/**
 * Created by dawid.kubiak on 03/12/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectReaderTest {

    @InjectMocks
    private ProjectReader projectReader;

    @Mock
    private MavenProject project;

    @Mock
    private DependencyManagement dependencyManagement;


    @Test
    public void shoudlGetDependeciesFromDependenciesManagement() throws Exception {
        //given
        when(project.getDependencyManagement()).thenReturn(dependencyManagement);
        when(dependencyManagement.getDependencies()).thenReturn(Lists.newArrayList(buildDependecyObject()));

        //when
        final Set<Module> dependecies = projectReader.getDependecies(Optional.empty());

        //then
        Assertions.assertThat(dependecies).hasSize(1);
    }

    @Test
    public void shoudlDetectEmptyDependenciesManagement() throws Exception {
        //given
        when(project.getDependencyManagement()).thenReturn(null);

        //when
        final Set<Module> dependecies = projectReader.getDependecies(Optional.empty());

        //then
        Assertions.assertThat(dependecies).hasSize(0);
    }

    private Dependency buildDependecyObject() {
        final Dependency dependency = new Dependency();
        dependency.setGroupId("g1");
        dependency.setArtifactId("a1");
        return dependency;
    }

}