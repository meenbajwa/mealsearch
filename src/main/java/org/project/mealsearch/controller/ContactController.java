package org.project.mealsearch.controller;

import org.project.mealsearch.model.ContactRequest;
import org.project.mealsearch.model.ContactResponse;
import org.project.mealsearch.model.ContactSubmission;
import org.project.mealsearch.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ContactResponse submit(@Valid @RequestBody ContactRequest request) throws IOException {
        return contactService.handle(request);
    }

    @GetMapping
    public List<ContactSubmission> submissions() throws IOException {
        return contactService.list();
    }
}
