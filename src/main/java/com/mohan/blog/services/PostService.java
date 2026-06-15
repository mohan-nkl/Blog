package com.mohan.blog.services;

import com.mohan.blog.dtos.PostForm;
import com.mohan.blog.models.Post;
import com.mohan.blog.models.Role;
import com.mohan.blog.models.Tag;
import com.mohan.blog.models.User;
import com.mohan.blog.repositories.PostRepository;
import com.mohan.blog.repositories.UserRepository;
import com.mohan.blog.specifications.PostSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    public Post create(PostForm form, User currentUser) {

        User author = resolveAuthor(form, currentUser);

        Post post = new Post(form.getTitle(), form.getExcerpt(), form.getContent(), author);
        applyPublishState(post, form.isPublished());
        applyTags(post, form.getTags());

        return postRepository.save(post);
    }

    @Transactional
    public Post update(Long id, PostForm form, User currentUser) {

        Post post = getPost(id);
        ensureCanModify(post, currentUser);

        post.setTitle(form.getTitle());
        post.setExcerpt(form.getExcerpt());
        post.setContent(form.getContent());

        // Only an admin may reassign the author; an author's post stays their own.
        if (isAdmin(currentUser)) {
            post.setAuthor(loadAuthor(form.getAuthorId()));
        }

        applyPublishState(post, form.isPublished());
        applyTags(post, form.getTags());

        return post;
    }

    @Transactional
    public void delete(Long id, User currentUser) {

        Post post = getPost(id);
        ensureCanModify(post, currentUser);

        postRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<User> findAuthorsWithPosts() {
        return postRepository.findDistinctAuthors();
    }

    // ----- Author resolution -----

    /** Authors always write as themselves; only an admin may choose the author. */
    private User resolveAuthor(PostForm form, User currentUser) {

        if (isAdmin(currentUser)) {
            return loadAuthor(form.getAuthorId());
        }
        return loadAuthor(currentUser.getId());
    }

    private User loadAuthor(Long authorId) {

        Optional<User> userOptional = userRepository.findById(authorId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Author not found: " + authorId);
        }
        return userOptional.get();
    }

    // ----- Permission checks -----

    private void ensureCanModify(Post post, User currentUser) {

        if (isAdmin(currentUser)) {
            return;
        }
        if (isOwner(post, currentUser)) {
            return;
        }
        throw new AccessDeniedException("You may only modify your own posts.");
    }

    private boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    private boolean isOwner(Post post, User currentUser) {
        return post.getAuthor() != null
                && post.getAuthor().getId().equals(currentUser.getId());
    }

    // ----- Field application -----

    /** Sets the published flag, and stamps publishedAt only on the false -> true transition. */
    private void applyPublishState(Post post, boolean nowPublished) {

        boolean wasPublished = post.isPublished();
        post.setPublished(nowPublished);

        if (nowPublished && !wasPublished) {
            post.setPublishedAt(LocalDateTime.now());
        }
    }

    private void applyTags(Post post, String tagCsv) {

        post.getTags().clear();
        Set<Tag> tags = tagService.resolveTags(tagCsv);
        for (Tag tag : tags) {
            post.addTag(tag);
        }
    }

}