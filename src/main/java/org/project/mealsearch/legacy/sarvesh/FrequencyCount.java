package org.project.mealsearch.legacy.sarvesh;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class FrequencyCount {

    static class WordFreq {
        String word;
        int freq;
        WordFreq(String w, int f) { word = w; freq = f; }
    }

    private static final Pattern TOKEN_PATTERN = Pattern.compile("[a-z0-9]+");

    private static List<String> tokenize(String s) {
        s = s.toLowerCase(Locale.ROOT);
        List<String> tokens = new ArrayList<>();
        Matcher m = TOKEN_PATTERN.matcher(s);
        while (m.find()) tokens.add(m.group());
        return tokens;
    }

    public static Map<String, Integer> buildGlobalFrequency(List<Path> files) throws IOException {
        Map<String, Integer> freq = new HashMap<>();
        Charset[] tryCharsets = new Charset[] {
            StandardCharsets.UTF_8,
            Charset.forName("Windows-1252"),
            StandardCharsets.ISO_8859_1
        };

        for (Path p : files) {
            boolean success = false;
            for (Charset cs : tryCharsets) {
                try (BufferedReader br = Files.newBufferedReader(p, cs)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        List<String> tokens = tokenize(line);
                        for (String t : tokens) {
                            if (t.isEmpty()) continue;
                            freq.put(t, freq.getOrDefault(t, 0) + 1);
                        }
                    }
                    System.out.println("Read file: " + p + " using charset: " + cs.displayName());
                    success = true;
                    break;
                } catch (MalformedInputException mie) {
                    System.err.println("MalformedInput when reading " + p + " with charset " + cs.displayName() + ". Trying next charset...");
                } catch (IOException ioe) {
                    System.err.println("Failed to read " + p + ": " + ioe.getMessage());
                    break;
                }
            }
            if (!success) {
                System.err.println("Failed to read " + p + " with available charsets.");
            }
        }
        return freq;
    }

    private static WordFreq[] mapToArray(Map<String,Integer> map) {
        WordFreq[] arr = new WordFreq[map.size()];
        int i = 0;
        for (Map.Entry<String,Integer> e : map.entrySet()) {
            arr[i++] = new WordFreq(e.getKey(), e.getValue());
        }
        return arr;
    }

    public static void heapSortDesc(WordFreq[] arr) {
        int n = arr.length;
        for (int i = n/2 - 1; i >= 0; i--) heapify(arr, n, i);
        for (int i = n - 1; i >= 0; i--) {
            swap(arr, 0, i);
            heapify(arr, i, 0);
        }
        for (int i = 0, j = n-1; i < j; i++, j--) swap(arr, i, j);
    }

    private static void heapify(WordFreq[] arr, int heapSize, int root) {
        int largest = root;
        int left = 2 * root + 1;
        int right = 2 * root + 2;

        if (left < heapSize && compareWF(arr[left], arr[largest]) > 0) largest = left;
        if (right < heapSize && compareWF(arr[right], arr[largest]) > 0) largest = right;

        if (largest != root) {
            swap(arr, root, largest);
            heapify(arr, heapSize, largest);
        }
    }

    private static int compareWF(WordFreq a, WordFreq b) {
        if (a.freq != b.freq) return Integer.compare(a.freq, b.freq);
        return b.word.compareTo(a.word) * -1;
    }

    private static void swap(WordFreq[] arr, int i, int j) {
        WordFreq t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    public static List<WordFreq> heapSortTopN(Map<String,Integer> map, int N) {
        WordFreq[] arr = mapToArray(map);
        if (arr.length == 0) return Collections.emptyList();
        heapSortDesc(arr);
        List<WordFreq> out = new ArrayList<>(Math.min(N, arr.length));
        for (int i = 0; i < Math.min(N, arr.length); i++) out.add(arr[i]);
        return out;
    }

    public static List<WordFreq> minHeapTopN(Map<String,Integer> map, int N) {
        if (N <= 0) return Collections.emptyList();
        PriorityQueue<WordFreq> pq = new PriorityQueue<>(N, (a,b) -> {
            if (a.freq != b.freq) return Integer.compare(a.freq, b.freq);
            return b.word.compareTo(a.word);
        });

        for (Map.Entry<String,Integer> e : map.entrySet()) {
            WordFreq wf = new WordFreq(e.getKey(), e.getValue());
            if (pq.size() < N) pq.offer(wf);
            else {
                WordFreq top = pq.peek();
                if (wf.freq > top.freq || (wf.freq == top.freq && wf.word.compareTo(top.word) < 0)) {
                    pq.poll();
                    pq.offer(wf);
                }
            }
        }

        List<WordFreq> out = new ArrayList<>(pq);
        out.sort((a,b) -> {
            if (b.freq != a.freq) return Integer.compare(b.freq, a.freq);
            return a.word.compareTo(b.word);
        });
        return out;
    }

    public static void main(String[] args) throws Exception {
        List<Path> files = new ArrayList<>();
        if (args.length == 0) {
            Path sample = Paths.get("sample_vocab_task3.txt");
            if (!Files.exists(sample)) {
                Files.write(sample, Arrays.asList(
                    "This is a sample sample line. Sample words: apple, banana, apple!",
                    "Another line: apple banana orange apple. Testing, one two three."
                ));
            }
            files.add(sample);
            System.out.println("No input files provided. Using sample: " + sample.toAbsolutePath());
        } else {
            for (String s : args) files.add(Paths.get(s));
        }

        Map<String,Integer> freq = buildGlobalFrequency(files);
        System.out.println("Unique words counted: " + freq.size());

        int N = 10;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter top N to display (default 10): ");
        String nline = br.readLine();
        try { if (nline != null && !nline.isBlank()) N = Integer.parseInt(nline.trim()); } catch (Exception e) {}

        System.out.println("\nTop " + N + " words using heap-sort (full sort):");
        List<WordFreq> top = heapSortTopN(freq, N);
        for (WordFreq w : top) System.out.printf("%s : %d%n", w.word, w.freq);

        System.out.println("\nTop " + N + " words using min-heap (streaming):");
        List<WordFreq> top2 = minHeapTopN(freq, N);
        for (WordFreq w : top2) System.out.printf("%s : %d%n", w.word, w.freq);

        System.out.println("\nDone.");
    }
}
