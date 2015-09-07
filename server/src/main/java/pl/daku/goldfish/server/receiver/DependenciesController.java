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

import pl.daku.goldfish.server.model.Module;
import pl.daku.goldfish.server.model.Project;
import pl.daku.goldfish.server.repository.ModuleRepository;
import pl.daku.goldfish.server.repository.ProjectRepository;

@RestController
public class DependenciesController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private GraphDatabase graphDatabase;

    @RequestMapping(value = ReceiverRestURI.ADD_PROJECT, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity addProject(@RequestBody Project incomeProject) {
        Transaction tx = graphDatabase.beginTx();
        Project response;
        HttpStatus responseStatus;
        try {
            Project project = projectRepository
                    .findByNameAndRepository(incomeProject.getName(), incomeProject.getRepository());

            if (project == null) {
                //It's new project
                project = incomeProject.copyWithouModules();
                responseStatus = HttpStatus.CREATED;
            } else {
                project.removeAllModules();
                responseStatus = HttpStatus.OK;
            }
            final Project finalProject = project;
            addOrUpdateModule(incomeProject, finalProject);
            projectRepository.save(finalProject);
            response = finalProject;
            tx.success();
        } finally {
            tx.close();
        }
        return new ResponseEntity<Project>(response, responseStatus);
    }

    private void addOrUpdateModule(@RequestBody Project incomeProject, Project finalProject) {
        incomeProject.getModules().forEach((module) -> {
            Module existsModule = moduleRepository.findByGroupIdAndArtifactId(module.getGroupId(),
                    module.getArtifactId());
            if (existsModule != null) {
                finalProject.containModule(existsModule);
            } else {
                finalProject.containModule(module);
            }
        });
    }
}