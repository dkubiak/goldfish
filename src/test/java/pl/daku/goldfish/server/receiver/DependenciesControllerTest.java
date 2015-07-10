package pl.daku.goldfish.server.receiver;

import static com.jayway.restassured.RestAssured.given;

import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
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
import com.jayway.restassured.internal.assertion.Assertion;
import com.jayway.restassured.mapper.factory.GsonObjectMapperFactory;

import pl.daku.goldfish.server.Application;
import pl.daku.goldfish.server.model.Project;
import pl.daku.goldfish.server.repository.ProjectRepository;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:8080")
public class DependenciesControllerTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GraphDatabase graphDatabase;

    @Value("${server.port}")
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void should_add_project_with_modules() {
        Gson gson = new GsonBuilder().create();
        String body = gson.toJson(new Project.Builder().withName("ProjectName").build());
        given().body(body).contentType(ContentType.JSON)
                .when().put(ReceiverRestURI.ADD_PROJECT)
                .then().statusCode(HttpStatus.SC_CREATED);
        Transaction tx = graphDatabase.beginTx();
        try {
            Project project = projectRepository.findByName("ProjectName").get(0);
            Assertions.assertThat(project.getId()).isNotNull();
            projectRepository.delete(project);
            tx.success();
        } finally {
            tx.close();
        }
    }
}
