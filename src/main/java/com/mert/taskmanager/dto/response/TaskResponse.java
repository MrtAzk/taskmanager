package com.mert.taskmanager.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mert.taskmanager.entity.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {


    private  Long id ;


    private  String name ;


    private String description ;

    private Long projectId;

    @JsonFormat()
    private LocalDate dueDate ;

}
