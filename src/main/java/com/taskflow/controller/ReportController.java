package com.taskflow.controller;

import com.taskflow.dto.ProjectProgressResponse;
import com.taskflow.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Reports", description = "Endpoints para reportes generales del sistema")
@RestController
public class ReportController {

    private final ProjectService projectService;

    public ReportController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "Progreso por proyecto", description = "Devuelve el avance y conteos de tareas por proyecto")
    @GetMapping("/reports/progress")
    public List<ProjectProgressResponse> progresoPorProyecto() {
        return projectService.progresoPorProyecto();
    }
}
