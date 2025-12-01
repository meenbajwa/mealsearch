package org.project.mealsearch.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ContactRequest {
    @NotBlank(message = "name is required")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z\\s'.-]{1,49}$",
            message = "name must be 2-50 characters (letters, spaces, . ' -)"
    )
    private String name;

    @NotBlank(message = "email is required")
    @Pattern(
            // basic RFC 5322 local, domains with multi-label TLDs (e.g., .co.uk, .tech)
            regexp = "^(?=.{3,254}$)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$",
            message = "email must be valid (e.g., user@example.com, supports .co, .co.uk, .com, .tech)"
    )
    private String email;

    @Pattern(
            // allow blank (optional) OR an intl-style phone number with country code and separators
            regexp = "^$|^(?:\\+?\\d{1,3}[\\s.-]?)?(?:\\(?\\d{2,4}\\)?[\\s.-]?){1,3}\\d{3,4}$",
            message = "phone must be a valid number with optional country code (e.g., +1 647 555 1234)"
    )
    private String phone;

    @NotBlank(message = "message is required")
    @Size(min = 10, max = 500, message = "message must be between 10 and 500 characters")
    private String message;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
