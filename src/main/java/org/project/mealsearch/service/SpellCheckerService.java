package org.project.mealsearch.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SpellCheckerService {

    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        boolean end;
    }

    private final Node root = new Node();

    public void loadDictionary(Collection<String> words) {
        for (String w : words) {
            insert(w.toLowerCase(Locale.ROOT));
        }
    }

    private void insert(String word) {
        Node cur = root;
        for (char c : word.toCharArray()) {
            cur = cur.children.computeIfAbsent(c, k -> new Node());
        }
        cur.end = true;
    }

    private boolean exists(String word) {
        Node cur = root;
        for (char c : word.toCharArray()) {
            cur = cur.children.get(c);
            if (cur == null) {
                return false;
            }
        }
        return cur.end;
    }

    public String suggestClosestWord(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        String low = input.toLowerCase(Locale.ROOT);
        if (exists(low)) {
            return low;
        }

        String best = null;
        int bestDist = Integer.MAX_VALUE;

        Queue<NodeState> q = new ArrayDeque<>();
        q.offer(new NodeState(root, 0, low, ""));

        while (!q.isEmpty()) {
            NodeState s = q.poll();
            if (s.pos == s.target.length()) {
                if (s.node.end) {
                    int dist = s.edits + Math.abs(s.target.length() - s.built.length());
                    if (dist < bestDist) {
                        bestDist = dist;
                        best = s.built;
                    }
                }
                continue;
            }
            char ch = s.target.charAt(s.pos);
            Node next = s.node.children.get(ch);
            if (next != null) {
                q.offer(new NodeState(next, s.pos + 1, s.target, s.built + ch, s.edits));
            }
            for (var entry : s.node.children.entrySet()) {
                char alt = entry.getKey();
                if (alt == ch) {
                    continue;
                }
                if (s.edits + 1 < bestDist) {
                    q.offer(new NodeState(entry.getValue(), s.pos + 1, s.target, s.built + alt, s.edits + 1));
                }
            }
            if (s.edits + 1 < bestDist) {
                q.offer(new NodeState(s.node, s.pos + 1, s.target, s.built, s.edits + 1));
            }
            for (var entry : s.node.children.entrySet()) {
                if (s.edits + 1 < bestDist) {
                    q.offer(new NodeState(entry.getValue(), s.pos, s.target, s.built + entry.getKey(), s.edits + 1));
                }
            }
        }
        return best == null ? low : best;
    }

    private static class NodeState {
        Node node;
        int pos;
        String target;
        String built;
        int edits;
        NodeState(Node n, int p, String t, String b, int e) {
            node = n;
            pos = p;
            target = t;
            built = b;
            edits = e;
        }
        NodeState(Node n, int p, String t, String b) {
            this(n, p, t, b, 0);
        }
    }
}
