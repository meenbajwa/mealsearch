package org.project.mealsearch.service;

import org.project.mealsearch.legacy.charmi.Task4;
import org.project.mealsearch.model.TopSearch;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class SearchFrequencyService {

    private final Task4.SearchFrequency searchFrequency = new Task4.SearchFrequency();
    private final String corpusLower;

    public SearchFrequencyService(org.project.mealsearch.config.MealDataLoader loader) {
        this.corpusLower = loader.getCorpusLower();
    }

    public int incrementAndGet(String word) {
        if (word == null) {
            return 0;
        }
        return searchFrequency.search(corpusLower, word.toLowerCase());
    }

    public int get(String word) {
        if (word == null) {
            return 0;
        }
        return searchFrequency.getFrequency(word.toLowerCase());
    }

    public List<TopSearch> topSearches(int limit, Function<String, Integer> totalProvider) {
        Map<String, Integer> map = searchFrequency.getAll();
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .map(e -> new TopSearch(
                        e.getKey(),
                        e.getValue(),
                        totalProvider.apply(e.getKey())
                ))
                .toList();
    }
}
