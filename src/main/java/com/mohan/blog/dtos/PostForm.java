package com.mohan.blog.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PostForm {

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        private String title;

        @Size(max = 500, message = "Excerpt must be at most 500 characters")
        private String excerpt;

        @NotBlank(message = "Content is required")
        private String content;

        @NotNull(message = "Please choose an author")
        private Long authorId;

        private String tags;

        private boolean published;

        public PostForm() {

        }

        public PostForm(String title, String excerpt, String content, Long authorId, String tags, boolean published) {
                this.title = title;
                this.excerpt = excerpt;
                this.content = content;
                this.authorId = authorId;
                this.tags = tags;
                this.published = published;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public String getExcerpt() {
                return excerpt;
        }

        public void setExcerpt(String excerpt) {
                this.excerpt = excerpt;
        }

        public Long getAuthorId() {
                return authorId;
        }

        public void setAuthorId(Long authorId) {
                this.authorId = authorId;
        }

        public String getContent() {
                return content;
        }

        public void setContent(String content) {
                this.content = content;
        }

        public String getTags() {
                return tags;
        }

        public void setTags(String tags) {
                this.tags = tags;
        }

        public boolean isPublished() {
                return published;
        }

        public void setPublished(Boolean published) {
                this.published = published;
        }
}