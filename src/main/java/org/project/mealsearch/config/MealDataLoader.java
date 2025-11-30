package org.project.mealsearch.config;


import jakarta.annotation.PostConstruct;
import org.project.mealsearch.model.Meal;
import org.project.mealsearch.model.Site;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MealDataLoader {

    private final String csvPath;

    private final List<Meal> meals = new ArrayList<>();
    private final Map<String, List<Meal>> mealsBySite = new LinkedHashMap<>();
    private final Map<String, String> corpusBySite = new LinkedHashMap<>();
    private final List<Site> sites = new ArrayList<>();
    private final List<String> tokens = new ArrayList<>();
    private String corpusLower = "";

    public MealDataLoader(@Value("${meals.csv-path}") String csvPath) {
        this.csvPath = csvPath;
    }

    @PostConstruct
    public void init() throws IOException {
        loadCsv();
    }

    public List<Meal> getMeals() {
        return Collections.unmodifiableList(meals);
    }

    public List<Site> getSites() {
        return Collections.unmodifiableList(sites);
    }

    public Map<String, List<Meal>> getMealsBySite() {
        return Collections.unmodifiableMap(mealsBySite);
    }

    public String getCorpusLower() {
        return corpusLower;
    }

    public Map<String, String> getCorpusBySite() {
        return Collections.unmodifiableMap(corpusBySite);
    }

    public List<String> getTokens() {
        return Collections.unmodifiableList(tokens);
    }

    private void loadCsv() throws IOException {
        Path path = Path.of(csvPath);
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                return;
            }

            String[] headers = parseCsvLine(headerLine);
            Map<String, Integer> headerIndex = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerIndex.put(headers[i].trim().toLowerCase(Locale.ROOT), i);
            }

            Integer idxId = headerIndex.get("id");
            Integer idxTitle = headerIndex.get("title");
            Integer idxDesc = headerIndex.get("description");
            Integer idxCategory = headerIndex.get("category");
            Integer idxSource = headerIndex.get("source_page");

            Map<String, String> sourceToSiteId = new LinkedHashMap<>();
            Map<String, StringBuilder> perSiteCorpus = new LinkedHashMap<>();
            AtomicInteger siteCounter = new AtomicInteger(1);
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = parseCsvLine(line);

                String sourcePage = safeGet(fields, idxSource, "unknown");
                String siteId = sourceToSiteId.computeIfAbsent(sourcePage, sp -> "site-" + siteCounter.getAndIncrement());

                long id = parseLongSafe(safeGet(fields, idxId, String.valueOf(meals.size() + 1)));
                String title = safeGet(fields, idxTitle, "");
                String desc = safeGet(fields, idxDesc, "");
                String category = safeGet(fields, idxCategory, "");

                Meal meal = new Meal(id, title, desc, category, sourcePage);
                meals.add(meal);
                mealsBySite.computeIfAbsent(siteId, k -> new ArrayList<>()).add(meal);

                StringBuilder sb = perSiteCorpus.computeIfAbsent(siteId, k -> new StringBuilder());
                sb.append(title).append(' ').append(desc).append(' ');

                collectTokens(title, tokens);
                collectTokens(desc, tokens);
            }

            for (Map.Entry<String, StringBuilder> entry : perSiteCorpus.entrySet()) {
                String text = entry.getValue().toString().toLowerCase(Locale.ROOT);
                corpusBySite.put(entry.getKey(), text);
                corpusLower += text;
            }

            for (Map.Entry<String, String> entry : sourceToSiteId.entrySet()) {
                String sourcePage = entry.getKey();
                String siteId = entry.getValue();
                int mealCount = mealsBySite.getOrDefault(siteId, List.of()).size();
                sites.add(new Site(siteId, sourcePage, deriveDisplayName(sourcePage), mealCount));
            }
        }
    }

    private static void collectTokens(String text, List<String> collector) {
        if (text == null) {
            return;
        }
        Matcher m = Pattern.compile("[a-zA-Z]+").matcher(text.toLowerCase(Locale.ROOT));
        while (m.find()) {
            collector.add(m.group());
        }
    }

    private static String deriveDisplayName(String sourcePage) {
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
            return host.substring(0, 1).toUpperCase(Locale.ROOT) + host.substring(1);
        } catch (Exception e) {
            return sourcePage;
        }
    }

    private static long parseLongSafe(String v) {
        try {
            return Long.parseLong(v.trim());
        } catch (Exception e) {
            return 0L;
        }
    }

    private static String safeGet(String[] fields, Integer idx, String def) {
        if (idx == null || idx < 0 || idx >= fields.length) {
            return def;
        }
        return fields[idx] == null ? def : fields[idx];
    }

    private static String[] parseCsvLine(String line) {
        if (line == null) {
            return new String[0];
        }
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }
}
