package org.project.mealsearch.service;

import org.project.mealsearch.config.MealDataLoader;
import org.project.mealsearch.legacy.jasmeen.WordCompletionTask2;
import org.project.mealsearch.model.SearchHitResponse;
import org.project.mealsearch.model.SearchResponse;
import org.project.mealsearch.model.SearchResultItem;
import org.project.mealsearch.model.SearchSuggestion;
import org.project.mealsearch.model.SpellCheckResult;
import org.project.mealsearch.model.TopSearch;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Locale;

@Service
public class SearchService {

    private final WordCompletionService wordCompletionService;
    private final SpellCheckerService spellCheckerService;
    private final SearchFrequencyService searchFrequencyService;
    private final FrequencyCountService frequencyCountService;
    private final MealDataLoader mealDataLoader;

    public SearchService(WordCompletionService wordCompletionService,
                         SpellCheckerService spellCheckerService,
                         SearchFrequencyService searchFrequencyService,
                         FrequencyCountService frequencyCountService,
                         MealDataLoader loader) {
        this.wordCompletionService = wordCompletionService;
        this.spellCheckerService = spellCheckerService;
        this.searchFrequencyService = searchFrequencyService;
        this.frequencyCountService = frequencyCountService;
        this.mealDataLoader = loader;
        this.spellCheckerService.loadDictionary(loader.getTokens());
    }

    public SearchResponse suggest(String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase();
        searchFrequencyService.incrementAndGet(normalized);

        List<WordCompletionTask2.PairWF> completions = wordCompletionService.completePrefix(normalized, 5);
        String suggested = normalized;
        boolean corrected = false;

        // Prefer prefix completions; only fall back to spell check when no completions are found.
        if (completions.isEmpty()) {
            suggested = spellCheckerService.suggestClosestWord(normalized);
            corrected = !suggested.equals(normalized);
            completions = wordCompletionService.completePrefix(suggested, 5);
        }

        SpellCheckResult spellCheck = new SpellCheckResult(
                corrected,
                normalized,
                corrected ? suggested : null
        );
        List<SearchSuggestion> suggestionList = wordCompletionService.toSuggestions(
                completions,
                searchFrequencyService::get,
                frequencyCountService::countTotalOccurrences
        );

        List<TopSearch> topSearches = searchFrequencyService.topSearches(10, frequencyCountService::countTotalOccurrences);

        return new SearchResponse(query, spellCheck, suggestionList, topSearches);
    }

    public List<TopSearch> top() {
        return searchFrequencyService.topSearches(10, frequencyCountService::countTotalOccurrences);
    }

    public SearchHitResponse registerHit(String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (normalized.isBlank()) {
            return new SearchHitResponse(normalized, 0, 0);
        }
        int total = frequencyCountService.countTotalOccurrences(normalized);
        int freq = searchFrequencyService.incrementAndGet(normalized);
        return new SearchHitResponse(normalized, freq, total);
    }

    public List<SearchResultItem> results(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String needle = query.toLowerCase(Locale.ROOT);
        return mealDataLoader.getMeals().stream()
                .filter(meal -> contains(meal.title(), needle) || contains(meal.description(), needle) || contains(meal.category(), needle))
                .limit(50)
                .map(meal -> new SearchResultItem(
                        meal.id(),
                        meal.title(),
                        meal.description(),
                        meal.sourcePage(),
                        deriveSiteName(meal.sourcePage()),
                        meal.category()
                ))
                .toList();
    }

    private boolean contains(String haystack, String needle) {
        if (haystack == null || needle == null) return false;
        return haystack.toLowerCase(Locale.ROOT).contains(needle);
    }

    private String deriveSiteName(String sourcePage) {
        if (sourcePage == null || sourcePage.isBlank()) {
            return "Unknown";
        }
        try {
            URI uri = new URI(sourcePage);
            String host = uri.getHost();
            if (host == null) {
                return sourcePage;
            }
            host = host.replace("www.", "");
            if (host.contains(".")) {
                host = host.substring(0, host.indexOf('.'));
            }
            if (host.isEmpty()) return sourcePage;
            return host.substring(0, 1).toUpperCase(Locale.ROOT) + host.substring(1);
        } catch (Exception e) {
            return sourcePage;
        }
    }
}
