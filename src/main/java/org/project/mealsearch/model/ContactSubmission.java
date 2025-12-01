package org.project.mealsearch.model;

public record ContactSubmission(
        String name,
        String email,
        String phone,
        String message
) {}
