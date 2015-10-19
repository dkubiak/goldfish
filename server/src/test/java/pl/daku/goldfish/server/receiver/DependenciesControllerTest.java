package pl.daku.goldfish.server.receiver;

import static com.jayway.restassured.RestAssured.given;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

import pl.daku.goldfish.server.Application;
import pl.daku.goldfish.server.model.Module;
import pl.daku.goldfish.server.model.Project;
import pl.daku.goldfish.server.repository.ModuleRepository;
import pl.daku.goldfish.server.repository.ProjectRepository;


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
    public void should_add_project_with_modules() {
        Gson gson = new GsonBuilder().create();
        String body = gson.toJson(prepareProjectWithModules("ProjectName", prepareModules(2)));
        given().body(body).contentType(ContentType.JSON)
                .when().put(ReceiverRestURI.ADD_PROJECT)
                .then().statusCode(HttpStatus.SC_CREATED);
        Transaction tx = graphDatabase.beginTx();
        try {
            Project project = projectRepository.findByNameAndRepository("ProjectName", "repo");
            Assertions.assertThat(project.getId()).isNotNull();
            Assertions.assertThat(project.getModules()).hasSize(2);
        } finally {
            tx.success();
            tx.close();
        }
    }

    @Test
    public void should_add_project_with_the_same_modules() {
        Gson gson = new GsonBuilder().create();
        String body = gson.toJson(prepareProjectWithModules("Project1Name", prepareModules(2)));
        given().body(body).contentType(ContentType.JSON)
                .when().put(ReceiverRestURI.ADD_PROJECT)
                .then().statusCode(HttpStatus.SC_CREATED);

        body = gson.toJson(prepareProjectWithModules("Project2Name", prepareModules(3)));
        given().body(body).contentType(ContentType.JSON)
                .when().put(ReceiverRestURI.ADD_PROJECT)
                .then().statusCode(HttpStatus.SC_CREATED);

        Transaction tx = graphDatabase.beginTx();
        Project projectWithTwoModules = null;
        Project projectWithThreeModules = null;
        try {
            projectWithTwoModules = projectRepository.findByNameAndRepository("Project1Name", "repo");
            projectWithThreeModules = projectRepository.findByNameAndRepository("Project2Name", "repo");

            //then
            Assertions.assertThat(projectWithTwoModules.getModules()).hasSize(2);
            Assertions.assertThat(projectWithThreeModules.getModules()).hasSize(3);

            Assertions.assertThat(projectWithThreeModules.getModules()).usingElementComparatorOnFields("id")
                    .containsAll(projectWithTwoModules.getModules());

            Set<Project> usedProject = moduleRepository.findByGroupIdAndArtifactId(buildModule(1).getGroupId(),
                    buildModule(1).getArtifactId()).getProjects();
            Assertions.assertThat(usedProject).hasSize(1);

        } finally {
            tx.success();
            tx.close();
        }
    }

    @Test
    public void should_update_project_when_exists_add_new_module() {
        Gson gson = new GsonBuilder().create();
        given().body(gson.toJson(prepareProjectWithModules("Project1Name", prepareModules(2)))).contentType(
                ContentType.JSON)
                .when().put(ReceiverRestURI.ADD_PROJECT)
                .then().statusCode(HttpStatus.SC_CREATED);
        given().body(gson.toJson(prepareProjectWithModules("Project2Name", prepareModules(2)))).contentType(
                ContentType.JSON)
                .when().put(ReceiverRestURI.ADD_PROJECT)
                .then().statusCode(HttpStatus.SC_CREATED);
        given().body(gson.toJson(prepareProjectWithModules("Project1Name", prepareModules(3)))).contentType(
                ContentType.JSON)
                .when().put(ReceiverRestURI.ADD_PROJECT)
                .then().statusCode(HttpStatus.SC_OK);

        Transaction tx = graphDatabase.beginTx();
        Project projectWithTwoModules = null;
        Project projectWithThreeModules = null;
        try {
            projectWithThreeModules = projectRepository.findByNameAndRepository("Project1Name", "repo");
            projectWithTwoModules = projectRepository.findByNameAndRepository("Project2Name", "repo");

            //then
            Assertions.assertThat(projectWithTwoModules.getModules()).hasSize(2);
            Assertions.assertThat(projectWithThreeModules.getModules()).hasSize(3);

            Assertions.assertThat(projectWithThreeModules.getModules()).usingElementComparatorOnFields("id")
                    .containsAll(projectWithTwoModules.getModules());
        } finally {
            tx.success();
            tx.close();
        }
    }


    @Test
    public void should_update_project_when_exists_remove_module() {
        Gson gson = new GsonBuilder().create();
        given().body(gson.toJson(prepareProjectWithModules("Project1Name", prepareModules(3)))).contentType(
                ContentType.JSON)
                .when().put(ReceiverRestURI.ADD_PROJECT)
                .then().statusCode(HttpStatus.SC_CREATED);
        given().body(gson.toJson(prepareProjectWithModules("Project2Name", prepareModules(2)))).contentType(
                ContentType.JSON)
                .when().put(ReceiverRestURI.ADD_PROJECT)
                .then().statusCode(HttpStatus.SC_CREATED);
        given().body(gson.toJson(prepareProjectWithModules("Project1Name", prepareModules(2)))).contentType(
                ContentType.JSON)
                .when().put(ReceiverRestURI.ADD_PROJECT)
                .then().statusCode(HttpStatus.SC_OK);

        Transaction tx = graphDatabase.beginTx();
        Project projectOneWithTwoTheSameModules = null;
        Project projectTwoWithTwoTheSameModules = null;
        try {
            projectOneWithTwoTheSameModules = projectRepository.findByNameAndRepository("Project1Name", "repo");
            projectTwoWithTwoTheSameModules = projectRepository.findByNameAndRepository("Project2Name", "repo");

            //then
            Assertions.assertThat(projectOneWithTwoTheSameModules.getModules()).hasSize(2);
            Assertions.assertThat(projectTwoWithTwoTheSameModules.getModules()).hasSize(2);

            Assertions.assertThat(projectOneWithTwoTheSameModules.getModules()).usingElementComparatorOnFields("id")
                    .containsAll(projectTwoWithTwoTheSameModules.getModules());
        } finally {
            tx.success();
            tx.close();
        }
    }

    private Project prepareProjectWithModules(String projectName, Set<Module> modules) {
        return new Project.Builder().withName(projectName).withRepository("repo").withModules(modules).build();
    }

    private Set<Module> prepareModules(int count) {
        return IntStream.range(0, count).mapToObj(i -> buildModule(i)).collect(Collectors.<Module>toSet());
    }

    private Module buildModule(int i) {
        return new Module.Builder().withArtifactId("ArtifactId_" + i).withGroupId("GroupId_" + i).build();
    }
}
