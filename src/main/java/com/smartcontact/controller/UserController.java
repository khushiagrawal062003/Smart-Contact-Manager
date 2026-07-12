package com.smartcontact.controller;

import com.smartcontact.entity.User;
import com.smartcontact.helper.Message;
import com.smartcontact.service.ContactService;
import com.smartcontact.service.FileService;
import com.smartcontact.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private FileService fileService;

    // Add common user details to all request models in this controller
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            User user = userService.getUserByEmail(email);
            model.addAttribute("loggedUser", user);
        }
    }

    // Dashboard Index
    @GetMapping("/index")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard - Smart Contact Manager");
        model.addAttribute("activePage", "dashboard");
        
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        
        long totalContacts = contactService.countTotalContacts(user.getId());
        long favoriteContacts = contactService.countFavoriteContacts(user.getId());
        
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("favoriteContacts", favoriteContacts);
        model.addAttribute("recentContacts", contactService.getRecentlyAdded(user.getId()));
        
        // Category Statistics
        List<Object[]> categoryStats = contactService.getCategoryStats(user.getId());
        model.addAttribute("categoryStats", categoryStats);
        
        return "normal/dashboard";
    }

    // View User Profile
    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("title", "My Profile - Smart Contact Manager");
        model.addAttribute("activePage", "profile");
        return "normal/profile";
    }

    // Settings (Change Password) Page
    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("title", "Settings - Smart Contact Manager");
        model.addAttribute("activePage", "settings");
        return "normal/settings";
    }

    // Change Password Handler
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        boolean success = userService.changePassword(user, oldPassword, newPassword);
        if (success) {
            redirectAttributes.addFlashAttribute("message", new Message("Password changed successfully!", "success"));
        } else {
            redirectAttributes.addFlashAttribute("message", new Message("Incorrect old password! Try again.", "danger"));
        }
        return "redirect:/user/settings";
    }

    // Update User Profile Handler
    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute("loggedUser") User loggedUser,
                                @RequestParam("profileImage") MultipartFile file,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            String email = principal.getName();
            User dbUser = userService.getUserByEmail(email);

            dbUser.setName(loggedUser.getName());
            dbUser.setAbout(loggedUser.getAbout());

            // Handle Profile Image Upload
            if (!file.isEmpty()) {
                // Delete old image if it exists and is not default
                if (dbUser.getImageUrl() != null && !dbUser.getImageUrl().equals("default.png")) {
                    fileService.deleteImage(dbUser.getImageUrl(), "user");
                }
                String newFileName = fileService.uploadImage(file, "user");
                dbUser.setImageUrl(newFileName);
            }

            userService.updateUser(dbUser);
            redirectAttributes.addFlashAttribute("message", new Message("Profile updated successfully!", "success"));

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", new Message("Error updating profile: " + e.getMessage(), "danger"));
        }
        return "redirect:/user/profile";
    }
}
