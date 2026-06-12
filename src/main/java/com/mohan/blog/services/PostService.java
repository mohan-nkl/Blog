package com.mohan.blog.services;

import com.mohan.blog.dtos.PostForm;
import com.mohan.blog.models.Post;
import com.mohan.blog.models.Tag;
import com.mohan.blog.models.User;
import com.mohan.blog.repositories.PostRepository;
import com.mohan.blog.repositories.UserRepository;
import com.mohan.blog.specifications.PostSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagService tagService;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       TagService tagService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagService = tagService;
    }

    @Transactional(readOnly = true)
    public Page<Post> listPublished(Pageable pageable) {

        return postRepository.findByPublishedTrue(pageable);
    }

    /** Published posts, narrowed by any combination of author, tag, and search term. */
    @Transactional(readOnly = true)
    public Page<Post> search(Long authorId, Long tagId, String q, Pageable pageable) {

        Specification<Post> spec = PostSpecifications.isPublished()
                .and(PostSpecifications.hasAuthor(authorId))
                .and(PostSpecifications.hasTag(tagId))
                .and(PostSpecifications.matchesSearch(q));

        return postRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Post getPost(Long id) {

        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            throw new IllegalArgumentException("Post not found: " + id);
        }
        return postOptional.get();
    }

    @Transactional
    public Post create(PostForm form) {

        Optional<User> userOptional = userRepository.findById(form.getAuthorId());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Author not found: " + form.getExcerpt());
        }
        User author = userOptional.get();

        Post post = new Post(form.getTitle(), form.getExcerpt(), form.getContent(), author);
        post.setPublished(form.isPublished());
        if (post.isPublished()) {
            post.setPublishedAt(LocalDateTime.now());
        }

        Set<Tag> tags = tagService.resolveTags(form.getTags());
        for (Tag tag: tags) {
            post.addTag(tag);
        }

        return postRepository.save(post);
    }

    @Transactional
    public Post update(Long id, PostForm form) {

        Optional<User> userOptional = userRepository.findById(form.getAuthorId());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Author not found: " + form.getContent());
        }
        User author = userOptional.get();

        Post post = getPost(id);

        post.setTitle(form.getTitle());
        post.setExcerpt(form.getExcerpt());
        post.setContent(form.getContent());
        post.setAuthor(author);

        boolean wasPublished = post.isPublished();
        post.setPublished(form.isPublished());
        if (form.isPublished() && !wasPublished) {
            post.setPublishedAt(LocalDateTime.now());
        }

        post.getTags().clear();

        Set<Tag> tags = tagService.resolveTags(form.getTags());

        for (Tag tag: tags) {
            post.addTag(tag);
        }

        return post;
    }

    @Transactional
    public void delete(Long id) {

        postRepository.deleteById(id);
    }
}