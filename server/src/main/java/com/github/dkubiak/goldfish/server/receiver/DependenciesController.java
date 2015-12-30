package com.github.dkubiak.goldfish.server.receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.dkubiak.goldfish.server.model.Project;

@RestController
//TODO ADD require field
public class DependenciesController {

    @Autowired
    private DependenciesService dependenciesService;

    @RequestMapping(value = ReceiverRestURI.ADD_PROJECT, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity addProject(@RequestBody Project incomeProject) {
        Project response;
        HttpStatus responseStatus;
        responseStatus = dependenciesService.isProject(incomeProject) ? HttpStatus.OK : HttpStatus.CREATED;
        Project project = dependenciesService.buildProject(incomeProject);
        dependenciesService.addModulesToProject(project, incomeProject.getModules());
        dependenciesService.addDependeciesToProject(project, incomeProject.getDependecies());

        return new ResponseEntity<String>(responseStatus);
    }
}