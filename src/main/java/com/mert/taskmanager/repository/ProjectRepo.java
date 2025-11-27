package com.mert.taskmanager.repository;

import com.mert.taskmanager.entity.Project;
import com.mert.taskmanager.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepo extends JpaRepository<Project, Long> {
    Page<Project> findByUserId(Long currentUserId, Pageable pageable);
}
