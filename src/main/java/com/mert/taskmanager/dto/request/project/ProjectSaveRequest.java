package com.mert.taskmanager.dto.request.project;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSaveRequest {

    @NotBlank
    private String name ;

    @NotBlank
    private String description ;

}
