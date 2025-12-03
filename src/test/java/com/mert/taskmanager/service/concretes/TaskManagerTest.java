package com.mert.taskmanager.service.concretes;

import com.mert.taskmanager.config.SecurityUtils;
import com.mert.taskmanager.core.exceptions.AccessDeniedException;
import com.mert.taskmanager.core.exceptions.ResourceNotFoundException;
import com.mert.taskmanager.core.mapper.TaskMapper;
import com.mert.taskmanager.dto.request.task.TaskSaveRequest;
import com.mert.taskmanager.dto.request.task.TaskUpdateRequest;
import com.mert.taskmanager.dto.response.TaskResponse;
import com.mert.taskmanager.entity.Project;
import com.mert.taskmanager.entity.Task;
import com.mert.taskmanager.entity.User;
import com.mert.taskmanager.repository.ProjectRepo;
import com.mert.taskmanager.repository.TaskRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    private  TaskManager taskManager;

    private   TaskRepo taskRepo ;
    private  ProjectRepo projectRepo;
    private   TaskMapper taskMapper;



    @BeforeEach//Test seneryasou  çalışmdan önce çalışcak olan özel method  kısım burası
    void setUp() {
        //Yalancı nesneler diyelim bunlara taskmannager cosnturactırını lazım olam parametredeki nesneleri yaratıp taklit etmek için
        taskRepo= Mockito.mock(TaskRepo.class);
        projectRepo= Mockito.mock(ProjectRepo.class);
        taskMapper= Mockito.mock(TaskMapper.class);


        taskManager=new TaskManager(taskRepo,projectRepo,taskMapper);

    }


    @Test
    public void save_WhenValidRequest_ShouldReturnTaskResponse() {
        LocalDateTime fixedDate = LocalDateTime.now();
        TaskSaveRequest req = new TaskSaveRequest();
        req.setName("TaskName");
        req.setDescription("TaskDesc");
        req.setProjectId(1L);
        req.setDueDate(LocalDate.from(fixedDate));

        User currentUser = new User();
        currentUser.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setUser(currentUser);

        Task mappedTask = new Task();
        mappedTask.setName("TaskName");
        mappedTask.setDescription("TaskDesc");
        mappedTask.setDueDate(req.getDueDate());


        TaskResponse response = new TaskResponse();
        response.setId(10L);
        response.setName("TaskName");
        response.setDescription("TaskDesc");
        response.setDueDate(mappedTask.getDueDate());


        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {

            mock.when(SecurityUtils::getCurrentUser).thenReturn(currentUser);

            Mockito.when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
            Mockito.when(taskMapper.toEntity(req)).thenReturn(mappedTask);
            Mockito.when(taskRepo.save(mappedTask)).thenReturn(mappedTask);
            Mockito.when(taskMapper.toResponse(mappedTask)).thenReturn(response);

            TaskResponse result = taskManager.save(req);

            assertNotNull(result);
            assertEquals("TaskName", result.getName());
            Mockito.verify(taskMapper).toEntity(req);
            Mockito.verify(taskRepo).save(mappedTask);
        }
    }

    @Test
    public void save_ProjectNotFound_ShouldThrow() {

        TaskSaveRequest req = new TaskSaveRequest();
        req.setProjectId(1L);

        User user = new User();
        user.setId(1L);

        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(user);

            Mockito.when(projectRepo.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> taskManager.save(req));
        }
    }

    @Test
    public void save_AccessDenied_ShouldThrow() {
        TaskSaveRequest req = new TaskSaveRequest();
        req.setProjectId(1L);

        User owner = new User();
        owner.setId(1L);

        User hacker = new User();
        hacker.setId(2L);

        Project project = new Project();
        project.setId(1L);
        project.setUser(owner);

        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(hacker);

            Mockito.when(projectRepo.findById(1L)).thenReturn(Optional.of(project));

            assertThrows(AccessDeniedException.class, () -> taskManager.save(req));
        }
    }

    @Test
    public void get_WhenValidRequest_ShouldReturnTaskResponse() {
        User user = new User();
        user.setId(1L);

        Project project = new Project();
        project.setId(2L);
        project.setUser(user);

        Task task = new Task();
        task.setId(10L);
        task.setProject(project);

        TaskResponse response = new TaskResponse();
        response.setId(10L);

        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(user);

            Mockito.when(taskRepo.findById(10L)).thenReturn(Optional.of(task));
            Mockito.when(taskMapper.toResponse(task)).thenReturn(response);

            TaskResponse result = taskManager.get(10L);

            assertNotNull(result);
            assertEquals(10L, result.getId());
        }
    }

    @Test
    public void get_ResourceNotFound_ShouldThrow() {
        User user = new User();
        user.setId(1L);

        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(user);

            Mockito.when(taskRepo.findById(10L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> taskManager.get(10L));
        }
    }

    @Test
    public void get_AccessDenied_ShouldThrow() {
        User owner = new User();
        owner.setId(1L);

        User hacker = new User();
        hacker.setId(2L);

        Project project = new Project();
        project.setUser(owner);

        Task task = new Task();
        task.setProject(project);

        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(hacker);
            Mockito.when(taskRepo.findById(10L)).thenReturn(Optional.of(task));

            assertThrows(AccessDeniedException.class, () -> taskManager.get(10L));
        }
    }

    @Test
    public void update_WhenValidRequest_ShouldReturnTaskResponse() {
        LocalDateTime fixedDate = LocalDateTime.now();
        User user = new User();
        user.setId(1L);

        TaskUpdateRequest req = new TaskUpdateRequest();
        req.setId(10L);
        req.setName("NewName");
        req.setDescription("NewDesc");
        req.setProjectId(2L);
        req.setDueDate(LocalDate.from(fixedDate));

        Project project = new Project();
        project.setId(2L);
        project.setUser(user);

        Task oldTask = new Task();
        oldTask.setId(10L);
        oldTask.setProject(project);
        oldTask.setDueDate(LocalDate.from(fixedDate));
        oldTask.setName("OldName");
        oldTask.setDescription("OldDesc");


        Task updated = new Task();
        updated.setId(10L);
        updated.setProject(project);
        updated.setDueDate(req.getDueDate());
        updated.setName(req.getName());
        updated.setDescription(req.getDescription());

        TaskResponse response = new TaskResponse();
        response.setId(10L);
        response.setDueDate(LocalDate.from(fixedDate));
        response.setName("NewName");
        response.setDescription("NewDesc");
        response.setProjectId(project.getId());


        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(user);

            Mockito.when(taskRepo.findById(10L)).thenReturn(Optional.of(oldTask));
            Mockito.when(projectRepo.findById(2L)).thenReturn(Optional.of(project));
            Mockito.when(taskMapper.toEntity(req)).thenReturn(updated);
            Mockito.when(taskRepo.save(updated)).thenReturn(updated);
            Mockito.when(taskMapper.toResponse(updated)).thenReturn(response);

            TaskResponse result = taskManager.update(req);

            assertEquals(10L, result.getId());
        }
    }

    @Test
    public void update_TaskNotFound_ShouldThrow() {
        User user = new User();
        user.setId(1L);

        TaskUpdateRequest req = new TaskUpdateRequest();
        req.setId(10L);

        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(user);

            Mockito.when(taskRepo.findById(10L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> taskManager.update(req));
        }
    }

    @Test
    public void update_ProjectNotFound_ShouldThrow() {
        User user = new User();
        user.setId(1L);

        TaskUpdateRequest req = new TaskUpdateRequest();
        req.setId(10L);
        req.setProjectId(2L);

        Task existingTask = new Task();
        existingTask.setId(10L);

        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(user);

            Mockito.when(taskRepo.findById(10L)).thenReturn(Optional.of(existingTask));
            Mockito.when(projectRepo.findById(2L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> taskManager.update(req));
        }
    }

    @Test
    public void update_AccessDenied_ShouldThrow() {
        User owner = new User();
        owner.setId(1L);

        User hacker = new User();
        hacker.setId(5L);

        Project project = new Project();
        project.setId(2L);
        project.setUser(owner);

        Task task = new Task();
        task.setId(10L);
        task.setProject(project);

        TaskUpdateRequest req = new TaskUpdateRequest();
        req.setId(10L);
        req.setProjectId(2L);

        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(hacker);

            Mockito.when(taskRepo.findById(10L)).thenReturn(Optional.of(task));
            Mockito.when(projectRepo.findById(2L)).thenReturn(Optional.of(project));

            assertThrows(AccessDeniedException.class, () -> taskManager.update(req));
        }
    }

    @Test
    public void delete_WhenValid_ShouldDelete() {
        User user = new User();
        user.setId(1L);

        Project project = new Project();
        project.setUser(user);

        Task task = new Task();
        task.setProject(project);

        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(user);

            Mockito.when(taskRepo.findById(10L)).thenReturn(Optional.of(task));

            boolean result = taskManager.delete(10L);

            assertTrue(result);
            Mockito.verify(taskRepo).delete(task);
        }
    }

    @Test
    public void delete_NotFound_ShouldThrow() {
        User user = new User();
        user.setId(1L);

        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(user);

            Mockito.when(taskRepo.findById(10L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> taskManager.delete(10L));
        }
    }

    @Test
    public void delete_AccessDenied_ShouldThrow() {
        User owner = new User();
        owner.setId(1L);

        User hacker = new User();
        hacker.setId(9L);

        Project project = new Project();
        project.setUser(owner);

        Task task = new Task();
        task.setProject(project);

        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(hacker);

            Mockito.when(taskRepo.findById(10L)).thenReturn(Optional.of(task));

            assertThrows(AccessDeniedException.class, () -> taskManager.delete(10L));
        }
    }

    @Test
    public void findAll_WhenValid_ShouldReturnPagedResponses() {
        User user = new User();
        user.setId(1L);

        Project project = new Project();
        project.setId(2L);
        project.setUser(user);

        Task task1 = new Task();
        task1.setId(1L);

        Task task2 = new Task();
        task2.setId(2L);

        TaskResponse res1 = new TaskResponse();
        res1.setId(1L);

        TaskResponse res2 = new TaskResponse();
        res2.setId(2L);

        Pageable pageable = PageRequest.of(0, 2);
        Page<Task> page = new PageImpl<>(List.of(task1, task2), pageable, 2);

        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(user);

            Mockito.when(projectRepo.findById(2L)).thenReturn(Optional.of(project));
            Mockito.when(taskRepo.findByProjectId(2L, pageable)).thenReturn(page);

            Mockito.when(taskMapper.toResponse(task1)).thenReturn(res1);
            Mockito.when(taskMapper.toResponse(task2)).thenReturn(res2);

            Page<TaskResponse> results = taskManager.findAll(0, 2, 2L);

            assertEquals(2, results.getTotalElements());
            assertEquals(1, results.getTotalPages());
            assertEquals(1L, results.getContent().get(0).getId());
            assertEquals(2L, results.getContent().get(1).getId());
        }
    }

    @Test
    public void findAll_ProjectIdNull_ShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.findAll(0, 2, null));
    }

    @Test
    public void findAll_ProjectNotFound_ShouldThrow() {
        User user = new User();
        user.setId(1L);

        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(user);

            Mockito.when(projectRepo.findById(2L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> taskManager.findAll(0, 2, 2L));
        }
    }

    @Test
    public void findAll_AccessDenied_ShouldThrow() {
        User owner = new User();
        owner.setId(1L);

        User hacker = new User();
        hacker.setId(9L);

        Project project = new Project();
        project.setId(2L);
        project.setUser(owner);

        try (MockedStatic<SecurityUtils> mock = Mockito.mockStatic(SecurityUtils.class)) {
            mock.when(SecurityUtils::getCurrentUser).thenReturn(hacker);

            Mockito.when(projectRepo.findById(2L)).thenReturn(Optional.of(project));

            assertThrows(AccessDeniedException.class, () -> taskManager.findAll(0, 2, 2L));
        }
    }

}