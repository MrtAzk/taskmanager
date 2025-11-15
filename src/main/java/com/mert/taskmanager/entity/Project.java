package com.mert.taskmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private  Long id ;

    @Column(name = "project_name")
    private String name ;

    @Column(name = "project_description")
    private String description ;

    @Column(name = "project_createdDate")
    private LocalDate createdAt;

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL)
    private List<Task> taskList;

}
