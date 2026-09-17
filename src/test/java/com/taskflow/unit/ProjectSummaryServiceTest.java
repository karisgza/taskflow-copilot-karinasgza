package com.taskflow.unit;

import com.taskflow.dto.ProjectSummaryResponse;
import com.taskflow.exception.TaskValidationException;
import com.taskflow.model.Priority;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import com.taskflow.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectSummaryServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService service;

    private final Project proyecto = new Project(2L, "App Móvil", "d", 1L, null);

    @Test
    void resumen_cuentaEstadosYvencidas() throws TaskValidationException {
        when(taskRepository.findByProjectId(2L)).thenReturn(List.of(
                        new Task(5L, "Tarea 5", "d", TaskStatus.TODO, Priority.MED, 2L, null, null),
                        new Task(6L, "Tarea 6", "d", TaskStatus.IN_PROGRESS, Priority.HIGH, 2L, 2L, null),
                        new Task(7L, "Tarea 7", "d", TaskStatus.IN_PROGRESS, Priority.LOW, 2L, null, LocalDate.now().minusDays(1)),
                        new Task(8L, "Tarea 8", "d", TaskStatus.DONE, Priority.MED, 2L, 1L, LocalDate.now().minusDays(5))
        ));

        ProjectSummaryResponse esperado = new ProjectSummaryResponse(2L, "App Móvil", 4,
                Map.of(TaskStatus.TODO, 1L, TaskStatus.IN_PROGRESS, 2L, TaskStatus.DONE, 1L), 1L);

        assertEquals(esperado, service.resumen(proyecto));
    }

    @Test
    void resumen_proyectoSinTareas_todoEnCero() {
        when(taskRepository.findByProjectId(2L)).thenReturn(List.of());

        ProjectSummaryResponse esperado = new ProjectSummaryResponse(2L, "App Móvil", 0,
                Map.of(TaskStatus.TODO, 0L, TaskStatus.IN_PROGRESS, 0L, TaskStatus.DONE, 0L), 0L);

        assertEquals(esperado, service.resumen(proyecto));
    }
}
