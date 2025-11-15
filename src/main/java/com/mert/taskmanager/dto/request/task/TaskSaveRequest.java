package com.mert.taskmanager.dto.request.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskSaveRequest {


    @NotBlank
    private String name ;

    @NotBlank
    private String description ;

    @Positive
    private Long projectId;
    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy") //  Gün-Ay-Yıl Formatı
    private LocalDate dueDate;
}
