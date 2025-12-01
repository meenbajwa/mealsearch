package org.project.mealsearch.model;

public record SearchHitResponse(
        String query,
        int searchFrequency,
        int totalOccurrences
) {}
