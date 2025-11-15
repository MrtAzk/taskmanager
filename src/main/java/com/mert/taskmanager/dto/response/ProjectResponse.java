package com.mert.taskmanager.dto.response;

import com.mert.taskmanager.entity.Task;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ProjectResponse {


    private  Long id ;
    
    private String name ;

    private String description ;

    private LocalDate createdAt;

    private List<Task> taskListId;
}
