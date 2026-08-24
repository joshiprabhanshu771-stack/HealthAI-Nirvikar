package com.healthai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping
public class PageController {

    private final String frontendUrl;

    public PageController(@org.springframework.beans.factory.annotation.Value("${app.frontend.url:http://localhost:5173}") String frontendUrl) {
        this.frontendUrl = frontendUrl.replaceAll("/$", "");
    }

    @GetMapping({"/", "/dashboard", "/home"})
    public RedirectView home() {
        return redirect("/src/pages/user_dashboard.html");
    }

    @GetMapping("/login")
    public RedirectView login() {
        return redirect("/src/pages/login.html");
    }

    @GetMapping("/signup")
    public RedirectView signup() {
        return redirect("/src/pages/signup.html");
    }

    @GetMapping("/wellness")
    public RedirectView wellness() {
        return redirect("/src/pages/health_and_wellness.html");
    }

    @GetMapping({"/wellness/tips", "/health-tips"})
    public RedirectView healthTips() {
        return redirect("/src/pages/health_tips.html");
    }

    private RedirectView redirect(String path) {
        return new RedirectView(frontendUrl + path);
    }
}