# Spring Boot Blog Application

A blog application built using Spring Boot, Spring MVC, Spring Data JPA, Thymeleaf, and PostgreSQL.

This project is being developed as part of a Spring Boot learning journey and demonstrates common web application features such as CRUD operations, comments, pagination, filtering, sorting, and search.

## Features

### Posts

* Create blog posts
* View all published posts
* View individual blog posts
* Update existing posts
* Delete posts

### Tags

* Associate multiple tags with a post
* Reuse existing tags
* Automatically create new tags when needed

### Comments

* Add comments to posts
* View comments on a post

### Search & Filtering

* Search posts by:

  * Title
  * Content
  * Excerpt
  * Author
  * Tags

* Filter posts by:

  * Author
  * Tag

### Sorting

* Sort posts by:

  * Published Date
  * Created Date
  * Title

### Pagination

* Paginated blog listing
* Configurable page size

## Tech Stack

### Backend

* Java
* Spring Boot
* Spring MVC
* Spring Data JPA
* Hibernate

### Frontend

* Thymeleaf
* HTML
* CSS

### Database

* PostgreSQL

## Project Structure

```text
src/main/java/com/mohan/blog

├── controllers
├── dtos
├── models
├── repositories
├── services
└── specifications
```

## Database Schema

### Users

* id
* name
* email
* password

### Posts

* id
* title
* excerpt
* content
* author
* published_at
* is_published
* created_at
* updated_at

### Tags

* id
* name
* created_at
* updated_at

### Comments

* id
* name
* email
* comment
* post_id
* created_at
* updated_at

## Current Status

### Part 1 - CRUD

* [x] Posts CRUD
* [x] Comments
* [x] Pagination
* [x] Search
* [x] Filtering
* [x] Sorting

### Planned Features

#### Part 2

* Spring Security Authentication
* Authorization using Roles
* Author and Admin permissions

#### Part 3

* Deployment to AWS / Heroku

#### Part 4

* REST APIs for all features

## Learning Goals

This project is intended to provide hands-on experience with:

* Spring MVC request lifecycle
* Service and Repository layers
* DTOs and Validation
* JPA Entity Relationships
* Hibernate Dirty Checking
* Pagination with Pageable and Page
* Dynamic Queries using JPA Specifications
* Thymeleaf Templates
* PostgreSQL Integration

## Author

Mohan Lakkoju
