package com.mohan.blog.services;

import com.mohan.blog.dtos.CommentForm;
import com.mohan.blog.models.Comment;
import com.mohan.blog.models.Post;
import com.mohan.blog.repositories.CommentRepository;
import com.mohan.blog.repositories.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public Comment addComment(Long postId, CommentForm form) {

        Optional<Post> postOptional = postRepository.findById(postId);

        Post post;
        if (postOptional.isEmpty()) {
            throw new IllegalArgumentException("Post not found: " + postId);
        }
        post = postOptional.get();

        Comment comment = new Comment(form.getName(), form.getEmail(), form.getComment());

        post.addComment(comment);     // sets both sides; cascade persists it on commit
        return comment;               // managed; its id is populated after flush
    }

    @Transactional(readOnly = true)
    public List<Comment> forPost(Long postId) {

        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }
}