package pl.daku.goldfish.collector.maven;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import pl.daku.goldfish.server.model.Project;

public class GoldfishServiceManagerTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Test
    public void should_add_project_and_recive_http_status_ok() {
        //given
        stubFor(put(urlEqualTo("/project/add"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .willReturn(aResponse()
                        .withStatus(200).withBody("ok")));
        //when
        new GoldfishServiceManager("http://localhost:8089")
                .addProject(new Project.Builder().withName("1").build());

        //then
        WireMock.verify(1, putRequestedFor(urlEqualTo("/project/add"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8")));
    }
}
