package pl.daku.goldfish.collector.maven;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.daku.goldfish.server.model.Project;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class GoldfishServiceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoldfishServiceManager.class);
    private String serviceEndpoint;
    private GoldfishService goldfishService;

    public GoldfishServiceManager(String serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
        goldfishService = buildRestAdapter(serviceEndpoint).create(GoldfishService.class);
    }

    public void addProject(Project project) throws GoldfishServiceException {
        try {
            goldfishService.addProject(project);
        } catch (RetrofitError e) {
            throw new GoldfishServiceException("Connection problem with hoste: " + serviceEndpoint, e);
        }
    }

    private RestAdapter buildRestAdapter(String serviceEndpoint) {
        return new RestAdapter.Builder().setEndpoint(serviceEndpoint).build();
    }
}
