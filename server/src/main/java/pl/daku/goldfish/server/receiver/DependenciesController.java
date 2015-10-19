package pl.daku.goldfish.server.receiver;

import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pl.daku.goldfish.server.model.Project;
import pl.daku.goldfish.server.repository.ModuleRepository;
import pl.daku.goldfish.server.repository.ProjectRepository;

@RestController
//TODO ADD require field
public class DependenciesController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private GraphDatabase graphDatabase;

    @Autowired
    private DependenciesService dependenciesService;

    @RequestMapping(value = ReceiverRestURI.ADD_PROJECT, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity addProject(@RequestBody Project incomeProject) {
        Transaction tx = graphDatabase.beginTx();
        Project response;
        HttpStatus responseStatus;
        try {
            responseStatus = dependenciesService.isProject(incomeProject) ? HttpStatus.OK : HttpStatus.CREATED;
            Project project = dependenciesService.buildProject(incomeProject);
            project = dependenciesService.addModulesToProject(project, incomeProject.getModules());
            project = dependenciesService.setProjectToEachModules(project);
            dependenciesService.saveDependecies(project);
            tx.success();
        } finally {
            tx.close();
        }
        return new ResponseEntity<String>("ok", responseStatus);
    }
}