package org.project.mealsearch.model;

public record SearchResultItem(
        long id,
        String title,
        String description,
        String url,
        String siteName,
        String category
) {}
