package com.taskflow.dto;

/**
 * DTO de respuesta para GET /reports/progress — progreso agregado por proyecto.
 * projectId: id del proyecto
 * projectName: nombre
 * totalTasks: total de tareas
 * doneTasks: tareas en DONE
 * percentDone: porcentaje completado, redondeado a 1 decimales
 */
public record ProjectProgressResponse(Long projectId, String projectName, long totalTasks, long doneTasks, double percentDone) {
}
