package com.mohan.blog.specifications;

import com.mohan.blog.models.Post;
import com.mohan.blog.models.Tag;
import com.mohan.blog.models.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class PostSpecifications {

    private PostSpecifications() { }   // utility class — no instances

    /** Only published posts. Always applied for the public list. */
    public static Specification<Post> isPublished() {
        return (root, query, cb) -> cb.isTrue(root.get("published"));
    }

    /** Filter by author id; null id = no filter (no-op). */
    public static Specification<Post> hasAuthor(Long authorId) {
        return (root, query, cb) -> authorId == null
                ? cb.conjunction()                                   // always-true: adds nothing
                : cb.equal(root.get("author").get("id"), authorId);
    }

    /** Filter by tag id; null = no-op. Joins the tags collection. */
    public static Specification<Post> hasTag(Long tagId) {
        return (root, query, cb) -> {
            if (tagId == null) {
                return cb.conjunction();
            }
            Join<Post, Tag> tags = root.join("tags");
            query.distinct(true);                                    // join can duplicate posts
            return cb.equal(tags.get("id"), tagId);
        };
    }

    /** Case-insensitive search across title, excerpt, content, author name, tag name. */
    public static Specification<Post> matchesSearch(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return cb.conjunction();
            }
            String like = "%" + q.trim().toLowerCase() + "%";
            query.distinct(true);
            Join<Post, User> author = root.join("author", JoinType.LEFT);
            Join<Post, Tag> tags = root.join("tags", JoinType.LEFT);
            return cb.or(
                    cb.like(cb.lower(root.get("title")),   like),
                    cb.like(cb.lower(root.get("excerpt")), like),
                    cb.like(cb.lower(root.get("content")), like),
                    cb.like(cb.lower(author.get("name")),  like),
                    cb.like(cb.lower(tags.get("name")),    like)
            );
        };
    }
}