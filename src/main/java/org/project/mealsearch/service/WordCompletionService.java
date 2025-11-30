package org.project.mealsearch.service;

import org.project.mealsearch.legacy.jasmeen.WordCompletionAdapter;
import org.project.mealsearch.legacy.jasmeen.WordCompletionTask2;
import org.project.mealsearch.model.SearchSuggestion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class WordCompletionService {

    private final WordCompletionAdapter adapter;

    public WordCompletionService(@Value("${meals.csv-path}") String csvPath) throws IOException {
        WordCompletionTask2.BuildStats stats = WordCompletionTask2.buildFromCsvLoose(csvPath);
        this.adapter = new WordCompletionAdapter(stats.bag);
    }

    public List<WordCompletionTask2.PairWF> completePrefix(String prefix, int limit) {
        if (prefix == null || prefix.isBlank()) {
            return List.of();
        }
        List<WordCompletionTask2.PairWF> matches = adapter.complete(prefix.toLowerCase(), limit);
        return WordCompletionTask2.topKByFreqHeap(matches, limit);
    }

    public List<SearchSuggestion> toSuggestions(List<WordCompletionTask2.PairWF> pairs,
                                                java.util.function.Function<String, Integer> searchFreq,
                                                java.util.function.Function<String, Integer> totalFreq) {
        return pairs.stream()
                .map(p -> new SearchSuggestion(p.w, searchFreq.apply(p.w), totalFreq.apply(p.w)))
                .toList();
    }
}
