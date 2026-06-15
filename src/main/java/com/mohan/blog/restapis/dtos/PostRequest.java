package com.mohan.blog.restapis.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,

        @Size(max = 500, message = "Excerpt must be at most 500 characters")
        String excerpt,

        @NotBlank(message = "Content is required")
        String content,

        Long authorId,        // used only when the caller is an admin

        String tags,          // comma-separated, same as the web form

        boolean published) {
}