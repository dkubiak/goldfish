package com.github.dkubiak.goldfish.server.receiver;

import static java.util.Optional.ofNullable;

import java.util.Set;

import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.stereotype.Component;

import com.github.dkubiak.goldfish.server.repository.ModuleRepository;
import com.github.dkubiak.goldfish.server.model.Module;
import com.github.dkubiak.goldfish.server.model.Project;
import com.github.dkubiak.goldfish.server.repository.ProjectRepository;

@Component
public class DependenciesService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ModuleRepository moduleRepository;

    @Autowired
    private GraphDatabase graphDatabase;

    public Project buildProject(Project project) {
        return ofNullable(projectRepository.findByNameAndRepository(project.getName(), project.getRepository()))
                .orElse(project)
                .copyWithouModulesAndDependecies();
    }

    public Project addModulesToProject(Project project, Set<Module> newModules) {
        Transaction tx = graphDatabase.beginTx();
        try {
            newModules.stream()
                    .forEach(m -> addModules(project, m));
            projectRepository.save(project);
            tx.success();
        } finally {
            tx.close();
        }
        return project;
    }

    public Project addDependeciesToProject(Project project, Set<Module> newDependecies) {
        Transaction tx = graphDatabase.beginTx();
        try {
            newDependecies.stream()
                    .forEach(d -> addDependecies(project, d));
            projectRepository.save(project);
            tx.success();
        } finally {
            tx.close();
        }
        return project;
    }

    private void addModules(Project project, Module m) {
        project.containModule(findModuleIntoRepository(m));
    }

    private void addDependecies(Project project, Module d) {
        project.usedDependencie(findModuleIntoRepository(d));
    }

    private Module findModuleIntoRepository(Module d) {
        return ofNullable(moduleRepository.findByGroupIdAndArtifactId(d.getGroupId(), d.getArtifactId()))
                .orElse(d);
    }

    public Boolean isProject(Project project) {
        return projectRepository.findByNameAndRepository(project.getName(),
                project.getRepository()) != null ? true : false;
    }
}
