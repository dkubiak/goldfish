package com.github.dkubiak.goldfish.server.receiver;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.github.dkubiak.goldfish.server.Application;
import com.github.dkubiak.goldfish.server.model.Module;
import com.github.dkubiak.goldfish.server.model.Project;
import com.github.dkubiak.goldfish.server.repository.ModuleRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

import com.github.dkubiak.goldfish.server.repository.ProjectRepository;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:8080")
public class DependenciesControllerTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private GraphDatabase graphDatabase;

    @Value("${server.port}")
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @After
    public void clearDB() {
        Transaction tx = graphDatabase.beginTx();
        projectRepository.deleteAll();
        moduleRepository.deleteAll();
        tx.success();
        tx.close();
    }

    @Test
    public void should_add_project_with_modules_and_dependecies() {
        //given
        addAndSendProject("ProjectName", generateModules(2), generateDependencies(4));
        Transaction tx = graphDatabase.beginTx();
        try {
            Project project = projectRepository.findByNameAndRepository("ProjectName", "repo");
            assertThat(project.getId()).isNotNull();
            assertThat(project.getModules()).hasSize(2);
            assertThat(project.getDependecies()).hasSize(4);
        } finally {
            tx.success();
            tx.close();
        }
    }

    @Test
    public void should_add_project_with_the_same_modules_and_dependencies() {
        //given
        addAndSendProject("Project1Name", generateModules(2), generateDependencies(4));
        addAndSendProject("Project2Name", generateModules(3), generateDependencies(8));

        Transaction tx = graphDatabase.beginTx();
        try {
            Project projectWithTwoModules = projectRepository.findByNameAndRepository("Project1Name", "repo");
            Project projectWithThreeModules = projectRepository.findByNameAndRepository("Project2Name", "repo");

            //then
            assertThat(projectWithTwoModules.getModules()).hasSize(2);
            assertThat(projectWithThreeModules.getModules()).hasSize(3);

            assertThat(projectWithTwoModules.getDependecies()).hasSize(4);
            assertThat(projectWithThreeModules.getDependecies()).hasSize(8);

            assertThat(projectWithThreeModules.getModules()).usingElementComparatorOnFields("id")
                    .containsAll(projectWithTwoModules.getModules());
            assertThat(projectWithThreeModules.getDependecies()).usingElementComparatorOnFields("id")
                    .containsAll(projectWithTwoModules.getDependecies());
        } finally {
            tx.success();
            tx.close();
        }
    }

    @Test
    public void should_update_project_when_exists_add_new_module() {
        //given
        addAndSendProject("Project1Name", generateModules(2), generateModules(4));
        addAndSendProject("Project2Name", generateModules(2), generateModules(2));
        updateAndSendProject("Project1Name", generateModules(3));

        Transaction tx = graphDatabase.beginTx();
        try {
            Project projectWithThreeModules = projectRepository.findByNameAndRepository("Project1Name", "repo");
            Project projectWithTwoModules = projectRepository.findByNameAndRepository("Project2Name", "repo");

            //then
            assertThat(projectWithTwoModules.getModules()).hasSize(2);
            assertThat(projectWithThreeModules.getModules()).hasSize(3);

            assertThat(projectWithThreeModules.getModules()).usingElementComparatorOnFields("id")
                    .containsAll(projectWithTwoModules.getModules());
        } finally {
            tx.success();
            tx.close();
        }
    }

    @Test
    public void should_update_exists_project_with_modules() {
        //given
        addAndSendProject("Project1Name", generateModules(3), generateModules(3));
        addAndSendProject("Project2Name", generateModules(2), generateModules(3));
        updateAndSendProject("Project1Name", generateModules(2));

        Transaction tx = graphDatabase.beginTx();
        try {
            Project projectOneWithTwoTheSameModules = projectRepository.findByNameAndRepository("Project1Name", "repo");
            Project projectTwoWithTwoTheSameModules = projectRepository.findByNameAndRepository("Project2Name", "repo");

            //then
            assertThat(projectOneWithTwoTheSameModules.getModules()).hasSize(2);
            assertThat(projectTwoWithTwoTheSameModules.getModules()).hasSize(2);

            assertThat(projectOneWithTwoTheSameModules.getModules()).usingElementComparatorOnFields("id")
                    .containsAll(projectTwoWithTwoTheSameModules.getModules());
        } finally {
            tx.success();
            tx.close();
        }
    }

    private Gson addAndSendProject(String projectName, Set<Module> modules, Set<Module> dependecies) {
        Gson gson = new GsonBuilder().create();
        String body = gson.toJson(prepareProjectWithModulesAndDependencies(projectName, modules, dependecies));
        given().body(body).contentType(ContentType.JSON)
                .when().put(ReceiverRestURI.ADD_PROJECT)
                .then().statusCode(HttpStatus.SC_CREATED);
        return gson;
    }

    private Gson updateAndSendProject(String projectName, Set<Module> modules) {
        Gson gson = new GsonBuilder().create();
        String body = gson.toJson(prepareProjectWithModules(projectName, modules));
        given().body(body).contentType(ContentType.JSON)
                .when().put(ReceiverRestURI.ADD_PROJECT)
                .then().statusCode(HttpStatus.SC_OK);
        return gson;
    }

    private Project prepareProjectWithModules(String projectName, Set<Module> modules) {
        return new Project.Builder().withName(projectName).withRepository("repo").withModules(modules).build();
    }

    private Project prepareProjectWithModulesAndDependencies(String projectName, Set<Module> modules, Set<Module>
            dependencies) {
        return new Project.Builder().withName(projectName)
                .withRepository("repo")
                .withModules(modules)
                .withDependecies(dependencies).build();
    }

    private Set<Module> generateDependencies(int count) {
        return generateModules(count);
    }

    private Set<Module> generateModules(int count) {
        return IntStream.range(0, count).mapToObj(i -> buildModule(i)).collect(Collectors.<Module>toSet());
    }

    private Module buildModule(int i) {
        return new Module.Builder().withArtifactId("ArtifactId_" + i).withGroupId("GroupId_" + i).build();
    }
}
