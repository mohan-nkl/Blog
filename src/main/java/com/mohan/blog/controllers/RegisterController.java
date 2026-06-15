package com.mohan.blog.controllers;

import com.mohan.blog.dtos.RegisterForm;
import com.mohan.blog.services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm registerForm,
                           BindingResult result, RedirectAttributes redirectAttributes) {

        if (registerForm.getPassword() != null
                && !registerForm.getPassword().equals(registerForm.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match");
        }

        if (userService.emailExists(registerForm.getEmail())) {
            result.rejectValue("email", "email.taken", "That email is already registered");
        }

        if (result.hasErrors()) {
            return "register";
        }

        userService.register(registerForm);

        redirectAttributes.addFlashAttribute("message", "Account created. Please log in.");

        return "redirect:/login";
    }
}