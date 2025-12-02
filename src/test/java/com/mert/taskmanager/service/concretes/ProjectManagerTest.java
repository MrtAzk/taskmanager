package com.mert.taskmanager.service.concretes;

import com.mert.taskmanager.config.SecurityUtils;
import com.mert.taskmanager.core.exceptions.AccessDeniedException;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    @Test
    public void update_WhenValidRequest_ShouldReturnProjectResponse(){
        User currentUser=new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@email");


        try (MockedStatic<SecurityUtils> mockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(() -> SecurityUtils.getCurrentUser()).thenReturn(currentUser);
            LocalDateTime fixedDate = LocalDateTime.now();

            Project oldProject = new Project();
            oldProject.setId(1L);
            oldProject.setName("OldName");
            oldProject.setDescription("OldDesc");
            oldProject.setUser(currentUser);
            oldProject.setCreatedAt(LocalDate.from(fixedDate));
            oldProject.setTaskList(null);

            ProjectUpdateRequest projectUpdateRequest=new ProjectUpdateRequest();
            projectUpdateRequest.setId(1L);
            projectUpdateRequest.setName("NewName");
            projectUpdateRequest.setDescription("NewDesc");

            Project updatedEntity = new Project();
            updatedEntity.setId(1L);
            updatedEntity.setName("NewName");
            updatedEntity.setDescription("NewDesc");
            updatedEntity.setUser(currentUser);
            updatedEntity.setCreatedAt(oldProject.getCreatedAt());
            updatedEntity.setTaskList(oldProject.getTaskList());


            ProjectResponse projectResponse = new ProjectResponse();
            projectResponse.setId(1L);
            projectResponse.setName("NewName");
            projectResponse.setDescription("NewDesc");
            projectResponse.setCreatedAt(updatedEntity.getCreatedAt());
            projectResponse.setTaskListId(null);

            Mockito.when(projectMapper.toResponse(updatedEntity)).thenReturn(projectResponse);
            Mockito.when(projectMapper.toEntity(projectUpdateRequest)).thenReturn(updatedEntity);
            Mockito.when(projectRepo.findById(1L)).thenReturn(Optional.of(oldProject));
            Mockito.when(projectRepo.save(updatedEntity)).thenReturn(updatedEntity);

            ProjectResponse result = projectManager.update(projectUpdateRequest);
            assertNotNull(result);
            assertEquals(projectResponse.getId(),result.getId());
            assertEquals(projectResponse.getName(),result.getName());
            assertEquals(projectResponse.getDescription(),result.getDescription());
            assertEquals(projectResponse.getCreatedAt(),result.getCreatedAt());
            assertEquals(projectResponse.getTaskListId(),result.getTaskListId());

            Mockito.verify(projectMapper).toResponse(updatedEntity);
            Mockito.verify(projectMapper).toEntity(projectUpdateRequest);
            Mockito.verify(projectRepo).save(updatedEntity);
            Mockito.verify(projectRepo).findById(1L);


        }
    }
    @Test
    public void update_ResourceNotFoundException_ShouldThrow(){
        User currentUser=new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@email");

        ProjectUpdateRequest req = new ProjectUpdateRequest();
        req.setId(1L);
        req.setName("updateNmae");
        req.setDescription("updateDesc");


        try (MockedStatic<SecurityUtils> mockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(() -> SecurityUtils.getCurrentUser()).thenReturn(currentUser);

            Mockito.when(projectRepo.findById(1L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,()->projectManager.update(req));
            Mockito.verify(projectRepo).findById(1L);

            }



        }
    @Test
    public void update_AccessDeniedException_ShouldThrow(){
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

        ProjectUpdateRequest req = new ProjectUpdateRequest();
        req.setId(1L);

        try (MockedStatic<SecurityUtils> mockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(() -> SecurityUtils.getCurrentUser()).thenReturn(hacker);

            Mockito.when(projectRepo.findById(1L)).thenReturn(Optional.of(project));

            assertThrows(AccessDeniedException.class,()->projectManager.update(req));
            }

        }
    @Test
    public  void delete_WhenValidRequest_ShouldDeleteProject(){
        LocalDateTime fixedDate = LocalDateTime.now();
        User currentUser=new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@email");

        Project deleteProject = new Project();
        deleteProject.setId(1L);
        deleteProject.setName("deleteProject");
        deleteProject.setDescription("deleteProjectDesc");
        deleteProject.setUser(currentUser);
        deleteProject.setCreatedAt(LocalDate.from(fixedDate));
        deleteProject.setTaskList(null);

        try (MockedStatic<SecurityUtils> mockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(() -> SecurityUtils.getCurrentUser()).thenReturn(currentUser);
            Mockito.when(projectRepo.findById(1L)).thenReturn(Optional.of(deleteProject));

            boolean res =projectManager.delete(1L);
            assertTrue(res);

            Mockito.verify(projectRepo).delete(deleteProject);
            Mockito.verify(projectRepo).findById(1L);


        }
    }
    @Test
    public void delete_ResourceNotFoundException_ShouldThrow(){
        LocalDateTime fixedDate = LocalDateTime.now();
        User currentUser=new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@email");

        try (MockedStatic<SecurityUtils> mockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(() -> SecurityUtils.getCurrentUser()).thenReturn(currentUser);

            Mockito.when(projectRepo.findById(1L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,()->projectManager.delete(1L));
            Mockito.verify(projectRepo).findById(1L);
            //Çağrılmadığından emin olmak içindir
            Mockito.verify(projectRepo, Mockito.never()).delete(Mockito.any());
        }

    }
    @Test
    public void delete_AccessDeniedException_ShouldThrow(){
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
            assertThrows(AccessDeniedException.class,()-> projectManager.delete(1L));

            Mockito.verify(projectRepo).findById(1L);
            //Çağrılmadığından emin olmak içindir
            Mockito.verify(projectRepo, Mockito.never()).delete(Mockito.any());
        }

    }
    @Test
    public void findAll_WhenValidRequest_ShouldReturnPagedProjectResponse() {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@email");

        try (MockedStatic<SecurityUtils> mockedStatic = Mockito.mockStatic(SecurityUtils.class)) {
            mockedStatic.when(() -> SecurityUtils.getCurrentUser()).thenReturn(currentUser);
            LocalDateTime fixedDate = LocalDateTime.now();

            Project project1 = new Project();
            project1.setId(1L);
            project1.setName("ProjectName1");
            project1.setDescription("ProjectDescription1");
            project1.setUser(currentUser);
            project1.setCreatedAt(LocalDate.from(fixedDate));
            project1.setTaskList(null);

            Project project2 = new Project();
            project2.setId(2L);
            project2.setName("ProjectName2");
            project2.setDescription("ProjectDescription2");
            project2.setUser(currentUser);
            project2.setCreatedAt(LocalDate.from(fixedDate));
            project2.setTaskList(null);

            ProjectResponse projectResponse1 = new ProjectResponse();
            projectResponse1.setId(1L);
            projectResponse1.setName("ProjectName1");
            projectResponse1.setDescription("ProjectDescription1");
            projectResponse1.setCreatedAt(LocalDate.from(fixedDate));
            projectResponse1.setTaskListId(null);

            ProjectResponse projectResponse2 = new ProjectResponse();
            projectResponse2.setId(2L);
            projectResponse2.setName("ProjectName2");
            projectResponse2.setDescription("ProjectDescription2");
            projectResponse2.setCreatedAt(LocalDate.from(fixedDate));
            projectResponse2.setTaskListId(null);

            Pageable pageable = PageRequest.of(0, 2);
            List<Project> projectList= List.of(project1,project2);
            Page<Project>  projectPages =new PageImpl<>(projectList);


            Mockito.when(projectRepo.findByUserId(currentUser.getId(),pageable)).thenReturn(projectPages);
            Mockito.when(projectMapper.toResponse(project1)).thenReturn(projectResponse1);
            Mockito.when(projectMapper.toResponse(project2)).thenReturn(projectResponse2);//Aynı ikiside

            Page<ProjectResponse> results = projectManager.findAll(0,2);



            assertEquals(2, results.getTotalElements());
            assertEquals(1, results.getTotalPages());
            assertEquals(0,results.getNumber());
            assertNotNull(results);
            assertEquals("ProjectName1", results.getContent().get(0).getName());
            assertEquals("ProjectName2", results.getContent().get(1).getName());



            Mockito.verify(projectRepo).findByUserId(currentUser.getId(),pageable);
            Mockito.verify(projectMapper).toResponse(project1);
            Mockito.verify(projectMapper).toResponse(project2);

        }

    }

}