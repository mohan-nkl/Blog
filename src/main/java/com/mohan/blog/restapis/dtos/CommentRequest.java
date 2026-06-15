package com.mohan.blog.restapis.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CommentRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Please enter a valid email")
        String email,

        @NotBlank(message = "Comment cannot be empty")
        String comment) {
}