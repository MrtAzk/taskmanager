package com.mert.taskmanager.config;

import com.mert.taskmanager.core.exceptions.ResourceNotFoundException;
import com.mert.taskmanager.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static User getCurrentUser() {

        // 1. Authentication objesini çek
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Eğer kullanıcı login olmamışsa, null dönebiliriz.
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Kullanıcı oturumu bulunamadı.");
        }

        User user = (User) authentication.getPrincipal();
        // 3. Principal (User Entity) objesini dönüştür.
        return user;
    }
}
