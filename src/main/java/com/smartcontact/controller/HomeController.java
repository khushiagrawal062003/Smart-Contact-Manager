package com.smartcontact.controller;

import com.smartcontact.entity.User;
import com.smartcontact.helper.Message;
import com.smartcontact.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    // Home Landing Page
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "index";
    }

    // Custom Login Page
    @GetMapping("/signin")
    public String login(Model model, 
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout) {
        model.addAttribute("title", "Login - Smart Contact Manager");
        if (error != null) {
            model.addAttribute("message", new Message("Invalid username or password!", "danger"));
        }
        if (logout != null) {
            model.addAttribute("message", new Message("You have logged out successfully.", "success"));
        }
        return "login";
    }

    // Registration Form Page
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Register - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "register";
    }

    // Registration Handler
    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                               BindingResult result,
                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
                               Model model, 
                               RedirectAttributes redirectAttributes) {
        try {
            if (!agreement) {
                throw new Exception("You must agree to the Terms & Conditions!");
            }

            if (result.hasErrors()) {
                model.addAttribute("title", "Register - Smart Contact Manager");
                model.addAttribute("user", user);
                return "register";
            }

            // Check duplicate email
            User existing = userService.getUserByEmail(user.getEmail());
            if (existing != null) {
                model.addAttribute("title", "Register - Smart Contact Manager");
                model.addAttribute("user", user);
                model.addAttribute("message", new Message("Email is already registered!", "danger"));
                return "register";
            }

            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("message", new Message("Successfully Registered! Please login to continue.", "success"));
            return "redirect:/signin";

        } catch (Exception e) {
            model.addAttribute("title", "Register - Smart Contact Manager");
            model.addAttribute("user", user);
            model.addAttribute("message", new Message("Error: " + e.getMessage(), "danger"));
            return "register";
        }
    }
}
