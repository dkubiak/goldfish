package pl.daku.goldfish.collector.maven;


import com.squareup.okhttp.Call;

import pl.daku.goldfish.server.model.Project;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.PUT;

public interface GoldfishService {

    @PUT("/project/add")
    String addProject(@Body Project project);
}
