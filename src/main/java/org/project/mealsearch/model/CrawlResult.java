package org.project.mealsearch.model;

import java.util.List;
import java.util.Set;

public record CrawlResult(
        String rootUrl,
        Set<String> emails,
        Set<String> phoneNumbers,
        Set<String> links,
        List<String> visited
) {}
