package pl.daku.goldfish.server;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;

@SpringBootApplication
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Configuration
    @EnableNeo4jRepositories("pl.daku.goldfish.server.repository")
    static class ApplicationConfig extends Neo4jConfiguration implements CommandLineRunner {

        public ApplicationConfig() {
            setBasePackage("pl.daku.goldfish.server");
        }

        @Bean
        GraphDatabaseService graphDatabaseService() {
            return new GraphDatabaseFactory().newEmbeddedDatabase("target/dependency-neo4j.db");
        }

        @Autowired
        GraphDatabaseService db;

        @Override
        public void run(String... strings) throws Exception {
            // Used for Neo4j browser
            // Deprecated, but very useful for way all-in-one
            // We don't need external neo4j, it's huge benefit.
            try {
                WrappingNeoServerBootstrapper neoServerBootstrapper;
                GraphDatabaseAPI api = (GraphDatabaseAPI) db;

                ServerConfigurator config = new ServerConfigurator(api);
                config.configuration()
                        .addProperty(Configurator.WEBSERVER_ADDRESS_PROPERTY_KEY, "0.0.0.0");
                config.configuration()
                        .addProperty(Configurator.WEBSERVER_PORT_PROPERTY_KEY, "8686");

                neoServerBootstrapper = new WrappingNeoServerBootstrapper(api, config);
                neoServerBootstrapper.start();
            } catch (Exception e) {
                log.error("Neo4j browser doesn't boot", e);
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
