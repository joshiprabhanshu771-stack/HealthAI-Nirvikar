package com.healthai.controller;

import com.healthai.entity.User;
import com.healthai.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;


    // ============================================================
    // SIGNUP
    // ============================================================

    @PostMapping("/signup")
    public String signup(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String mobile,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        // 1. Check password confirmation
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "signup";
        }

        // 2. Check email already exists
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email is already registered.");
            return "signup";
        }

        // 3. Check mobile already exists
        if (userRepository.existsByMobile(mobile)) {
            model.addAttribute("error", "Mobile number is already registered.");
            return "signup";
        }

        // 4. Create user
        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setMobile(mobile);
        user.setPassword(password);

        // 5. Save user in MySQL
        userRepository.save(user);

        // 6. Successful signup → Login page
        return "redirect:/login";
    }


    // ============================================================
    // LOGIN
    // ============================================================

    @PostMapping("/api/auth/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> loginRequest) {

        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Map<String, Object> response = new HashMap<>();

        // Check empty values
        if (email == null || password == null
                || email.trim().isEmpty()
                || password.isEmpty()) {

            response.put("success", false);
            response.put("message", "Email and password are required.");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }


        // Find user by email
        Optional<User> optionalUser =
                userRepository.findByEmail(email.trim());


        // User not found
        if (optionalUser.isEmpty()) {

            response.put("success", false);
            response.put("message", "Invalid email or password.");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }


        // Get user
        User user = optionalUser.get();


        // Check password
        if (!user.getPassword().equals(password)) {

            response.put("success", false);
            response.put("message", "Invalid email or password.");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }


        // Login successful
        response.put("success", true);
        response.put("message", "Login successful.");

        return ResponseEntity.ok(response);
    }
}