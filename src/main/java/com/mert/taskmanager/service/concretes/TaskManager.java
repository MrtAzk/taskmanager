package com.mert.taskmanager.service.concretes;

import com.mert.taskmanager.dto.request.task.TaskSaveRequest;
import com.mert.taskmanager.dto.request.task.TaskUpdateRequest;
import com.mert.taskmanager.dto.response.TaskResponse;
import com.mert.taskmanager.entity.Project;
import com.mert.taskmanager.entity.Task;
import com.mert.taskmanager.core.mapper.TaskMapper;
import com.mert.taskmanager.repository.ProjectRepo;
import com.mert.taskmanager.repository.TaskRepo;
import com.mert.taskmanager.service.abstracts.ITaskService;
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
        Project project= projectRepo.findById(taskSaveRequest.getProjectId()).orElseThrow(()->new EntityNotFoundException("Proje Bulunamadı"));
        saveTask.setProject(project);
        taskRepo.save(saveTask);
        TaskResponse taskResponse =taskMapper.toResponse(saveTask);
        return taskResponse;

    }

    @Override
    public TaskResponse get(Long id) {
        Task getTask=taskRepo.findById(id).orElseThrow(()->new EntityNotFoundException("Aranan Task bulunamadı"));
        TaskResponse taskResponse=taskMapper.toResponse(getTask);
        return  taskResponse;
    }

    @Override
    public TaskResponse update(TaskUpdateRequest taskUpdateRequest) {
        Task updatedTask=taskMapper.toEntity(taskUpdateRequest);
        Task oldTask=taskRepo.findById(taskUpdateRequest.getId()).orElseThrow(()->new EntityNotFoundException("Update edilecek olan task bulunamadı"));
        Project project=projectRepo.findById(taskUpdateRequest.getProjectId()).orElseThrow(()->new EntityNotFoundException("project bulunamadı"));
        updatedTask.setProject(project);
        taskRepo.save(updatedTask);
        TaskResponse taskResponse =taskMapper.toResponse(updatedTask);
        return  taskResponse;
    }

    @Override//Belki kullanırım
    public Page<TaskResponse> findAll(int page, int pageSize,Long projectId) {

        if (projectId == null) {
            throw new IllegalArgumentException("Project ID null olamaz backend ıd bekliyor .");
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
        Task deleteTask=taskRepo.findById(id).orElseThrow(()->new EntityNotFoundException("Silinecek Olan Id ye ait task bulunamadı"));
        taskRepo.delete(deleteTask);
        return  true;
    }
}
