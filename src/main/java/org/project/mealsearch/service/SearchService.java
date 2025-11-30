package org.project.mealsearch.service;

import org.project.mealsearch.config.MealDataLoader;
import org.project.mealsearch.legacy.jasmeen.WordCompletionTask2;
import org.project.mealsearch.model.SearchResponse;
import org.project.mealsearch.model.SearchSuggestion;
import org.project.mealsearch.model.SpellCheckResult;
import org.project.mealsearch.model.TopSearch;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final WordCompletionService wordCompletionService;
    private final SpellCheckerService spellCheckerService;
    private final SearchFrequencyService searchFrequencyService;
    private final FrequencyCountService frequencyCountService;

    public SearchService(WordCompletionService wordCompletionService,
                         SpellCheckerService spellCheckerService,
                         SearchFrequencyService searchFrequencyService,
                         FrequencyCountService frequencyCountService,
                         MealDataLoader loader) {
        this.wordCompletionService = wordCompletionService;
        this.spellCheckerService = spellCheckerService;
        this.searchFrequencyService = searchFrequencyService;
        this.frequencyCountService = frequencyCountService;
        this.spellCheckerService.loadDictionary(loader.getTokens());
    }

    public SearchResponse suggest(String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase();
        searchFrequencyService.incrementAndGet(normalized);

        String suggested = spellCheckerService.suggestClosestWord(normalized);
        SpellCheckResult spellCheck = new SpellCheckResult(
                !suggested.equals(normalized),
                normalized,
                suggested.equals(normalized) ? null : suggested
        );

        List<WordCompletionTask2.PairWF> completions = wordCompletionService.completePrefix(suggested, 5);
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
}
