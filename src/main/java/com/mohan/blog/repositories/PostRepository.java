package com.mohan.blog.repositories;

import com.mohan.blog.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    // Public list page: only published posts, paged. Newest-first is decided
    // by the Pageable's Sort we pass in from the service.
    Page<Post> findByPublishedTrue(Pageable pageable);

    // Has anyone already used this exact title? (handy for duplicate checks)
    boolean existsByTitle(String title);
}