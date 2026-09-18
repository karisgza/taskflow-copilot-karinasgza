package com.taskflow.unit;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgresoProyectosServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void progreso_por_proyecto_calcula_porcentajes_y_orden() throws Exception {
        Project p1 = new Project(1L, "Plataforma TaskFlow", "d", 1L, null);
        Project p2 = new Project(2L, "App Móvil", "d", 1L, null);
        Project p3 = new Project(3L, "Migración Legacy", "d", 1L, null);

        when(projectRepository.findAll()).thenReturn(List.of(p3, p1, p2));

        // proyecto 1: 5 tareas, 1 DONE -> 20.0
        Task t1 = new Task(11L, "t01", "d", TaskStatus.DONE, Priority.MED, 1L, 1L, null);
                Task t2 = new Task(12L, "t02", "d", TaskStatus.TODO, Priority.MED, 1L, null, null);
                Task t3 = new Task(13L, "t03", "d", TaskStatus.TODO, Priority.MED, 1L, null, null);
                Task t4 = new Task(14L, "t04", "d", TaskStatus.TODO, Priority.MED, 1L, null, null);
                Task t5 = new Task(15L, "t05", "d", TaskStatus.TODO, Priority.MED, 1L, null, null);
        when(taskRepository.findByProjectId(1L)).thenReturn(List.of(t1, t2, t3, t4, t5));

        // proyecto 2: 3 tareas, 1 DONE -> 33.3
        Task u1 = new Task(21L, "u01", "d", TaskStatus.DONE, Priority.MED, 2L, 1L, null);
                Task u2 = new Task(22L, "u02", "d", TaskStatus.TODO, Priority.MED, 2L, null, null);
                Task u3 = new Task(23L, "u03", "d", TaskStatus.TODO, Priority.MED, 2L, null, null);
        when(taskRepository.findByProjectId(2L)).thenReturn(List.of(u1, u2, u3));

        // proyecto 3: sin tareas
        when(taskRepository.findByProjectId(3L)).thenReturn(List.of());

        var salida = projectService.progresoPorProyecto();

        assertEquals(3, salida.size());
        assertEquals(1L, salida.get(0).projectId());
        assertEquals(2L, salida.get(1).projectId());
        assertEquals(3L, salida.get(2).projectId());

        assertEquals(5L, salida.get(0).totalTasks());
        assertEquals(1L, salida.get(0).doneTasks());
        assertEquals(3L, salida.get(1).totalTasks());
        assertEquals(1L, salida.get(1).doneTasks());
        assertEquals(0L, salida.get(2).totalTasks());
        assertEquals(0L, salida.get(2).doneTasks());

        assertEquals(20.0, salida.get(0).percentDone());
        assertEquals(33.3, salida.get(1).percentDone());
        assertEquals(0.0, salida.get(2).percentDone());
    }
}
