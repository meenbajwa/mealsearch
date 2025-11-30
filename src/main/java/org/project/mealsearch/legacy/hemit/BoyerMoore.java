package org.project.mealsearch.legacy.hemit;

import java.util.Arrays;

public class BoyerMoore {
    private final String pattern_hemit;
    private final int[] badCharShift_hemit;

    public BoyerMoore(String pattern_hemit) {
        if (pattern_hemit == null) throw new IllegalArgumentException("pattern must not be null");
        this.pattern_hemit = pattern_hemit;
        this.badCharShift_hemit = buildBadCharShift_hemit(pattern_hemit);
    }

    private int[] buildBadCharShift_hemit(String pat_hemit) {
        int m_hemit = pat_hemit.length();
        int[] shift_hemit = new int[65536];
        Arrays.fill(shift_hemit, m_hemit);
        for (int i_hemit = 0; i_hemit < m_hemit - 1; i_hemit++) {
            char c_hemit = Character.toLowerCase(pat_hemit.charAt(i_hemit));
            shift_hemit[c_hemit] = m_hemit - 1 - i_hemit;
        }
        return shift_hemit;
    }

    public int countOccurrences_hemit(String text_hemit, boolean wholeWord_hemit) {
        if (text_hemit == null) return 0;
        int n_hemit = text_hemit.length();
        int m_hemit = pattern_hemit.length();
        if (m_hemit == 0) return 0;

        int count_hemit = 0;
        int i_hemit = 0;
        while (i_hemit <= n_hemit - m_hemit) {
            int j_hemit = m_hemit - 1;
            while (j_hemit >= 0 && charEqualsIgnoreCase_hemit(pattern_hemit.charAt(j_hemit), text_hemit.charAt(i_hemit + j_hemit))) {
                j_hemit--;
            }
            if (j_hemit < 0) {
                if (!wholeWord_hemit || isWholeWordAt_hemit(text_hemit, i_hemit, m_hemit)) {
                    count_hemit++;
                }
                int nextIndex_hemit = i_hemit + m_hemit;
                if (nextIndex_hemit >= n_hemit) break;
                char nextChar_hemit = Character.toLowerCase(text_hemit.charAt(nextIndex_hemit));
                int shift_hemit = (nextChar_hemit < badCharShift_hemit.length) ? badCharShift_hemit[nextChar_hemit] : m_hemit;
                i_hemit += Math.max(1, shift_hemit);
            } else {
                char bad_hemit = Character.toLowerCase(text_hemit.charAt(i_hemit + j_hemit));
                int shift_hemit = (bad_hemit < badCharShift_hemit.length) ? badCharShift_hemit[bad_hemit] : m_hemit;
                i_hemit += Math.max(1, shift_hemit - (m_hemit - 1 - j_hemit));
            }
        }

        return count_hemit;
    }

    private boolean isWholeWordAt_hemit(String text_hemit, int start_hemit, int len_hemit) {
        int before_hemit = start_hemit - 1;
        int after_hemit = start_hemit + len_hemit;
        if (before_hemit >= 0) {
            char c_hemit = text_hemit.charAt(before_hemit);
            if (Character.isLetterOrDigit(c_hemit) || c_hemit == '_') return false;
        }
        if (after_hemit < text_hemit.length()) {
            char c_hemit = text_hemit.charAt(after_hemit);
            if (Character.isLetterOrDigit(c_hemit) || c_hemit == '_') return false;
        }
        return true;
    }

    public java.util.List<String> trace_hemit(String text_hemit, boolean wholeWord_hemit, int maxRecords_hemit) {
        java.util.List<String> out_hemit = new java.util.ArrayList<>();
        if (text_hemit == null) return out_hemit;
        int n_hemit = text_hemit.length();
        int m_hemit = pattern_hemit.length();
        if (m_hemit == 0) return out_hemit;

        int i_hemit = 0;
        while (i_hemit <= n_hemit - m_hemit) {
            if (out_hemit.size() >= maxRecords_hemit) break;
            StringBuilder line_hemit = new StringBuilder();
            line_hemit.append(String.format("Alignment i=%d:%n", i_hemit));

            int j_hemit = m_hemit - 1;
            while (j_hemit >= 0) {
                char pc_hemit = pattern_hemit.charAt(j_hemit);
                char tc_hemit = text_hemit.charAt(i_hemit + j_hemit);
                boolean eq_hemit = charEqualsIgnoreCase_hemit(pc_hemit, tc_hemit);
                line_hemit.append(String.format("  compare pattern[%d]='%s' to text[%d]='%s' -> %s%n", j_hemit, pc_hemit, i_hemit + j_hemit, tc_hemit, eq_hemit ? "EQUAL" : "DIFFER"));
                if (!eq_hemit) { break; }
                j_hemit--;
            }

            if (j_hemit < 0) {
                line_hemit.append("  --> FULL MATCH at i=" + i_hemit + "\n");
                if (wholeWord_hemit) {
                    boolean ww_hemit = isWholeWordAt_hemit(text_hemit, i_hemit, m_hemit);
                    line_hemit.append("  whole-word check: " + (ww_hemit ? "PASS" : "FAIL") + "\n");
                }
                int nextIndex_hemit = i_hemit + m_hemit;
                if (nextIndex_hemit >= n_hemit) {
                    line_hemit.append("  shift: end of text reached\n");
                    out_hemit.add(line_hemit.toString());
                    break;
                }
                char nextChar_hemit = Character.toLowerCase(text_hemit.charAt(nextIndex_hemit));
                int shift_hemit = (nextChar_hemit < badCharShift_hemit.length) ? badCharShift_hemit[nextChar_hemit] : m_hemit;
                line_hemit.append(String.format("  nextChar='%s' -> shift=%d%n", nextChar_hemit, Math.max(1, shift_hemit)));
                out_hemit.add(line_hemit.toString());
                i_hemit += Math.max(1, shift_hemit);
            } else {
                char bad_hemit = Character.toLowerCase(text_hemit.charAt(i_hemit + j_hemit));
                int shift_hemit = (bad_hemit < badCharShift_hemit.length) ? badCharShift_hemit[bad_hemit] : m_hemit;
                line_hemit.append(String.format("  -> MISMATCH at j=%d (text char '%s'), computed shift=%d (apply i += %d)%n", j_hemit, bad_hemit, shift_hemit, Math.max(1, shift_hemit - (m_hemit - 1 - j_hemit))));
                out_hemit.add(line_hemit.toString());
                i_hemit += Math.max(1, shift_hemit - (m_hemit - 1 - j_hemit));
            }
        }

        return out_hemit;
    }

    private boolean charEqualsIgnoreCase_hemit(char a_hemit, char b_hemit) {
        return Character.toLowerCase(a_hemit) == Character.toLowerCase(b_hemit);
    }
}
