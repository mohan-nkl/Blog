package com.mohan.blog.services;

import com.mohan.blog.models.Tag;
import com.mohan.blog.repositories.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public List<Tag> findAll() {

        // for the tag filter dropdown
        return tagRepository.findAll();
    }

    @Transactional
    public Set<Tag> resolveTags(String csv) {

        Set<Tag> result = new HashSet<>();

        if (csv == null || csv.isBlank()) {
            return result;
        }

        String[] tagNames = csv.split(",");

        for (String tagName: tagNames) {

            String normalizedTagName = tagName.trim().toLowerCase();

            if (normalizedTagName.isEmpty()) {
                continue;
            }

            Tag tag;
            Optional<Tag> tagOptional = tagRepository.findByName(normalizedTagName);
            if (tagOptional.isPresent()) {
                tag = tagOptional.get();
            }
            else {
                Tag newTag = new Tag(normalizedTagName);
                tag = tagRepository.save(newTag);
            }

            result.add(tag);
        }

        return result;
    }
}