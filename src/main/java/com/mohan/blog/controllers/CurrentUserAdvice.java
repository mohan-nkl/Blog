package com.mohan.blog.controllers;

import com.mohan.blog.models.User;
import com.mohan.blog.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserAdvice {

    // Exposes the logged-in user (or null) to every view, so templates can
    // show/hide nav items with plain th:if — no Thymeleaf-Security dialect needed.
    @ModelAttribute("currentUser")
    public User currentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }

        return null;
    }
}