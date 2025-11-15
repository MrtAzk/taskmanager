package com.mert.taskmanager.service.abstracts;


import com.mert.taskmanager.dto.request.project.ProjectSaveRequest;
import com.mert.taskmanager.dto.request.project.ProjectUpdateRequest;
import com.mert.taskmanager.dto.response.ProjectResponse;
import com.mert.taskmanager.dto.response.TaskResponse;
import org.springframework.data.domain.Page;

public interface IProjectService {
    ProjectResponse save(ProjectSaveRequest projectSaveRequest);

    ProjectResponse get(Long id);

    ProjectResponse update(ProjectUpdateRequest projectUpdateRequest);

    Page<ProjectResponse> findAll(int page, int pageSize);

    boolean delete(Long id);
}
