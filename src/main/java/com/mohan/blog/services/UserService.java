package com.mohan.blog.services;

import com.mohan.blog.dtos.RegisterForm;
import com.mohan.blog.models.Role;
import com.mohan.blog.models.User;
import com.mohan.blog.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
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

    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User register(RegisterForm form) {

        User user = new User(form.getName(), form.getEmail(),
                passwordEncoder.encode(form.getPassword()), Role.AUTHOR);

        return userRepository.save(user);
    }
}