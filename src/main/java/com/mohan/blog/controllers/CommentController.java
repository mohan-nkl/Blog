package com.mohan.blog.controllers;

import com.mohan.blog.dtos.CommentForm;
import com.mohan.blog.models.Comment;
import com.mohan.blog.security.CustomUserDetails;
import com.mohan.blog.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Show the edit form for a comment.
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {

        Comment comment = commentService.getComment(id);

        CommentForm commentForm = new CommentForm(comment.getName(), comment.getEmail(), comment.getComment());

        model.addAttribute("commentForm", commentForm);
        model.addAttribute("commentId", id);
        model.addAttribute("postId", comment.getPost().getId());

        return "comment-form";
    }

    // Update a comment.
    @PostMapping("/{id}")
    public String updateComment(@PathVariable Long id, @Valid @ModelAttribute("commentForm") CommentForm commentForm,
                                BindingResult result, @AuthenticationPrincipal CustomUserDetails principal,
                                Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            Comment comment = commentService.getComment(id);
            model.addAttribute("commentId", id);
            model.addAttribute("postId", comment.getPost().getId());
            return "comment-form";
        }

        Comment updated = commentService.updateComment(id, commentForm, principal.getUser());

        redirectAttributes.addFlashAttribute("message", "Comment updated.");

        return "redirect:/posts/" + updated.getPost().getId();
    }

    // Delete a comment.
    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal,
                                RedirectAttributes redirectAttributes) {

        Long postId = commentService.deleteComment(id, principal.getUser());

        redirectAttributes.addFlashAttribute("message", "Comment deleted.");

        return "redirect:/posts/" + postId;
    }
}