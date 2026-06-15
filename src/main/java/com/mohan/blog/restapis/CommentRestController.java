package com.mohan.blog.restapis;

import com.mohan.blog.dtos.CommentForm;
import com.mohan.blog.models.Comment;
import com.mohan.blog.restapis.dtos.CommentRequest;
import com.mohan.blog.restapis.dtos.CommentResponse;
import com.mohan.blog.security.CustomUserDetails;
import com.mohan.blog.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentRestController {

    private final CommentService commentService;

    public CommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    // POST /api/posts/{postId}/comments — public.
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> add(@PathVariable Long postId,
                                               @Valid @RequestBody CommentRequest request) {

        Comment comment = commentService.addComment(postId, toForm(request));

        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.from(comment));
    }

    // PUT /api/comments/{id} — moderation (post owner or admin).
    @PutMapping("/comments/{id}")
    public CommentResponse update(@PathVariable Long id, @Valid @RequestBody CommentRequest request,
                                  @AuthenticationPrincipal CustomUserDetails principal) {

        Comment updated = commentService.updateComment(id, toForm(request), principal.getUser());

        return CommentResponse.from(updated);
    }

    // DELETE /api/comments/{id} — moderation (post owner or admin).
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal CustomUserDetails principal) {

        commentService.deleteComment(id, principal.getUser());

        return ResponseEntity.noContent().build();
    }

    private CommentForm toForm(CommentRequest request) {
        return new CommentForm(request.name(), request.email(), request.comment());
    }
}