package org.project.mealsearch.service;

import org.project.mealsearch.config.MealDataLoader;
import org.project.mealsearch.legacy.charmi.Task4;
import org.project.mealsearch.model.TopSearch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

@Service
public class SearchFrequencyService {

    private final Map<String, Integer> searchFrequency = new HashMap<>();
    private final String corpusLower;
    private final MealDataLoader loader;
    private final Path storagePath;
    private final int seedCount;

    public SearchFrequencyService(MealDataLoader loader,
                                  @Value("${search.frequency.file:search-frequency.csv}") String storageFile,
                                  @Value("${search.frequency.seed-count:5}") int seedCount) {
        this.loader = loader;
        this.corpusLower = loader.getCorpusLower();
        this.storagePath = Path.of(storageFile);
        this.seedCount = seedCount;
        loadOrSeed();
    }

    public int incrementAndGet(String word) {
        if (word == null || word.isBlank()) {
            return 0;
        }
        String key = word.toLowerCase(Locale.ROOT);
        int occurrences = Task4.kmpCountOccurrences(corpusLower, key);
        searchFrequency.put(key, searchFrequency.getOrDefault(key, 0) + 1);
        persistSafe();
        return occurrences;
    }

    public int get(String word) {
        if (word == null || word.isBlank()) {
            return 0;
        }
        return searchFrequency.getOrDefault(word.toLowerCase(Locale.ROOT), 0);
    }

    public List<TopSearch> topSearches(int limit, Function<String, Integer> totalProvider) {
        return searchFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .map(e -> new TopSearch(
                        e.getKey(),
                        e.getValue(),
                        totalProvider.apply(e.getKey())
                ))
                .toList();
    }

    private void loadOrSeed() {
        Map<String, Integer> existing = readFromDisk();
        if (!existing.isEmpty()) {
            searchFrequency.putAll(existing);
            return;
        }
        seedFromCorpus();
        persistSafe();
    }

    private Map<String, Integer> readFromDisk() {
        Map<String, Integer> out = new HashMap<>();
        if (!Files.exists(storagePath)) {
            return out;
        }
        try (BufferedReader br = Files.newBufferedReader(storagePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("word")) {
                    continue;
                }
                String[] parts = trimmed.split(",", 2);
                String key = parts[0].trim().toLowerCase(Locale.ROOT);
                if (key.isEmpty()) {
                    continue;
                }
                int count = parseIntSafe(parts.length > 1 ? parts[1].trim() : "0");
                out.put(key, count);
            }
        } catch (IOException e) {
            System.err.println("Failed to load search frequencies: " + e.getMessage());
        }
        return out;
    }

    private void seedFromCorpus() {
        Map<String, Integer> counts = new HashMap<>();
        for (String token : loader.getTokens()) {
            if (token == null || token.isBlank()) {
                continue;
            }
            String key = token.toLowerCase(Locale.ROOT);
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }
        counts.entrySet().stream()
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.getValue(), a.getValue());
                    if (cmp != 0) return cmp;
                    return a.getKey().compareTo(b.getKey());
                })
                .limit(seedCount)
                .forEach(e -> searchFrequency.put(e.getKey(), e.getValue()));
    }

    private void persistSafe() {
        try {
            Path parent = storagePath.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (BufferedWriter bw = Files.newBufferedWriter(storagePath, StandardCharsets.UTF_8)) {
                bw.write("word,count");
                bw.newLine();
                searchFrequency.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(e -> writeLine(bw, e.getKey(), e.getValue()));
            }
        } catch (IOException | UncheckedIOException e) {
            System.err.println("Failed to persist search frequencies: " + e.getMessage());
        }
    }

    private void writeLine(BufferedWriter bw, String word, int count) {
        try {
            bw.write(word);
            bw.write(',');
            bw.write(Integer.toString(count));
            bw.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static int parseIntSafe(String v) {
        try {
            return Integer.parseInt(v);
        } catch (Exception e) {
            return 0;
        }
    }
}
