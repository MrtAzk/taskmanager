package com.mert.taskmanager.controller;

import com.mert.taskmanager.dto.request.project.ProjectSaveRequest;
import com.mert.taskmanager.dto.request.project.ProjectUpdateRequest;
import com.mert.taskmanager.dto.response.ProjectResponse;
import com.mert.taskmanager.service.abstracts.IProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/projects")
public class ProjectController {

    private  final IProjectService projectService;

    public ProjectController(IProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> save (@Valid @RequestBody ProjectSaveRequest projectSaveRequest){

        ProjectResponse savedProject = this.projectService.save(projectSaveRequest);

        // 201 Created durum koduyla yanıt dön
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedProject);

    }

    @PutMapping
    public ResponseEntity<ProjectResponse> update (@Valid @RequestBody ProjectUpdateRequest projectUpdateRequest){

        ProjectResponse updatedProject = this.projectService.update(projectUpdateRequest);

        return  ResponseEntity.ok(updatedProject);

    }

    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> getAll(@RequestParam(name = "page",required = false,defaultValue = "0") int page,
                                       @RequestParam(name = "pageSize",required = false,defaultValue = "10")int pageSize){

        Page<ProjectResponse> projectPage =this.projectService.findAll(page,pageSize);
        return ResponseEntity.ok(projectPage);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> get (@PathVariable("id") Long id){

        ProjectResponse project = this.projectService.get(id);
        return ResponseEntity.ok(project);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete (@PathVariable("id") Long id){

        boolean isDeleted = this.projectService.delete(id);

        String successMessage = id + ".ID'ye sahip proje başarıyla silinmiştir.";

        // 200 OK durum kodu ve başarılı mesajı ile yanıt dön
        return ResponseEntity.ok(successMessage);

    }




}
