package com.mohan.blog.controllers;

import com.mohan.blog.models.Post;
import com.mohan.blog.services.PostService;
import com.mohan.blog.services.TagService;
import com.mohan.blog.services.UserService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
public class HomeController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("publishedAt", "createdAt", "title");

    private final PostService postService;
    private final UserService userService;
    private final TagService tagService;

    public HomeController(PostService postService, UserService userService, TagService tagService) {
        this.postService = postService;
        this.userService = userService;
        this.tagService = tagService;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "publishedAt") String sortField,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        String safeSortField = determineSortField(sortField);
        Sort.Direction sortDirection = determineSortDirection(order);

        Sort sort = Sort.by(sortDirection, safeSortField);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts = postService.search(authorId, tagId, q, pageable);

        addDataToModel(model, posts, authorId, tagId, q, safeSortField, order);

        return "index";
    }

    private String determineSortField(String sortField) {

        if (ALLOWED_SORT_FIELDS.contains(sortField)) {
            return sortField;
        }
        return "publishedAt";
    }

    private Sort.Direction determineSortDirection(String order) {

        if ("asc".equalsIgnoreCase(order)) {
            return Sort.Direction.ASC;
        }
        return Sort.Direction.DESC;
    }

    private void addDataToModel(Model model, Page<Post> posts, Long authorId, Long tagId, String q, String sortField,
                                String order) {

        model.addAttribute("posts", posts);
        model.addAttribute("authors", userService.findAll());
        model.addAttribute("tags", tagService.findAll());
        model.addAttribute("authorId", authorId);
        model.addAttribute("tagId", tagId);
        model.addAttribute("q", q);
        model.addAttribute("sortField", sortField);
        model.addAttribute("order", order);
    }
}