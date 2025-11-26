package com.mert.taskmanager.service.concretes;

import com.mert.taskmanager.core.exceptions.ResourceNotFoundException;
import com.mert.taskmanager.dto.request.project.ProjectSaveRequest;
import com.mert.taskmanager.dto.request.project.ProjectUpdateRequest;
import com.mert.taskmanager.dto.response.ProjectResponse;
import com.mert.taskmanager.entity.Project;
import com.mert.taskmanager.core.mapper.ProjectMapper;
import com.mert.taskmanager.repository.ProjectRepo;
import com.mert.taskmanager.service.abstracts.IProjectService;
import com.mert.taskmanager.utils.Msg;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        Project saveProject=projectMapper.toEntity(projectSaveRequest);
        saveProject.setCreatedAt(LocalDate.from(LocalDateTime.now()));
        projectRepo.save(saveProject);
        ProjectResponse projectResponse = projectMapper.toResponse(saveProject);
        return projectResponse;
    }

    @Override
    public ProjectResponse get(Long id) {
        Project getProject=projectRepo.findById(id).orElseThrow(()->new ResourceNotFoundException(Msg.PROJECT_NOTFOUND));
        ProjectResponse projectResponse=projectMapper.toResponse(getProject);
        return  projectResponse;
    }

    @Override
    public ProjectResponse update(ProjectUpdateRequest projectUpdateRequest) {
        Project oldProject=projectRepo.findById(projectUpdateRequest.getId()).orElseThrow(()->new ResourceNotFoundException(Msg.PROJECT_NOTFOUND));
        Project updatedProject= projectMapper.toEntity(projectUpdateRequest);
        updatedProject.setCreatedAt(oldProject.getCreatedAt());
        updatedProject.setTaskList(oldProject.getTaskList());
        projectRepo.save(updatedProject);
        ProjectResponse projectResponse=projectMapper.toResponse(updatedProject);
        return  projectResponse;
    }

    @Override
    public Page<ProjectResponse> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page,pageSize);
        Page<Project> taskPage = projectRepo.findAll(pageable);
        List<ProjectResponse> projectResponseList=new ArrayList<>();

        for (Project project :taskPage.getContent()){
            projectResponseList.add(projectMapper.toResponse(project));
        }

        return new PageImpl<>(projectResponseList);
    }

    @Override
    public boolean delete(Long id) {
        Project project=projectRepo.findById(id).orElseThrow(()->new ResourceNotFoundException(Msg.PROJECT_NOTFOUND));
        projectRepo.delete(project);
        return true;
    }
}
