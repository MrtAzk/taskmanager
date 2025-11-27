package com.mert.taskmanager.service.concretes;

import com.mert.taskmanager.config.SecurityUtils;
import com.mert.taskmanager.core.exceptions.AccessDeniedException;
import com.mert.taskmanager.core.exceptions.ResourceNotFoundException;
import com.mert.taskmanager.dto.request.project.ProjectSaveRequest;
import com.mert.taskmanager.dto.request.project.ProjectUpdateRequest;
import com.mert.taskmanager.dto.response.ProjectResponse;
import com.mert.taskmanager.entity.Project;
import com.mert.taskmanager.core.mapper.ProjectMapper;
import com.mert.taskmanager.entity.User;
import com.mert.taskmanager.repository.ProjectRepo;
import com.mert.taskmanager.service.abstracts.IProjectService;
import com.mert.taskmanager.utils.Msg;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectManager implements IProjectService {
    private final ProjectRepo projectRepo;
    private final ProjectMapper projectMapper;

    public ProjectManager(ProjectRepo projectRepo, ProjectMapper projectMapper) {
        this.projectRepo = projectRepo;
        this.projectMapper = projectMapper;
    }


    @Override
    public ProjectResponse save(ProjectSaveRequest projectSaveRequest) {

        User currentUser= SecurityUtils.getCurrentUser();


        Project saveProject=projectMapper.toEntity(projectSaveRequest);
        saveProject.setCreatedAt(LocalDate.from(LocalDateTime.now()));
        saveProject.setUser(currentUser);
        projectRepo.save(saveProject);
        ProjectResponse projectResponse = projectMapper.toResponse(saveProject);
        return projectResponse;
    }

    @Override
    public ProjectResponse get(Long id) {
        User currentUser= SecurityUtils.getCurrentUser();
        Long currentUserId=currentUser.getId();
        Project getProject=projectRepo.findById(id).orElseThrow(()->new ResourceNotFoundException(Msg.PROJECT_NOTFOUND));

        if (currentUserId.equals(getProject.getUser().getId())) {
            ProjectResponse projectResponse=projectMapper.toResponse(getProject);
            return  projectResponse;
        }

        else throw new AccessDeniedException("Bu projeye erişme yetkiniz bulunmamaktadır.");
    }

    @Override
    public ProjectResponse update(ProjectUpdateRequest projectUpdateRequest) {
        User currentUser= SecurityUtils.getCurrentUser();
        Long currentUserId=currentUser.getId();
        Project oldProject=projectRepo.findById(projectUpdateRequest.getId()).orElseThrow(()->new ResourceNotFoundException(Msg.PROJECT_NOTFOUND));

        if (currentUserId.equals(oldProject.getUser().getId())) {
            Project updatedProject= projectMapper.toEntity(projectUpdateRequest);
            updatedProject.setCreatedAt(oldProject.getCreatedAt());
            updatedProject.setTaskList(oldProject.getTaskList());
            updatedProject.setUser(oldProject.getUser());
            projectRepo.save(updatedProject);
            ProjectResponse projectResponse=projectMapper.toResponse(updatedProject);
            return  projectResponse;
        }
        else  throw new AccessDeniedException("Bu projeye erişme yetkiniz bulunmamaktadır.");
    }

    @Override
    public Page<ProjectResponse> findAll(int page, int pageSize) {

        User currentUser= SecurityUtils.getCurrentUser();
        Long currentUserId=currentUser.getId();


        Pageable pageable = PageRequest.of(page,pageSize);
        Page<Project> taskPage = projectRepo.findByUserId(currentUserId,pageable);
        List<ProjectResponse> projectResponseList=new ArrayList<>();

        for (Project project :taskPage.getContent()){
            projectResponseList.add(projectMapper.toResponse(project));
        }

        return new PageImpl<>(projectResponseList);
    }

    @Override
    public boolean delete(Long id) {

        User currentUser= SecurityUtils.getCurrentUser();
        Long currentUserId=currentUser.getId();
        Project project=projectRepo.findById(id).orElseThrow(()->new ResourceNotFoundException(Msg.PROJECT_NOTFOUND));
        if (currentUserId.equals(project.getUser().getId())) {
            projectRepo.delete(project);
            return true;
        }
        else  throw new AccessDeniedException("Bu projeye erişme yetkiniz bulunmamaktadır.");
    }
}
