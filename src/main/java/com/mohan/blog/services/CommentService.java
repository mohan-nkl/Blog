package com.mohan.blog.services;

import com.mohan.blog.dtos.CommentForm;
import com.mohan.blog.models.Comment;
import com.mohan.blog.models.Post;
import com.mohan.blog.models.Role;
import com.mohan.blog.models.User;
import com.mohan.blog.repositories.CommentRepository;
import com.mohan.blog.repositories.PostRepository;
import org.springframework.security.access.AccessDeniedException;
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

        Post post = loadPost(postId);

        Comment comment = new Comment(form.getName(), form.getEmail(), form.getComment());
        post.addComment(comment);       // sets both sides; cascade persists on commit

        return comment;
    }

    @Transactional(readOnly = true)
    public List<Comment> forPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    @Transactional(readOnly = true)
    public Comment getComment(Long commentId) {

        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty()) {
            throw new IllegalArgumentException("Comment not found: " + commentId);
        }
        return commentOptional.get();
    }

    @Transactional
    public Comment updateComment(Long commentId, CommentForm form, User currentUser) {

        Comment comment = getComment(commentId);
        ensureCanModerate(comment, currentUser);

        comment.setName(form.getName());
        comment.setEmail(form.getEmail());
        comment.setComment(form.getComment());

        return comment;
    }

    @Transactional
    public Long deleteComment(Long commentId, User currentUser) {

        Comment comment = getComment(commentId);
        ensureCanModerate(comment, currentUser);

        Long postId = comment.getPost().getId();
        commentRepository.delete(comment);

        return postId;
    }

    // ----- helpers -----

    private Post loadPost(Long postId) {

        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            throw new IllegalArgumentException("Post not found: " + postId);
        }
        return postOptional.get();
    }

    private void ensureCanModerate(Comment comment, User currentUser) {

        if (isAdmin(currentUser)) {
            return;
        }
        if (isPostOwner(comment, currentUser)) {
            return;
        }
        throw new AccessDeniedException("You may only moderate comments on your own posts.");
    }

    private boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    private boolean isPostOwner(Comment comment, User currentUser) {

        Post post = comment.getPost();
        return post.getAuthor() != null
                && post.getAuthor().getId().equals(currentUser.getId());
    }
}