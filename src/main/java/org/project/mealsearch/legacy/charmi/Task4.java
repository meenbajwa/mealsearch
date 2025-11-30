package org.project.mealsearch.legacy.charmi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Task4 {

    public static String loadCorpusFromCsv(String csvPath) throws IOException {
        StringBuilder corpus = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String headerLine = br.readLine();
            if (headerLine == null) return "";
            String[] headers = parseCsvLine(headerLine);
            Map<String, Integer> headerIndex = new HashMap<>();
            for (int i = 0; i < headers.length; i++) headerIndex.put(headers[i].trim().toLowerCase(), i);

            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = parseCsvLine(line);
                String title = getField(fields, headerIndex.get("title"));
                String desc = getField(fields, headerIndex.get("description"));
                if (title != null && !title.isEmpty()) {
                    corpus.append(title).append("\n");
                }
                if (desc != null && !desc.isEmpty()) {
                    corpus.append(desc).append("\n");
                }
                corpus.append("\n");
            }
        }
        return corpus.toString();
    }

    private static String getField(String[] fields, Integer idx) {
        if (idx == null) return "";
        if (idx < 0 || idx >= fields.length) return "";
        return fields[idx] == null ? "" : fields[idx];
    }

    private static String[] parseCsvLine(String line) {
        if (line == null) return new String[0];
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

    public static int[] buildLps(String pattern) {
        int n = pattern.length();
        int[] lps = new int[n];
        int len = 0;
        int i = 1;
        while (i < n) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    public static int kmpCountOccurrences(String text, String pattern) {
        if (pattern == null || pattern.length() == 0) return 0;
        int[] lps = buildLps(pattern);
        int i = 0, j = 0, count = 0;
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++; j++;
                if (j == pattern.length()) {
                    count++;
                    j = lps[j - 1];
                }
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return count;
    }

    public static class SearchFrequency {
        private final Map<String, Integer> freq = new HashMap<>();

        public int search(String text, String pattern) {
            if (pattern == null) pattern = "";
            String key = pattern.toLowerCase();
            int occurrences = kmpCountOccurrences(text, pattern);
            freq.put(key, freq.getOrDefault(key, 0) + 1);
            return occurrences;
        }

        public int getFrequency(String pattern) {
            if (pattern == null) return 0;
            return freq.getOrDefault(pattern.toLowerCase(), 0);
        }

        public Map<String, Integer> getAll() {
            return Collections.unmodifiableMap(freq);
        }
    }

    public static void main(String[] args) {
        // CLI retained from teammate; left intentionally unchanged.
    }
}
