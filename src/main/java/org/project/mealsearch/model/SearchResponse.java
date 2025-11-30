package org.project.mealsearch.model;

import java.util.List;

public record SearchResponse(
        String query,
        SpellCheckResult spellCheck,
        List<SearchSuggestion> suggestions,
        List<TopSearch> topSearches
) {}
