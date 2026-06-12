package com.mohan.blog.controllers;

import com.mohan.blog.dtos.CommentForm;
import com.mohan.blog.dtos.PostForm;
import com.mohan.blog.models.Post;
import com.mohan.blog.models.Tag;
import com.mohan.blog.services.CommentService;
import com.mohan.blog.services.PostService;
import com.mohan.blog.services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

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

    // To view a post
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {

        Post post = postService.getPost(id);

        CommentForm commentForm = new CommentForm(null, null, null);

        model.addAttribute("post", post);
        model.addAttribute("commentForm", commentForm);

        return "post-detail";
    }

    // To show create form
    @GetMapping("/new")
    public String showCreateForm(Model model) {

        PostForm postForm = new PostForm(null, null, null, null, null, false);

        model.addAttribute("postForm", postForm);
        model.addAttribute("authors", userService.findAll());

        return "post-form";
    }

    // To create post
    @PostMapping
    public String createPost(@Valid @ModelAttribute("postForm") PostForm postForm, BindingResult result, Model model,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("authors", userService.findAll());
            return "post-form";
        }

        Post savedPost = postService.create(postForm);

        redirectAttributes.addFlashAttribute("message", "Post created.");

        return "redirect:/posts/" + savedPost.getId();
    }

    // To show edit form
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {

        Post post = postService.getPost(id);

        StringBuilder tagCsvBuilder = new StringBuilder();
        for (Tag tag : post.getTags()) {
            if (!tagCsvBuilder.isEmpty()) {
                tagCsvBuilder.append(", ");
            }
            tagCsvBuilder.append(tag.getName());
        }
        String tagCsv = tagCsvBuilder.toString();

        Long authorId = null;
        if (post.getAuthor() != null) {
            authorId = post.getAuthor().getId();
        }

        PostForm postForm = new PostForm(post.getTitle(), post.getExcerpt(), post.getContent(), authorId, tagCsv,
                                        post.isPublished());

        model.addAttribute("postForm", postForm);
        model.addAttribute("postId", id);
        model.addAttribute("authors", userService.findAll());

        return "post-form";
    }

    // To update post
    @PostMapping("/{id}")
    public String updatePost(@PathVariable Long id, @Valid @ModelAttribute("postForm") PostForm postForm,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("authors", userService.findAll());
            model.addAttribute("postId", id);
            return "post-form";
        }

        postService.update(id, postForm);

        redirectAttributes.addFlashAttribute("message", "Post updated.");

        return "redirect:/posts/" + id;
    }

    // To delete a post
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        postService.delete(id);

        redirectAttributes.addFlashAttribute("message", "Post deleted.");

        return "redirect:/";
    }

    // To add a comment
    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id, @Valid @ModelAttribute("commentForm") CommentForm commentForm,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            Post post = postService.getPost(id);
            model.addAttribute("post", post);
            return "post-detail";
        }

        commentService.addComment(id, commentForm);

        redirectAttributes.addFlashAttribute("message", "Comment added.");

        return "redirect:/posts/" + id;
    }
}