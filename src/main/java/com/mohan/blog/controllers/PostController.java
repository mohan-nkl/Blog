package com.mohan.blog.controllers;

import com.mohan.blog.dtos.CommentForm;
import com.mohan.blog.dtos.PostForm;
import com.mohan.blog.models.Post;
import com.mohan.blog.models.Role;
import com.mohan.blog.models.Tag;
import com.mohan.blog.models.User;
import com.mohan.blog.security.CustomUserDetails;
import com.mohan.blog.services.CommentService;
import com.mohan.blog.services.PostService;
import com.mohan.blog.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    public PostController(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }

    // View a post — public.
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {

        Post post = postService.getPost(id);

        model.addAttribute("post", post);
        model.addAttribute("commentForm", new CommentForm(null, null, null));

        return "post-detail";
    }

    // Show the create form.
    @GetMapping("/new")
    public String showCreateForm(@AuthenticationPrincipal CustomUserDetails principal, Model model) {

        User currentUser = principal.getUser();

        PostForm postForm = new PostForm(null, null, null, defaultAuthorId(currentUser), null, false);

        model.addAttribute("postForm", postForm);
        addAuthorChoices(model, currentUser);

        return "post-form";
    }

    // Create a post.
    @PostMapping
    public String createPost(@Valid @ModelAttribute("postForm") PostForm postForm, BindingResult result,
                             @AuthenticationPrincipal CustomUserDetails principal, Model model,
                             RedirectAttributes redirectAttributes) {

        User currentUser = principal.getUser();

        if (result.hasErrors()) {
            addAuthorChoices(model, currentUser);
            return "post-form";
        }

        Post savedPost = postService.create(postForm, currentUser);

        redirectAttributes.addFlashAttribute("message", "Post created.");

        return "redirect:/posts/" + savedPost.getId();
    }

    // Show the edit form.
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal,
                               Model model) {

        User currentUser = principal.getUser();
        Post post = postService.getPost(id);

        PostForm postForm = new PostForm(post.getTitle(), post.getExcerpt(), post.getContent(),
                authorIdOf(post), tagCsvOf(post), post.isPublished());

        model.addAttribute("postForm", postForm);
        model.addAttribute("postId", id);
        addAuthorChoices(model, currentUser);

        return "post-form";
    }

    // Update a post.
    @PostMapping("/{id}")
    public String updatePost(@PathVariable Long id, @Valid @ModelAttribute("postForm") PostForm postForm,
                             BindingResult result, @AuthenticationPrincipal CustomUserDetails principal,
                             Model model, RedirectAttributes redirectAttributes) {

        User currentUser = principal.getUser();

        if (result.hasErrors()) {
            model.addAttribute("postId", id);
            addAuthorChoices(model, currentUser);
            return "post-form";
        }

        postService.update(id, postForm, currentUser);

        redirectAttributes.addFlashAttribute("message", "Post updated.");

        return "redirect:/posts/" + id;
    }

    // Delete a post.
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal,
                             RedirectAttributes redirectAttributes) {

        postService.delete(id, principal.getUser());

        redirectAttributes.addFlashAttribute("message", "Post deleted.");

        return "redirect:/";
    }

    // Add a comment — public.
    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id, @Valid @ModelAttribute("commentForm") CommentForm commentForm,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("post", postService.getPost(id));
            return "post-detail";
        }

        commentService.addComment(id, commentForm);

        redirectAttributes.addFlashAttribute("message", "Comment added.");

        return "redirect:/posts/" + id;
    }

    // ----- form helpers -----

    /** Authors are pre-filled as themselves; an admin chooses, so leave it blank. */
    private Long defaultAuthorId(User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            return null;
        }
        return currentUser.getId();
    }

    /** Data the post form needs to render the author field correctly for this user's role. */
    private void addAuthorChoices(Model model, User currentUser) {
        boolean admin = currentUser.getRole() == Role.ADMIN;
        model.addAttribute("isAdmin", admin);
        model.addAttribute("currentUserName", currentUser.getName());
        if (admin) {
            model.addAttribute("authors", userService.findAll());
        }
    }

    private Long authorIdOf(Post post) {
        if (post.getAuthor() == null) {
            return null;
        }
        return post.getAuthor().getId();
    }

    private String tagCsvOf(Post post) {
        StringBuilder builder = new StringBuilder();
        for (Tag tag : post.getTags()) {
            if (!builder.isEmpty()) {
                builder.append(", ");
            }
            builder.append(tag.getName());
        }
        return builder.toString();
    }
}