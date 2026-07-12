package com.smartcontact.controller;

import com.smartcontact.entity.Contact;
import com.smartcontact.entity.User;
import com.smartcontact.helper.CSVHelper;
import com.smartcontact.helper.Message;
import com.smartcontact.service.ContactService;
import com.smartcontact.service.FileService;
import com.smartcontact.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class ContactController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private FileService fileService;

    // Common Model Binder
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            User user = userService.getUserByEmail(email);
            model.addAttribute("loggedUser", user);
        }
    }

    // Add Contact GET Page
    @GetMapping("/add-contact")
    public String addContactForm(Model model) {
        model.addAttribute("title", "Add Contact - Smart Contact Manager");
        model.addAttribute("activePage", "add-contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact";
    }

    // Process Contact Form
    @PostMapping("/process-contact")
    public String processContact(@Valid @ModelAttribute("contact") Contact contact,
                                 BindingResult result,
                                 @RequestParam("contactImage") MultipartFile file,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("title", "Add Contact - Smart Contact Manager");
                model.addAttribute("activePage", "add-contact");
                model.addAttribute("contact", contact);
                return "normal/add_contact";
            }

            String email = principal.getName();
            User user = userService.getUserByEmail(email);

            // Upload profile picture if provided
            if (!file.isEmpty()) {
                String fileName = fileService.uploadImage(file, "contact");
                contact.setImage(fileName);
            } else {
                contact.setImage("contact_default.png");
            }

            contactService.saveContact(contact, user);
            redirectAttributes.addFlashAttribute("message", new Message("Contact added successfully!", "success"));
            return "redirect:/user/add-contact";

        } catch (Exception e) {
            model.addAttribute("title", "Add Contact - Smart Contact Manager");
            model.addAttribute("activePage", "add-contact");
            model.addAttribute("contact", contact);
            model.addAttribute("message", new Message("Error saving contact: " + e.getMessage(), "danger"));
            return "normal/add_contact";
        }
    }

    // Show Contacts Page with Advanced Search, Filtering, Sorting and Pagination
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") int page,
                               @RequestParam(value = "sort", defaultValue = "name_asc") String sort,
                               @RequestParam(value = "category", required = false) String category,
                               @RequestParam(value = "favorite", required = false) Boolean favorite,
                               @RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "nameFilter", required = false) String nameFilter,
                               @RequestParam(value = "phoneFilter", required = false) String phoneFilter,
                               @RequestParam(value = "emailFilter", required = false) String emailFilter,
                               @RequestParam(value = "workFilter", required = false) String workFilter,
                               Model model, Principal principal) {
        
        model.addAttribute("title", "My Contacts - Smart Contact Manager");
        
        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        // Sorting Logic (e.g. name_asc, name_desc, cId_desc)
        String sortBy = "name";
        Sort.Direction direction = Sort.Direction.ASC;
        if (sort.contains("_")) {
            String[] sortParts = sort.split("_");
            sortBy = sortParts[0];
            direction = sortParts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        }
        
        Pageable pageable = PageRequest.of(page, 10, Sort.by(direction, sortBy));
        Page<Contact> contactsPage;

        // Search Routing
        boolean isSearching = false;
        if (search != null && !search.trim().isEmpty()) {
            // Global Search
            contactsPage = contactService.searchGlobal(user.getId(), search.trim(), pageable);
            isSearching = true;
        } else if ((nameFilter != null && !nameFilter.trim().isEmpty()) ||
                   (phoneFilter != null && !phoneFilter.trim().isEmpty()) ||
                   (emailFilter != null && !emailFilter.trim().isEmpty()) ||
                   (workFilter != null && !workFilter.trim().isEmpty()) ||
                   (category != null && !category.trim().isEmpty()) ||
                   favorite != null) {
            // Multi-Filter Advanced Search
            contactsPage = contactService.searchMultiFilter(
                    user.getId(),
                    nameFilter != null ? nameFilter.trim() : null,
                    phoneFilter != null ? phoneFilter.trim() : null,
                    emailFilter != null ? emailFilter.trim() : null,
                    workFilter != null ? workFilter.trim() : null,
                    category != null && !category.isEmpty() ? category : null,
                    favorite,
                    pageable
            );
            isSearching = true;
        } else {
            // Default View
            contactsPage = contactService.getContactsByUser(user.getId(), pageable);
        }

        model.addAttribute("contacts", contactsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contactsPage.getTotalPages());
        model.addAttribute("totalItems", contactsPage.getTotalElements());
        model.addAttribute("activePage", "view-contacts");
        
        // Pass parameters back to page to maintain UI state
        model.addAttribute("sort", sort);
        model.addAttribute("category", category);
        model.addAttribute("favorite", favorite);
        model.addAttribute("search", search);
        model.addAttribute("nameFilter", nameFilter);
        model.addAttribute("phoneFilter", phoneFilter);
        model.addAttribute("emailFilter", emailFilter);
        model.addAttribute("workFilter", workFilter);
        model.addAttribute("isSearching", isSearching);

        return "normal/show_contacts";
    }

    // View Detailed Contact page
    @GetMapping("/contact/{cId}")
    public String contactDetail(@PathVariable("cId") Long cId, Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        Contact contact = contactService.getContactById(cId);
        
        // Security check: ensure contact belongs to logged-in user
        if (contact != null && contact.getUser().getId().equals(user.getId())) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName() + " - Details");
            model.addAttribute("activePage", "view-contacts");
            return "normal/contact_detail";
        }

        return "redirect:/user/show-contacts/0?error=unauthorized";
    }

    // Delete Contact
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Long cId, Principal principal, RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        Contact contact = contactService.getContactById(cId);
        if (contact != null && contact.getUser().getId().equals(user.getId())) {
            // Delete contact photo
            if (contact.getImage() != null && !contact.getImage().equals("contact_default.png")) {
                fileService.deleteImage(contact.getImage(), "contact");
            }
            contactService.deleteContact(cId, user);
            redirectAttributes.addFlashAttribute("message", new Message("Contact deleted successfully!", "success"));
        } else {
            redirectAttributes.addFlashAttribute("message", new Message("Access denied or contact not found!", "danger"));
        }
        return "redirect:/user/show-contacts/0";
    }

    // Edit Contact GET Form
    @GetMapping("/update-contact/{cId}")
    public String updateContactForm(@PathVariable("cId") Long cId, Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        Contact contact = contactService.getContactById(cId);
        if (contact != null && contact.getUser().getId().equals(user.getId())) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", "Update " + contact.getName());
            model.addAttribute("activePage", "view-contacts");
            return "normal/update_contact";
        }
        return "redirect:/user/show-contacts/0?error=unauthorized";
    }

    // Process Contact Update
    @PostMapping("/process-update")
    public String processUpdate(@Valid @ModelAttribute("contact") Contact contact,
                                BindingResult result,
                                @RequestParam("contactImage") MultipartFile file,
                                Principal principal,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("title", "Update Contact - Smart Contact Manager");
                model.addAttribute("activePage", "view-contacts");
                model.addAttribute("contact", contact);
                return "normal/update_contact";
            }

            String email = principal.getName();
            User user = userService.getUserByEmail(email);

            Contact oldContact = contactService.getContactById(contact.getcId());
            if (oldContact == null || !oldContact.getUser().getId().equals(user.getId())) {
                throw new Exception("Access Denied!");
            }

            // Image Update Processing
            if (!file.isEmpty()) {
                // Delete old image if not default
                if (oldContact.getImage() != null && !oldContact.getImage().equals("contact_default.png")) {
                    fileService.deleteImage(oldContact.getImage(), "contact");
                }
                String newFileName = fileService.uploadImage(file, "contact");
                contact.setImage(newFileName);
            } else {
                // Retain old image
                contact.setImage(oldContact.getImage());
            }

            contact.setUser(user);
            contactService.updateContact(contact);
            redirectAttributes.addFlashAttribute("message", new Message("Contact updated successfully!", "success"));
            return "redirect:/user/contact/" + contact.getcId();

        } catch (Exception e) {
            model.addAttribute("title", "Update Contact - Smart Contact Manager");
            model.addAttribute("activePage", "view-contacts");
            model.addAttribute("contact", contact);
            model.addAttribute("message", new Message("Error updating contact: " + e.getMessage(), "danger"));
            return "normal/update_contact";
        }
    }

    // CSV Export Handler
    @GetMapping("/export-csv")
    public void exportCSV(HttpServletResponse response, Principal principal) throws IOException {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        
        List<Contact> contacts = contactService.getAllContactsForUser(user.getId());
        
        String csvContent = CSVHelper.contactsToCSV(contacts);
        
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contacts_" + user.getName().toLowerCase().replace(" ", "_") + ".csv");
        response.getWriter().write(csvContent);
    }

    // CSV Import Handler
    @PostMapping("/import-csv")
    public String importCSV(@RequestParam("csvFile") MultipartFile file, Principal principal, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", new Message("Please select a CSV file to upload!", "danger"));
            return "redirect:/user/show-contacts/0";
        }

        try {
            String email = principal.getName();
            User user = userService.getUserByEmail(email);

            InputStream is = file.getInputStream();
            List<Contact> importedContacts = CSVHelper.csvToContacts(is);
            
            for (Contact contact : importedContacts) {
                contactService.saveContact(contact, user);
            }

            redirectAttributes.addFlashAttribute("message", new Message("Successfully imported " + importedContacts.size() + " contacts from CSV!", "success"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", new Message("Failed to parse and import CSV: " + e.getMessage(), "danger"));
        }
        return "redirect:/user/show-contacts/0";
    }
}
