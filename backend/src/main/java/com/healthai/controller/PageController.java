package com.healthai.controller;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final Path frontendPages =
            Paths.get("C:/Users/PILR/Desktop/HealthAI/frontend/pages");


    // =====================================================
    // Home / Main Dashboard
    // =====================================================

    @GetMapping("/")
    public ResponseEntity<Resource> home() {
        return servePage("user_dashboard.html");
    }


    // =====================================================
    // Dashboard
    // =====================================================

    @GetMapping("/dashboard")
    public ResponseEntity<Resource> dashboard() {
        return servePage("user_dashboard.html");
    }


    // =====================================================
    // User Dashboard HTML
    // =====================================================

    @GetMapping("/user_dashboard.html")
    public ResponseEntity<Resource> userDashboard() {
        return servePage("user_dashboard.html");
    }


    // =====================================================
    // Login
    // =====================================================

    @GetMapping("/login")
    public ResponseEntity<Resource> loginPage() {
        return servePage("login.html");
    }


    @GetMapping("/login.html")
    public ResponseEntity<Resource> login() {
        return servePage("login.html");
    }


    // =====================================================
    // Signup
    // =====================================================

    @GetMapping("/signup")
    public ResponseEntity<Resource> signupPage() {
        return servePage("signup.html");
    }


    @GetMapping("/signup.html")
    public ResponseEntity<Resource> signup() {
        return servePage("signup.html");
    }


    // =====================================================
    // Serve Frontend HTML
    // =====================================================

    private ResponseEntity<Resource> servePage(
            String fileName
    ) {

        try {

            Path filePath =
                    frontendPages
                            .resolve(fileName)
                            .normalize();


            Resource resource =
                    new UrlResource(
                            filePath.toUri()
                    );


            if (
                    !resource.exists() ||
                    !resource.isReadable()
            ) {

                System.out.println(
                        "Frontend page not found: "
                        + filePath
                );

                return ResponseEntity
                        .notFound()
                        .build();
            }


            return ResponseEntity
                    .ok()
                    .contentType(
                            MediaType.TEXT_HTML
                    )
                    .body(resource);

        }

        catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }
}