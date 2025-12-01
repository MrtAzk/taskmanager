package com.mert.taskmanager.service.concretes;

import com.mert.taskmanager.core.mapper.TaskMapper;
import com.mert.taskmanager.dto.request.task.TaskSaveRequest;
import com.mert.taskmanager.repository.ProjectRepo;
import com.mert.taskmanager.repository.TaskRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    private  TaskManager taskManager;

    private   TaskRepo taskRepo ;
    private  ProjectRepo projectRepo;
    private   TaskMapper taskMapper;



    @BeforeEach//Test seneryasou  çalışmdan önce çalışcak olan özel method  kısım burası
    void setUp() {
        //Yalancı nesneler diyelim bunlara taskmannager cosnturactırını lazım olam parametredeki nesneleri yaratıp taklit etmek için
        taskRepo= Mockito.mock(TaskRepo.class);
        projectRepo= Mockito.mock(ProjectRepo.class);
        taskMapper= Mockito.mock(TaskMapper.class);


        taskManager=new TaskManager(taskRepo,projectRepo,taskMapper);

    }


    @Test//Project testi önce sonra bu
    public void save_WhenValidRequest_ShouldReturnTaskResponse() {
        TaskSaveRequest taskSaveRequest = new TaskSaveRequest();

    }
}