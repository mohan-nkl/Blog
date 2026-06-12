package com.mohan.blog.repositories;

import com.mohan.blog.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // Powers "find-or-create": find the tag by name, or create it if absent.
    Optional<Tag> findByName(String name);

    // Resolve several tag names at once (e.g. when saving a post's tags).
    List<Tag> findByNameIn(Collection<String> names);
}
