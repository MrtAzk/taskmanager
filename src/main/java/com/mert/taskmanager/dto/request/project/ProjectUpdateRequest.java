package com.mert.taskmanager.dto.request.project;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateRequest {


    @Positive
    private  Long id ;

    @NotBlank
    private String name ;

    @NotBlank
    private String description ;
}
