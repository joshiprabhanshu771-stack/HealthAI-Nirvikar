package com.healthai.controller;

import com.healthai.model.User;
import com.healthai.com.healthai.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private final UserService userService;

    public PageController(UserService userService) {
        this.userService = userService;
    }

    // =========================
    // LOGIN PAGE
    // =========================

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


    // =========================
    // SIGNUP PAGE
    // =========================

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }


    // =========================
    // SIGNUP PROCESS
    // =========================

    @PostMapping("/signup")
    public String signup(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String mobile,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        // Check password confirmation
        if (!password.equals(confirmPassword)) {

            model.addAttribute(
                    "error",
                    "Passwords do not match."
            );

            return "signup";
        }


        // Create User object
        User user = new User(
                name,
                email,
                password,
                mobile
        );


        // Save user
        boolean registered =
                userService.registerUser(user);


        // Email already exists
        if (!registered) {

            model.addAttribute(
                    "error",
                    "An account with this email already exists."
            );

            return "signup";
        }


        // Registration successful
        model.addAttribute(
                "success",
                "Account created successfully. Please login."
        );

        return "login";
    }


    // =========================
    // LOGIN PROCESS
    // =========================

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        User user =
                userService.loginUser(
                        email,
                        password
                );


        // Wrong email or password
        if (user == null) {

            model.addAttribute(
                    "error",
                    "Incorrect email or password. Please try again."
            );

            return "login";
        }


        // Correct login
        return "redirect:/user_dashboard";
    }


    // =========================
    // DASHBOARD
    // =========================

    @GetMapping("/user_dashboard")
    public String dashboard() {
        return "user_dashboard";
    }
}