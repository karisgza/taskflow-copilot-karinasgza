package com.taskflow.mapper;

import com.taskflow.dto.ProjectResponse;
import com.taskflow.dto.ProjectSummaryResponse;
import com.taskflow.model.Project;
import com.taskflow.model.TaskStatus;

import java.util.Map;

/**
 * ProjectMapper — puente DTO <-> dominio del lado Project. Estático, a mano, sin MapStruct.
 */
public final class ProjectMapper {

    private ProjectMapper() {
        // no instanciable
    }

    /** Entidad -> DTO de salida. */
    public static ProjectResponse aResponse(Project p) {
        return new ProjectResponse(p.getId(), p.getName(), p.getDescription(),
                p.getOwnerId(), p.getCreatedAt());
    }

    /** Conteos ya calculados por el service -> DTO de salida de GET /projects/{id}/summary. */
    public static ProjectSummaryResponse aSummary(Long projectId, String projectName,
                                                  long totalTasks, Map<TaskStatus, Long> byStatus,
                                                  long overdue) {
        return new ProjectSummaryResponse(projectId, projectName, totalTasks, byStatus, overdue);
    }

    /**
     * Construye el DTO de progreso para un proyecto dado los conteos ya calculados por el service.
     */
    public static com.taskflow.dto.ProjectProgressResponse aProgreso(Project proyecto, long totalTasks,
                                                                      long doneTasks, double percentDone) {
        return new com.taskflow.dto.ProjectProgressResponse(proyecto.getId(), proyecto.getName(),
                totalTasks, doneTasks, percentDone);
    }
}
