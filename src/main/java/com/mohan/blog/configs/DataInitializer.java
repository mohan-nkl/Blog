package com.mohan.blog.configs;

import com.mohan.blog.models.Comment;
import com.mohan.blog.models.Post;
import com.mohan.blog.models.Role;
import com.mohan.blog.models.Tag;
import com.mohan.blog.models.User;
import com.mohan.blog.repositories.PostRepository;
import com.mohan.blog.repositories.UserRepository;
import com.mohan.blog.services.TagService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TagService tagService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PostRepository postRepository,
                           TagService tagService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUsersIfEmpty();
        seedPostsIfEmpty();
    }

    // ----- users -----

    private void seedUsersIfEmpty() {

        if (userRepository.count() > 0) {
            return;
        }

        User admin = new User("Admin", "admin@blog.com", passwordEncoder.encode("admin123"), Role.ADMIN);
        User asha = new User("Rafi", "rafi@blog.com", passwordEncoder.encode("rafi123"), Role.AUTHOR);
        User ravi = new User("Bhaswanth", "bhaswanth@blog.com", passwordEncoder.encode("bhaswanth123"), Role.AUTHOR);

        userRepository.saveAll(List.of(admin, asha, ravi));
    }

    // ----- posts -----

    private void seedPostsIfEmpty() {

        if (postRepository.count() > 0) {
            return;
        }

        User bhaswanth = requireUser("bhaswanth@blog.com");
        User rafi = requireUser("rafi@blog.com");
        User admin = requireUser("admin@blog.com");

        LocalDateTime now = LocalDateTime.now();

        Post p1 = buildPost("Getting Started with Spring Boot 4",
                "A gentle introduction to bootstrapping your first Spring Boot 4 application.",
                "Spring Boot removes most of the boilerplate from setting up a Spring application.\n\n"
                        + "In this post we create a project, add starters, and run it.",
                bhaswanth, "spring, java", now.minusDays(30));
        p1.addComment(new Comment("Priya", "priya@example.com", "Great starter guide, thanks!"));
        p1.addComment(new Comment("Karthik", "karthik@example.com", "Helped me set up my first project."));
        postRepository.save(p1);

        Post p2 = buildPost("Understanding Spring Data JPA",
                "Repositories, entities, and how JPA maps your objects to tables.",
                "Spring Data JPA lets you talk to the database with almost no boilerplate.\n\n"
                        + "Extend JpaRepository and you get CRUD for free.",
                rafi, "spring, jpa", now.minusDays(28));
        p2.addComment(new Comment("Meera", "meera@example.com", "The repository examples were very clear."));
        postRepository.save(p2);

        postRepository.save(buildPost("A Practical Guide to Thymeleaf",
                "Server-rendered HTML with Thymeleaf, the natural templating engine.",
                "Thymeleaf templates are valid HTML you can open in a browser.\n\n"
                        + "We cover fragments, expressions, and form binding.",
                bhaswanth, "thymeleaf, spring", now.minusDays(25)));

        postRepository.save(buildPost("Securing Apps with Spring Security",
                "Authentication and authorization, explained from the ground up.",
                "Spring Security locks everything down by default.\n\n"
                        + "You then open it up with a SecurityFilterChain that matches your rules.",
                admin, "security, spring", now.minusDays(22)));

        postRepository.save(buildPost("JWT Authentication Explained",
                "How stateless token-based auth actually works.",
                "A JWT is three Base64 chunks: header, payload, and signature.\n\n"
                        + "The signature makes it tamper-proof, but it is readable, so never store secrets in it.",
                rafi, "security, jwt", now.minusDays(20)));

        postRepository.save(buildPost("PostgreSQL Tips for Developers",
                "Practical habits for working effectively with PostgreSQL.",
                "From indexes to EXPLAIN plans, a few habits make a big difference.\n\n"
                        + "We look at patterns that keep queries fast.",
                bhaswanth, "sql, postgres", now.minusDays(18)));

        postRepository.save(buildPost("Writing Clean Service Layers",
                "Keeping business logic readable, testable, and in one place.",
                "A good service layer reads like a description of the rules.\n\n"
                        + "Small named helpers beat long tangled methods every time.",
                rafi, "java, design", now.minusDays(15)));

        postRepository.save(buildPost("Pagination, Sorting and Filtering",
                "Building list pages that scale beyond a handful of rows.",
                "Pageable and Sort give you paging and ordering with very little code.\n\n"
                        + "Specifications let you compose filters cleanly.",
                bhaswanth, "spring, jpa", now.minusDays(12)));

        postRepository.save(buildPost("REST API Best Practices",
                "Designing JSON APIs that are predictable and pleasant to use.",
                "Use the right status codes, stable response shapes, and clear errors.\n\n"
                        + "DTOs keep your entities out of the public contract.",
                admin, "rest, api", now.minusDays(9)));

        postRepository.save(buildPost("Testing Spring Boot Applications",
                "From unit tests to slice tests to full integration tests.",
                "Good tests let you change code with confidence.\n\n"
                        + "We cover @WebMvcTest, @DataJpaTest, and mocking.",
                rafi, "testing, spring", now.minusDays(6)));

        postRepository.save(buildPost("BCrypt and Password Hashing",
                "Why you never store raw passwords, and how BCrypt helps.",
                "BCrypt salts each password and is deliberately slow.\n\n"
                        + "That slowness is what makes brute-force attacks expensive.",
                bhaswanth, "security", now.minusDays(4)));

        postRepository.save(buildPost("Deploying to the Cloud",
                "Taking your app from localhost to a live server.",
                "Deployment is mostly about configuration and environment variables.\n\n"
                        + "We outline the steps for a typical cloud platform.",
                admin, "devops, cloud", now.minusDays(2)));
    }

    private Post buildPost(String title, String excerpt, String content, User author,
                           String tagsCsv, LocalDateTime publishedAt) {

        Post post = new Post(title, excerpt, content, author);
        post.setPublished(true);
        post.setPublishedAt(publishedAt);

        for (Tag tag : tagService.resolveTags(tagsCsv)) {
            post.addTag(tag);
        }

        return post;
    }

    private User requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Seed user missing: " + email));
    }
}