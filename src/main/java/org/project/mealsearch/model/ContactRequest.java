package org.project.mealsearch.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ContactRequest {
    @NotBlank
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s'.-]{1,49}$")
    private String name;

    @NotBlank
    @Email
    private String email;

    @Pattern(regexp = "^[0-9+()\\-\\s]*$")
    private String phone;

    @NotBlank
    @Size(min = 10, max = 500)
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
