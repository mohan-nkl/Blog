package com.mohan.blog.specifications;

import com.mohan.blog.models.Post;
import com.mohan.blog.models.Tag;
import com.mohan.blog.models.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class PostSpecifications {

    private PostSpecifications() { }   // utility class — no instances

    /** Only published posts. Always applied for the public list. */
    public static Specification<Post> isPublished() {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("published"));
    }

    /** Filter by author id; null id = no filter (no-op). */
    public static Specification<Post> hasAuthor(Long authorId) {

        return (root, query, criteriaBuilder) -> {
            if (authorId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                    root.get("author").get("id"),
                    authorId
            );
        };
    }

    /** Filter by tag id; null = no-op. Joins the tags collection. */
    public static Specification<Post> hasTag(Long tagId) {

        return (root, query, criteriaBuilder) -> {
            if (tagId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<Post, Tag> tagJoin = root.join("tags");

            return criteriaBuilder.equal(tagJoin.get("id"), tagId);
        };
    }

    /** Case-insensitive search across title, excerpt, content, author name, tag name. */
    public static Specification<Post> matchesSearch(String searchText) {

        return (root, query, criteriaBuilder) -> {

            if (searchText == null || searchText.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String searchPattern = "%" + searchText.trim().toLowerCase() + "%";

            query.distinct(true);

            Join<Post, User> authorJoin = root.join("author", JoinType.LEFT);
            Join<Post, Tag> tagJoin = root.join("tags", JoinType.LEFT);

            Predicate titleMatches = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern);
            Predicate excerptMatches = criteriaBuilder.like(criteriaBuilder.lower(root.get("excerpt")), searchPattern);
            Predicate contentMatches = criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), searchPattern);
            Predicate authorMatches = criteriaBuilder.like(criteriaBuilder.lower(authorJoin.get("name")), searchPattern);
            Predicate tagMatches = criteriaBuilder.like(criteriaBuilder.lower(tagJoin.get("name")), searchPattern);

            return criteriaBuilder.or(titleMatches, excerptMatches, contentMatches, authorMatches, tagMatches);
        };
    }
}