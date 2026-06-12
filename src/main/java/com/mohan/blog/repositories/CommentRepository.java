package com.mohan.blog.repositories;

import com.mohan.blog.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // All comments for a post, newest first — for the detail page / moderation.
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    // How many comments a post has, without loading them all.
    long countByPostId(Long postId);
}