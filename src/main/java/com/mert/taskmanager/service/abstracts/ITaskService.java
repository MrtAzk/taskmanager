package com.mert.taskmanager.service.abstracts;

import com.mert.taskmanager.dto.request.task.TaskSaveRequest;
import com.mert.taskmanager.dto.request.task.TaskUpdateRequest;
import com.mert.taskmanager.dto.response.TaskResponse;
import org.springframework.data.domain.Page;

public interface ITaskService {
    TaskResponse save(TaskSaveRequest taskSaveRequest);

    TaskResponse get(Long id);

    TaskResponse update(TaskUpdateRequest taskUpdateRequest);

    Page<TaskResponse> findAll(int page, int pageSize,Long projectId);

    boolean delete(Long id);
}
