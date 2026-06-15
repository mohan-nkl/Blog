package com.mohan.blog.restapis.dtos;

import com.mohan.blog.models.User;

public record AuthorResponse(Long id, String name) {

    public static AuthorResponse from(User user) {
        if (user == null) {
            return null;
        }
        return new AuthorResponse(user.getId(), user.getName());
    }
}