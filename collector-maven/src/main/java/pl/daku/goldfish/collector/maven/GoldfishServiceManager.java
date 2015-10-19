package pl.daku.goldfish.collector.maven;


import pl.daku.goldfish.server.model.Project;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.*;
import retrofit.client.Response;

public class GoldfishServiceManager {

    //private static final Logger LOGGER = LoggerFactory.getLogger(GoldfishServiceManager.class);
    private String serviceEndpoint;
    private GoldfishService goldfishService;

    public GoldfishServiceManager(String serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
        goldfishService = buildRestAdapter(serviceEndpoint).create(GoldfishService.class);
    }

    public void addProject(Project project) {
       goldfishService.addProject(project);
               //, new Callback<String>() {
//            @Override
//            public void success(String project, Response response) {
//                System.out.println("OK");
//                //LOGGER.info("Project {} was added.", project.getName());
//                //LOGGER.debug("HTTP Status: {} Response: {}", response.getStatus(), response.getBody());
//            }
//
//            @Override
//            public void failure(RetrofitError retrofitError) {
//                System.out.println("NOtOK :(");
//                //LOGGER.error("Something wrong: HTTP Status {} and response: {}", retrofitError.getResponse().getStatus
//                //        (), retrofitError.getResponse().getBody());
//            }
//        }
//        );
    }

    private RestAdapter buildRestAdapter(String serviceEndpoint) {
        return new RestAdapter.Builder().setEndpoint(serviceEndpoint).setLogLevel(RestAdapter.LogLevel.FULL).build();
    }
}
