package com.mohan.blog.restapis.dtos;

import com.mohan.blog.models.Comment;

import java.time.LocalDateTime;

public record CommentResponse(Long id, String name, String comment, LocalDateTime createdAt) {

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(), comment.getName(), comment.getComment(), comment.getCreatedAt());
    }
}