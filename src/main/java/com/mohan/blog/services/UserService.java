package com.mohan.blog.services;

import com.mohan.blog.models.User;
import com.mohan.blog.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {

        // populates the author dropdown
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {

        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        throw new IllegalArgumentException("User not found: " + id);
    }
}