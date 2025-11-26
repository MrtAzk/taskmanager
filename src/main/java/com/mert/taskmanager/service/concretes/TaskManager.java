package com.mert.taskmanager.service.concretes;

import com.mert.taskmanager.core.exceptions.ResourceNotFoundException;
import com.mert.taskmanager.dto.request.task.TaskSaveRequest;
import com.mert.taskmanager.dto.request.task.TaskUpdateRequest;
import com.mert.taskmanager.dto.response.TaskResponse;
import com.mert.taskmanager.entity.Project;
import com.mert.taskmanager.entity.Task;
import com.mert.taskmanager.core.mapper.TaskMapper;
import com.mert.taskmanager.repository.ProjectRepo;
import com.mert.taskmanager.repository.TaskRepo;
import com.mert.taskmanager.service.abstracts.ITaskService;
import com.mert.taskmanager.utils.Msg;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class TaskManager implements ITaskService {

    private  final TaskRepo taskRepo ;
    private final ProjectRepo projectRepo;
    private  final TaskMapper taskMapper;

    public TaskManager(TaskRepo taskRepo, ProjectRepo projectRepo, TaskMapper taskMapper) {
        this.taskRepo = taskRepo;
        this.projectRepo = projectRepo;
        this.taskMapper = taskMapper;
    }


    @Override
    public TaskResponse save(TaskSaveRequest taskSaveRequest) {
        Task saveTask = taskMapper.toEntity(taskSaveRequest);
        Project project= projectRepo.findById(taskSaveRequest.getProjectId()).orElseThrow(()->new ResourceNotFoundException(Msg.TASK_PROJECT_NOTFOUND));
        saveTask.setProject(project);
        taskRepo.save(saveTask);
        TaskResponse taskResponse =taskMapper.toResponse(saveTask);
        return taskResponse;

    }

    @Override
    public TaskResponse get(Long id) {
        Task getTask=taskRepo.findById(id).orElseThrow(()->new ResourceNotFoundException(Msg.TASK_NOTFOUND));
        TaskResponse taskResponse=taskMapper.toResponse(getTask);
        return  taskResponse;
    }

    @Override
    public TaskResponse update(TaskUpdateRequest taskUpdateRequest) {
        Task updatedTask=taskMapper.toEntity(taskUpdateRequest);
        Task oldTask=taskRepo.findById(taskUpdateRequest.getId()).orElseThrow(()->new ResourceNotFoundException(Msg.TASK_NOTFOUND));
        Project project=projectRepo.findById(taskUpdateRequest.getProjectId()).orElseThrow(()->new ResourceNotFoundException(Msg.TASK_PROJECT_NOTFOUND));
        updatedTask.setProject(project);
        taskRepo.save(updatedTask);
        TaskResponse taskResponse =taskMapper.toResponse(updatedTask);
        return  taskResponse;
    }

    @Override//Belki kullanırım
    public Page<TaskResponse> findAll(int page, int pageSize,Long projectId) {

        if (projectId == null) {
            throw new IllegalArgumentException(Msg.VALIDATE_PROJECT_ID_NULL);
        }
        Pageable pageable = PageRequest.of(page,pageSize);
        Page<Task> taskPage = taskRepo.findByProjectId(projectId,pageable);
        List<TaskResponse> taskResponseList=new ArrayList<>();
        for (Task task :taskPage.getContent()){
                taskResponseList.add(taskMapper.toResponse(task));
        }
        return new PageImpl<>(taskResponseList,pageable,taskPage.getTotalElements());
    }

    @Override
    public boolean delete(Long id) {
        Task deleteTask=taskRepo.findById(id).orElseThrow(()->new ResourceNotFoundException(Msg.TASK_NOTFOUND));
        taskRepo.delete(deleteTask);
        return  true;
    }
}
