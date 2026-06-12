package com.mohan.blog.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CommentForm {

        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Please enter a valid email")
        private String email;

        @NotBlank(message = "Comment cannot be empty")
        private String comment;

        public CommentForm() {

        }

        public CommentForm(String name, String email, String comment) {
                this.name = name;
                this.email = email;
                this.comment = comment;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getComment() {
                return comment;
        }

        public void setComment(String comment) {
                this.comment = comment;
        }
}