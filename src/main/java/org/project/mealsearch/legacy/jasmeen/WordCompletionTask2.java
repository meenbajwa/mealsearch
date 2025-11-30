package org.project.mealsearch.legacy.jasmeen;

// file WordCompletionTask2.java
// COMP8547 assignment 2 task 2 word completion using avl and min heap
// CSV - comprehensive_scraped_data_20250927_131708_UTF8_clean.csv

import java.io.*;
import java.util.*;

public class WordCompletionTask2 {

    public static class NodeUniq {
        String w;
        int c;
        int h;
        NodeUniq L, R;

        NodeUniq(String ww, int cc) {
            w = ww;
            c = cc;
            h = 1;
        }
    }

    public static class AvlBag {
        private NodeUniq root;

        public void insertOrBump(String word) {
            if (word == null || word.isEmpty()) return;
            root = insertOrBump(root, word);
        }

        private NodeUniq insertOrBump(NodeUniq t, String word) {
            if (t == null) return new NodeUniq(word, 1);
            int cmp = word.compareTo(t.w);
            if (cmp < 0) t.L = insertOrBump(t.L, word);
            else if (cmp > 0) t.R = insertOrBump(t.R, word);
            else { t.c++; return t; }

            t.h = 1 + Math.max(h(t.L), h(t.R));
            return rebalance(t);
        }

        private int h(NodeUniq t) { return t == null ? 0 : t.h; }

        private int bf(NodeUniq t) { return t == null ? 0 : h(t.L) - h(t.R); }

        private NodeUniq rotRight(NodeUniq y) {
            NodeUniq x = y.L;
            NodeUniq t2 = x.R;
            x.R = y;
            y.L = t2;
            y.h = 1 + Math.max(h(y.L), h(y.R));
            x.h = 1 + Math.max(h(x.L), h(x.R));
            return x;
        }

        private NodeUniq rotLeft(NodeUniq x) {
            NodeUniq y = x.R;
            NodeUniq t2 = y.L;
            y.L = x;
            x.R = t2;
            x.h = 1 + Math.max(h(x.L), h(x.R));
            y.h = 1 + Math.max(h(y.L), h(y.R));
            return y;
        }

        private NodeUniq rebalance(NodeUniq t) {
            int b = bf(t);
            if (b > 1) {
                if (bf(t.L) < 0) t.L = rotLeft(t.L);
                return rotRight(t);
            }
            if (b < -1) {
                if (bf(t.R) > 0) t.R = rotRight(t.R);
                return rotLeft(t);
            }
            return t;
        }

        public List<PairWF> gatherPrefixFromSplit(String prefix) {
            List<PairWF> out = new ArrayList<>();
            if (root == null || prefix == null || prefix.isEmpty()) return out;
            String low = prefix;
            String high = prefix + Character.MAX_VALUE;
            NodeUniq split = findSplitSpot(root, low, high);
            if (split == null) return out;
            dfsBounded(split, low, high, out);
            List<PairWF> filtered = new ArrayList<>();
            for (PairWF p : out) if (p.w.startsWith(prefix)) filtered.add(p);
            return filtered;
        }

        private NodeUniq findSplitSpot(NodeUniq t, String low, String high) {
            NodeUniq cur = t;
            while (cur != null) {
                int a = cur.w.compareTo(low);
                int b = cur.w.compareTo(high);
                if (a < 0) cur = cur.R;
                else if (b >= 0) cur = cur.L;
                else break;
            }
            return cur;
        }

        private void dfsBounded(NodeUniq t, String low, String high, List<PairWF> out) {
            if (t == null) return;
            if (t.w.compareTo(low) >= 0) dfsBounded(t.L, low, high, out);
            if (t.w.compareTo(low) >= 0 && t.w.compareTo(high) < 0) out.add(new PairWF(t.w, t.c));
            if (t.w.compareTo(high) < 0) dfsBounded(t.R, low, high, out);
        }

        public List<PairWF> toListABC() {
            List<PairWF> out = new ArrayList<>();
            inOrder(root, out);
            return out;
        }

        private void inOrder(NodeUniq t, List<PairWF> out) {
            if (t == null) return;
            inOrder(t.L, out);
            out.add(new PairWF(t.w, t.c));
            inOrder(t.R, out);
        }
    }

    public static class PairWF {
        public String w;
        public int f;
        public PairWF(String ww, int ff) { w = ww; f = ff; }
    }

    static class CsvTiny implements Closeable {
        BufferedReader br;
        CsvTiny(String path) throws IOException {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
        }
        @Override public void close() throws IOException { if (br != null) br.close(); }

        static List<String> split(String line) {
            List<String> out = new ArrayList<>();
            if (line == null) return out;
            StringBuilder sb = new StringBuilder();
            boolean inQ = false;
            for (int i = 0; i < line.length(); i++) {
                char ch = line.charAt(i);
                if (ch == '\"') {
                    if (inQ && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                        sb.append('\"');
                        i++;
                    } else {
                        inQ = !inQ;
                    }
                } else if (ch == ',' && !inQ) {
                    out.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(ch);
                }
            }
            out.add(sb.toString());
            return out;
        }
    }

    static List<String> tokenizeToWordsSimple(String text) {
        List<String> out = new ArrayList<>();
        if (text == null) return out;
        String low = text.toLowerCase(Locale.ROOT);
        String[] parts = low.split("[^a-z]+");
        for (String p : parts) if (p.length() > 0) out.add(p);
        return out;
    }

    public static class BuildStats {
        public AvlBag bag;
        public int rows;
        public long tokens;
        public List<String> cols;
        BuildStats(AvlBag b, int r, long t, List<String> c) { bag = b; rows = r; tokens = t; cols = c; }
    }

    public static BuildStats buildFromCsvLoose(String csvPath) throws IOException {
        AvlBag bag = new AvlBag();
        long tok = 0L;
        int row = 0;
        List<String> used = new ArrayList<>();

        String[] pref = { "title", "description" };
        String[] more = { "plan name", "features", "body", "content", "text", "summary" };

        try (CsvTiny r = new CsvTiny(csvPath)) {
            String headLine = r.br.readLine();
            if (headLine == null) throw new IOException("csv is empty");
            List<String> head = CsvTiny.split(headLine);
            Map<String,Integer> idx = new HashMap<>();
            for (int i = 0; i < head.size(); i++) idx.put(head.get(i).trim().toLowerCase(Locale.ROOT), i);

            List<Integer> useIdx = new ArrayList<>();
            for (String h : pref) { Integer k = idx.get(h); if (k != null) { useIdx.add(k); used.add(h); } }
            for (String h : more) { Integer k = idx.get(h); if (k != null) { useIdx.add(k); used.add(h); } }

            if (useIdx.isEmpty()) throw new IOException("no expected text columns");

            String line;
            while ((line = r.br.readLine()) != null) {
                List<String> f = CsvTiny.split(line);
                StringBuilder buf = new StringBuilder();
                for (Integer i : useIdx) if (i < f.size()) buf.append(f.get(i)).append(' ');
                List<String> words = tokenizeToWordsSimple(buf.toString());
                tok += words.size();
                for (String w : words) bag.insertOrBump(w);
                row++;
            }
        }
        return new BuildStats(bag, row, tok, used);
    }

    public static List<PairWF> topKByFreqHeap(List<PairWF> list, int k) {
        if (k <= 0) return Collections.emptyList();
        PriorityQueue<PairWF> pq = new PriorityQueue<>((a,b) -> {
            if (a.f != b.f) return Integer.compare(a.f, b.f);
            return a.w.compareTo(b.w);
        });
        for (PairWF p : list) {
            if (pq.size() < k) pq.offer(p);
            else {
                PairWF top = pq.peek();
                if (top != null && (p.f > top.f || (p.f == top.f && p.w.compareTo(top.w) < 0))) {
                    pq.poll();
                    pq.offer(p);
                }
            }
        }
        List<PairWF> out = new ArrayList<>(pq);
        out.sort((a,b) -> a.f == b.f ? a.w.compareTo(b.w) : Integer.compare(b.f, a.f));
        return out;
    }

    public static List<PairWF> topNWhole(AvlBag bag, int n) {
        return topKByFreqHeap(bag.toListABC(), n);
    }

    public static void main(String[] args) {
        // original CLI retained
    }

    private static int parseIntSafe(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
