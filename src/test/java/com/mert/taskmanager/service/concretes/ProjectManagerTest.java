package com.mert.taskmanager.service.concretes;

import com.mert.taskmanager.config.SecurityUtils;
import com.mert.taskmanager.core.exceptions.AccessDeniedException;
import com.mert.taskmanager.core.exceptions.ErrorResponse;
import com.mert.taskmanager.core.exceptions.ResourceNotFoundException;
import com.mert.taskmanager.core.mapper.ProjectMapper;
import com.mert.taskmanager.dto.request.project.ProjectSaveRequest;
import com.mert.taskmanager.dto.request.project.ProjectUpdateRequest;
import com.mert.taskmanager.dto.response.ProjectResponse;
import com.mert.taskmanager.entity.Project;
import com.mert.taskmanager.entity.User;
import com.mert.taskmanager.repository.ProjectRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProjectManagerTest {

    private ProjectManager projectManager;


    private  ProjectRepo projectRepo;
    private  ProjectMapper projectMapper;

    @BeforeEach
    void setUp() {
        projectRepo = Mockito.mock(ProjectRepo.class);
        projectMapper = Mockito.mock(ProjectMapper.class);

        projectManager = new ProjectManager(projectRepo, projectMapper);
    }

    @Test
    public void save_WhenValidRequest_ShouldReturnProjectResponse() {
        ProjectSaveRequest projectSaveRequest = new ProjectSaveRequest();

        // --- 1) Test input'unu oluştur (ProjectSaveRequest) ---
        projectSaveRequest.setName("ProjectName");
        projectSaveRequest.setDescription("ProjectDescription");

        // --- 2) SecurityUtils.getCurrentUser() mockla ---
        User currentUser=new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@email");

        //Statik metotlar normal Mockito ile mocklanmaz, Mockito.mockStatic kullanman gerekiyor.
        try (MockedStatic<SecurityUtils> mockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(()->SecurityUtils.getCurrentUser()).thenReturn(currentUser);

            // --- 3) Mapper'ın döndüreceği entity (toEntity) ---
            Project mappedProject=new Project();
            mappedProject.setName("ProjectName");
            mappedProject.setDescription("ProjectDescription");
            mappedProject.setUser(currentUser);
            mappedProject.setCreatedAt(LocalDate.from(LocalDateTime.now()));
            mappedProject.setTaskList(null);
            Mockito.when(projectMapper.toEntity(projectSaveRequest)).thenReturn(mappedProject);

            // --- 4) Mapper'ın response dönmesi (toResponse) ---
            ProjectResponse projectResponse=new ProjectResponse();
            projectResponse.setId(1L);
            projectResponse.setName("ProjectName");
            projectResponse.setDescription("ProjectDescription");
            projectResponse.setCreatedAt(LocalDate.from(LocalDateTime.now()));
            projectResponse.setTaskListId(null);
            Mockito.when(projectMapper.toResponse(mappedProject)).thenReturn(projectResponse);

            // --- 5) Service metodunu çağır ---
            Mockito.when(projectRepo.save(mappedProject)).thenReturn(mappedProject);
            ProjectResponse result = projectManager.save(projectSaveRequest);



            // --- 6) Doğrulamalar ---
            assertNotNull(result);
            assertEquals(projectResponse.getId(),result.getId());
            assertEquals(projectResponse.getName(),result.getName());
            assertEquals(projectResponse.getDescription(),result.getDescription());
            assertEquals(projectResponse.getCreatedAt(),result.getCreatedAt());
            assertEquals(projectResponse.getTaskListId(),result.getTaskListId());
            // Mapper ve repo çağrıları doğrulama
            Mockito.verify(projectMapper).toEntity(projectSaveRequest);
            Mockito.verify(projectRepo).save(mappedProject);
            Mockito.verify(projectMapper).toResponse(mappedProject);

        }

    }
    @Test
    public void get_WhenValidRequest_ShouldReturnProjectResponse() {
        User currentUser=new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@email");


        try (MockedStatic<SecurityUtils> mockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(() -> SecurityUtils.getCurrentUser()).thenReturn(currentUser);

            Project mappedProject=new Project();
            mappedProject.setId(1L);
            mappedProject.setName("ProjectName1");
            mappedProject.setDescription("ProjectDescription1");
            mappedProject.setUser(currentUser);
            mappedProject.setCreatedAt(LocalDate.from(LocalDateTime.now()));
            mappedProject.setTaskList(null);

            ProjectResponse projectResponse=new ProjectResponse();
            projectResponse.setId(1L);
            projectResponse.setName("ProjectName1");
            projectResponse.setDescription("ProjectDescription1");
            projectResponse.setCreatedAt(LocalDate.from(LocalDateTime.now()));
            projectResponse.setTaskListId(null);

            Mockito.when(projectMapper.toResponse(mappedProject)).thenReturn(projectResponse);
            Mockito.when(projectRepo.findById(1L)).thenReturn(Optional.of(mappedProject));

            ProjectResponse result = projectManager.get(1L);
            assertNotNull(result);
            assertEquals(projectResponse.getId(),result.getId());
            assertEquals(projectResponse.getName(),result.getName());
            assertEquals(projectResponse.getDescription(),result.getDescription());
            assertEquals(projectResponse.getCreatedAt(),result.getCreatedAt());
            assertEquals(projectResponse.getTaskListId(),result.getTaskListId());
            Mockito.verify(projectMapper).toResponse(mappedProject);
            Mockito.verify(projectRepo).findById(1L);
        }

    }
    @Test
    public void get_ResourceNotFoundException_ShouldThrow() {
        User currentUser=new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@email");



        try (MockedStatic<SecurityUtils> mockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(() -> SecurityUtils.getCurrentUser()).thenReturn(currentUser);

            Mockito.when(projectRepo.findById(1L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,()->projectManager.get(1L));

        }

    }
    @Test
    public void get_AccessDeniedException_ShouldThrow() {
        User owner=new User();
        owner.setId(1L);
        owner.setEmail("test@email");

        User hacker=new User();
        hacker.setId(2L);

        Project project=new Project();
        project.setId(1L);
        project.setName("ProjectName1");
        project.setDescription("ProjectDescription1");
        project.setUser(owner);
        project.setCreatedAt(LocalDate.from(LocalDateTime.now()));
        project.setTaskList(null);

        try (MockedStatic<SecurityUtils> mockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(() -> SecurityUtils.getCurrentUser()).thenReturn(hacker);

            Mockito.when(projectRepo.findById(1L)).thenReturn(Optional.of(project));

            assertThrows(AccessDeniedException.class,()->projectManager.get(1L));
        }
    }

}