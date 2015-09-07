package pl.daku.goldfish.collector.maven;


import pl.daku.goldfish.server.model.Project;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface GoldfishEndpoint {

    @POST("/add/project")
    void addProject(@Body Project project, Callback callback);
}
