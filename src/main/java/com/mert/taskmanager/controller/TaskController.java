package com.mert.taskmanager.controller;

import com.mert.taskmanager.dto.request.task.TaskSaveRequest;
import com.mert.taskmanager.dto.request.task.TaskUpdateRequest;
import com.mert.taskmanager.dto.response.TaskResponse;
import com.mert.taskmanager.service.abstracts.ITaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tasks")
public class TaskController {

    private final ITaskService taskService;

    public TaskController(ITaskService taskService) {
        this.taskService = taskService;
    }


    @PostMapping
    public ResponseEntity<TaskResponse> save (@Valid @RequestBody TaskSaveRequest taskSaveRequest){

       TaskResponse savedTask= this.taskService.save(taskSaveRequest);

       return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);

    }

    @PutMapping
    public  ResponseEntity<TaskResponse>update (@Valid @RequestBody TaskUpdateRequest taskUpdateRequest){

        TaskResponse updatedTask=  this.taskService.update(taskUpdateRequest);

        return  ResponseEntity.ok(updatedTask);

    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAll(@RequestParam(name = "page",required = false,defaultValue = "0") int page,
                                        @RequestParam(name = "pageSize",required = false,defaultValue = "10")int pageSize, @RequestParam(name = "projectId",required = true)Long projectId )
    {

        Page<TaskResponse> taskPage =this.taskService.findAll(page,pageSize,projectId);

        return ResponseEntity.ok(taskPage);

    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> get (@PathVariable("id") Long id){

        TaskResponse task= this.taskService.get(id);
        return ResponseEntity.ok(task);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String>  delete (@PathVariable("id") Long id){

        boolean isDeleted=  this.taskService.delete(id);
        String successMessage = id + ".ID'ye sahip task başarıyla silinmiştir.";
        return ResponseEntity.ok(successMessage);

    }

}
