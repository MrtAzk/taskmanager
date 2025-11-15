package com.mert.taskmanager.core.mapper;

import com.mert.taskmanager.dto.request.task.TaskSaveRequest;
import com.mert.taskmanager.dto.request.task.TaskUpdateRequest;
import com.mert.taskmanager.dto.response.TaskResponse;
import com.mert.taskmanager.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id",ignore = true)

    @Mapping(target = "project",ignore = true)
    Task toEntity(TaskSaveRequest taskSaveRequest);

    @Mapping(target = "projectId",source = "project.id")
    TaskResponse toResponse(Task task);


    @Mapping(target = "project", ignore = true)
    Task toEntity(TaskUpdateRequest taskUpdateRequest);
}
