package com.mohan.blog.restapis;

import com.mohan.blog.dtos.PostForm;
import com.mohan.blog.models.Post;
import com.mohan.blog.restapis.dtos.PagedResponse;
import com.mohan.blog.restapis.dtos.PostRequest;
import com.mohan.blog.restapis.dtos.PostResponse;
import com.mohan.blog.security.CustomUserDetails;
import com.mohan.blog.services.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("publishedAt", "createdAt", "title");

    private final PostService postService;

    public PostRestController(PostService postService) {
        this.postService = postService;
    }

    // GET /api/posts — public list with paging, search, filter, sort.
    @GetMapping
    public PagedResponse<PostResponse> list(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "publishedAt") String sortField,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Sort sort = Sort.by(direction(order), safeSortField(sortField));
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts = postService.search(authorId, tagId, q, pageable);

        List<PostResponse> content = posts.getContent().stream()
                .map(PostResponse::summary)
                .toList();

        return PagedResponse.of(posts, content);
    }

    // GET /api/posts/{id} — public single post, with comments.
    @GetMapping("/{id}")
    public PostResponse get(@PathVariable Long id) {
        return PostResponse.detail(postService.getPost(id));
    }

    // POST /api/posts — create (author writes as self; admin may choose).
    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest request,
                                               @AuthenticationPrincipal CustomUserDetails principal) {

        Post saved = postService.create(toForm(request), principal.getUser());

        return ResponseEntity
                .created(URI.create("/api/posts/" + saved.getId()))
                .body(PostResponse.detail(saved));
    }

    // PUT /api/posts/{id} — update (ownership enforced in the service).
    @PutMapping("/{id}")
    public PostResponse update(@PathVariable Long id, @Valid @RequestBody PostRequest request,
                               @AuthenticationPrincipal CustomUserDetails principal) {

        Post updated = postService.update(id, toForm(request), principal.getUser());

        return PostResponse.detail(updated);
    }

    // DELETE /api/posts/{id} — delete (ownership enforced in the service).
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal CustomUserDetails principal) {

        postService.delete(id, principal.getUser());

        return ResponseEntity.noContent().build();
    }

    // ----- helpers -----

    private PostForm toForm(PostRequest request) {
        return new PostForm(request.title(), request.excerpt(), request.content(),
                request.authorId(), request.tags(), request.published());
    }

    private String safeSortField(String sortField) {
        if (ALLOWED_SORT_FIELDS.contains(sortField)) {
            return sortField;
        }
        return "publishedAt";
    }

    private Sort.Direction direction(String order) {
        if ("asc".equalsIgnoreCase(order)) {
            return Sort.Direction.ASC;
        }
        return Sort.Direction.DESC;
    }
}