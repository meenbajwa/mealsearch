package org.project.mealsearch.service;


import org.project.mealsearch.config.MealDataLoader;
import org.project.mealsearch.legacy.hemit.BoyerMoore;
import org.project.mealsearch.legacy.sarvesh.FrequencyCount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FrequencyCountService {

    private final MealDataLoader loader;
    private final Map<String, Integer> globalCounts;

    public FrequencyCountService(MealDataLoader loader,
                                 @Value("${meals.csv-path}") String csvPath) throws IOException {
        this.loader = loader;
        this.globalCounts = FrequencyCount.buildGlobalFrequency(List.of(Path.of(csvPath)));
    }

    public int countTotalOccurrences(String word) {
        if (word == null || word.isBlank()) {
            return 0;
        }
        BoyerMoore bm = new BoyerMoore(word.toLowerCase());
        return bm.countOccurrences_hemit(loader.getCorpusLower(), false);
    }

    public Map<String, Integer> countBySite(String word) {
        Map<String, Integer> out = new HashMap<>();
        if (word == null || word.isBlank()) {
            return out;
        }
        for (var site : loader.getSites()) {
            String text = loader.getCorpusBySite().get(site.siteId());
            if (text == null) {
                continue;
            }
            int c = new BoyerMoore(word.toLowerCase()).countOccurrences_hemit(text, false);
            out.put(site.siteId(), c);
        }
        return out;
    }

    public int getGlobalCountFromSarvesh(String word) {
        if (word == null) {
            return 0;
        }
        return globalCounts.getOrDefault(word.toLowerCase(), 0);
    }
}
