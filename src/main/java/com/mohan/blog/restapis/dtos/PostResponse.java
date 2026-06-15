package com.mohan.blog.restapis.dtos;

import com.mohan.blog.models.Post;
import com.mohan.blog.models.Tag;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String excerpt,
        String content,
        AuthorResponse author,
        List<String> tags,
        boolean published,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CommentResponse> comments) {

    /** For list responses — no comments. */
    public static PostResponse summary(Post post) {
        return build(post, null);
    }

    /** For single-post responses — includes comments. */
    public static PostResponse detail(Post post) {
        List<CommentResponse> comments = post.getComments().stream()
                .map(CommentResponse::from)
                .toList();
        return build(post, comments);
    }

    private static PostResponse build(Post post, List<CommentResponse> comments) {

        List<String> tagNames = post.getTags().stream()
                .map(Tag::getName)
                .toList();

        return new PostResponse(
                post.getId(), post.getTitle(), post.getExcerpt(), post.getContent(),
                AuthorResponse.from(post.getAuthor()), tagNames,
                post.isPublished(), post.getPublishedAt(),
                post.getCreatedAt(), post.getUpdatedAt(), comments);
    }
}