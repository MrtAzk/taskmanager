package com.mert.taskmanager.service.concretes;

import com.mert.taskmanager.config.SecurityUtils;
import com.mert.taskmanager.core.exceptions.AccessDeniedException;
import com.mert.taskmanager.core.exceptions.ResourceNotFoundException;
import com.mert.taskmanager.dto.request.task.TaskSaveRequest;
import com.mert.taskmanager.dto.request.task.TaskUpdateRequest;
import com.mert.taskmanager.dto.response.TaskResponse;
import com.mert.taskmanager.entity.Project;
import com.mert.taskmanager.entity.Task;
import com.mert.taskmanager.core.mapper.TaskMapper;
import com.mert.taskmanager.entity.User;
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
        User currentUser= SecurityUtils.getCurrentUser();

        Task saveTask = taskMapper.toEntity(taskSaveRequest);
        Project project= projectRepo.findById(taskSaveRequest.getProjectId()).orElseThrow(()->new ResourceNotFoundException(Msg.TASK_PROJECT_NOTFOUND));
        if (currentUser.getId().equals(project.getUser().getId())) {
            saveTask.setProject(project);
            taskRepo.save(saveTask);
            TaskResponse taskResponse =taskMapper.toResponse(saveTask);
            return taskResponse;
        }
        else throw new AccessDeniedException("Bu projeye erişme yetkiniz bulunmamaktadır.");
    }

    @Override
    public TaskResponse get(Long id) {
        User currentUser= SecurityUtils.getCurrentUser();
        Long currentUserId=currentUser.getId();

        Task getTask=taskRepo.findById(id).orElseThrow(()->new ResourceNotFoundException(Msg.TASK_NOTFOUND));
        if (currentUserId.equals(getTask.getProject().getUser().getId())) {
            TaskResponse taskResponse=taskMapper.toResponse(getTask);
            return  taskResponse;
        }
        else throw new AccessDeniedException("Bu projeye erişme yetkiniz bulunmamaktadır.");
    }

    @Override
    public TaskResponse update(TaskUpdateRequest taskUpdateRequest) {
        User currentUser= SecurityUtils.getCurrentUser();
        Long currentUserId=currentUser.getId();

        Task updatedTask=taskMapper.toEntity(taskUpdateRequest);
        Task oldTask=taskRepo.findById(taskUpdateRequest.getId()).orElseThrow(()->new ResourceNotFoundException(Msg.TASK_NOTFOUND));
        Project project=projectRepo.findById(taskUpdateRequest.getProjectId()).orElseThrow(()->new ResourceNotFoundException(Msg.TASK_PROJECT_NOTFOUND));

        if (currentUserId.equals(project.getUser().getId())) {
            updatedTask.setProject(project);
            taskRepo.save(updatedTask);
            TaskResponse taskResponse =taskMapper.toResponse(updatedTask);
            return  taskResponse;

        }
        else throw new AccessDeniedException("Bu projeye erişme yetkiniz bulunmamaktadır.");
    }

    @Override//Belki kullanırım
    public Page<TaskResponse> findAll(int page, int pageSize,Long projectId) {

        if (projectId == null) {
            throw new IllegalArgumentException(Msg.VALIDATE_PROJECT_ID_NULL);
        }

        User currentUser= SecurityUtils.getCurrentUser();
        Long currentUserId=currentUser.getId();
        Project project=projectRepo.findById(projectId).orElseThrow(()->new ResourceNotFoundException(Msg.PROJECT_NOTFOUND));//Login olan id ile projectten user id yi almak için çağrdım

        Pageable pageable = PageRequest.of(page,pageSize);
        Page<Task> taskPage = taskRepo.findByProjectId(projectId,pageable);
        List<TaskResponse> taskResponseList=new ArrayList<>();
        if (currentUserId.equals(project.getUser().getId())) {
            for (Task task :taskPage.getContent()){
                taskResponseList.add(taskMapper.toResponse(task));
            }
            return new PageImpl<>(taskResponseList,pageable,taskPage.getTotalElements());
        }
        else throw new AccessDeniedException("Bu projeye erişme yetkiniz bulunmamaktadır.");
    }

    @Override
    public boolean delete(Long id) {
        User currentUser= SecurityUtils.getCurrentUser();
        Long currentUserId=currentUser.getId();
        Task deleteTask=taskRepo.findById(id).orElseThrow(()->new ResourceNotFoundException(Msg.TASK_NOTFOUND));
        if (currentUserId.equals(deleteTask.getProject().getUser().getId())) {
            taskRepo.delete(deleteTask);
            return  true;
        }
        else throw new AccessDeniedException("Bu projeye erişme yetkiniz bulunmamaktadır.");
    }
}
