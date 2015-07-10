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
import pl.daku.goldfish.server.repository.ProjectRepository;

@RestController
public class DependenciesController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GraphDatabase graphDatabase;

    @RequestMapping(value = ReceiverRestURI.ADD_PROJECT, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity addProject(@RequestBody Project project) {
        Transaction tx = graphDatabase.beginTx();
        try {
            projectRepository.save(project);
            tx.success();
        } finally {
            tx.close();
        }

        return new ResponseEntity<Project>(project, HttpStatus.CREATED);
    }
}