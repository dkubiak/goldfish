package pl.daku.goldfish.collector.maven;

import java.nio.charset.Charset;

import org.eclipse.jetty.http.HttpStatus;

import net.jadler.JadlerMocker;
import net.jadler.stubbing.server.jetty.JettyStubHttpServer;

/**
 * Created by dawid.kubiak on 29/12/15.
 */
public class GoldfishServiceMock {

    public void mockEndpointAddProject(int port) {
        final JadlerMocker jadlerMocker = new JadlerMocker(new JettyStubHttpServer(8089));
        jadlerMocker.start();
        jadlerMocker.onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo("/project/add")
                .respond()
                .withStatus(HttpStatus.OK_200)
                .withBody("ok")
                .withEncoding(Charset.forName("UTF-8"))
                .withContentType("application/json; charset=UTF-8");
    }
}
