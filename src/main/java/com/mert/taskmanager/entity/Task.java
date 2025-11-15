package com.mert.taskmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private  Long id ;

    @Column(name = "task_title")
    private  String name ;

    @Column(name = "task_description")
    private String description ;

    @Column(name = "task_dueDate")
    private LocalDate dueDate ;

    @ManyToOne
    @JoinColumn(name = "project_id")
    Project project ;

}
