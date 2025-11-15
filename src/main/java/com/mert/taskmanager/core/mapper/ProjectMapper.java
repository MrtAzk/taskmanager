package com.mert.taskmanager.core.mapper;


import com.mert.taskmanager.dto.request.project.ProjectSaveRequest;
import com.mert.taskmanager.dto.request.project.ProjectUpdateRequest;
import com.mert.taskmanager.dto.request.task.TaskUpdateRequest;
import com.mert.taskmanager.dto.response.ProjectResponse;
import com.mert.taskmanager.entity.Project;
import com.mert.taskmanager.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "taskList",ignore = true)
    Project toEntity(ProjectSaveRequest projectSaveRequest);

    ProjectResponse toResponse(Project task);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "taskList", ignore = true)
    Project toEntity(ProjectUpdateRequest projectUpdateRequest);
}
