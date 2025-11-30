package org.project.mealsearch.legacy.hemit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String CSV_NAME_hemit = "comprehensive_scraped_data_20250927_131708.csv";

    public static void main(String[] args) throws Exception {
        java.util.Scanner scanner_hemit = new java.util.Scanner(System.in, "UTF-8");
        String path_hemit = CSV_NAME_hemit;
        int traceSampleSize_hemit = 2000;
        int maxTraceRecords_hemit = 20;

        printBanner_hemit();
        System.out.println("Using data file: " + path_hemit);
        System.out.println();

        while (true) {
            System.out.print("Enter search word (or 'exit' to quit): ");
            String word_hemit = scanner_hemit.nextLine();
            if (word_hemit == null) break;
            word_hemit = word_hemit.trim();
            if (word_hemit.isEmpty()) continue;
            String lw_hemit = word_hemit.toLowerCase();
            if (lw_hemit.equals("exit") || lw_hemit.equals("quit")) break;

            performSearchAndShowTrace_hemit(word_hemit, path_hemit, traceSampleSize_hemit, maxTraceRecords_hemit);

            System.out.print("Search again? (Y/n): ");
            String again_hemit = scanner_hemit.nextLine();
            if (again_hemit != null) {
                again_hemit = again_hemit.trim().toLowerCase();
                if (again_hemit.equals("n") || again_hemit.equals("no")) break;
            }
        }

        System.out.println("\nGoodbye.");
        scanner_hemit.close();
    }

    private static void performSearchAndShowTrace_hemit(String word_hemit, String path_hemit, int traceSampleSize_hemit, int maxTraceRecords_hemit) {
        try {
            File f_hemit = new File(path_hemit);
            if (!f_hemit.exists()) {
                System.err.println("Data file not found: " + path_hemit);
                return;
            }

            List<File> files_hemit = new ArrayList<>();
            if (f_hemit.isDirectory()) collectFilesRecursively_hemit(f_hemit, files_hemit);
            else files_hemit.add(f_hemit);

            BoyerMoore bm_hemit = new BoyerMoore(word_hemit);
            int total_hemit = 0;
            int fileIndex_hemit = 0;

            System.out.println();
            System.out.println("Searching " + files_hemit.size() + " file(s)...");

            File maxFile_hemit = null;
            int maxCount_hemit = -1;

            for (File file_hemit : files_hemit) {
                fileIndex_hemit++;
                String content_hemit = readFileUtf8_hemit(file_hemit);
                int count_hemit = bm_hemit.countOccurrences_hemit(content_hemit, false);
                System.out.println("[" + fileIndex_hemit + "/" + files_hemit.size() + "] FOund : " + count_hemit + " occurrences");
                total_hemit += count_hemit;
                if (count_hemit > maxCount_hemit) { maxCount_hemit = count_hemit; maxFile_hemit = file_hemit; }
            }

            System.out.println("\n---------------RESULT-----------------");
            System.out.println("Total occurrences of '" + word_hemit + "': " + total_hemit);

            if (maxFile_hemit == null && files_hemit.size() > 0) maxFile_hemit = files_hemit.get(0);
            if (maxFile_hemit != null) {
                System.out.println();
                System.out.println("Showing first " + maxTraceRecords_hemit + " Boyer-Moore steps for file: ");
                String content_hemit = readFileUtf8_hemit(maxFile_hemit);
                String sample_hemit = content_hemit.substring(0, Math.min(traceSampleSize_hemit, content_hemit.length()));
                List<String> trace_hemit = bm_hemit.trace_hemit(sample_hemit, false, maxTraceRecords_hemit);
                int step_hemit = 0;
                for (String s_hemit : trace_hemit) {
                    step_hemit++;
                    System.out.println("[step " + step_hemit + "]\n" + s_hemit);
                }
                System.out.println("--- End of trace (" + Math.min(trace_hemit.size(), maxTraceRecords_hemit) + " steps) ---\n");
            }

        } catch (Exception e_hemit) {
            System.err.println("Error during search: " + e_hemit.getMessage());
            e_hemit.printStackTrace(System.err);
        }
    }

    private static void printBanner_hemit() {
        String line_hemit = "============================================================";
        System.out.println(line_hemit);
        System.out.println(" Assignment 3 - Task 3 : Frequency Count using Boyer-Moore");
        System.out.println(line_hemit + "\n");
    }

    private static void collectFilesRecursively_hemit(File dir_hemit, List<File> out_hemit) {
        File[] children_hemit = dir_hemit.listFiles();
        if (children_hemit == null) return;
        for (File c_hemit : children_hemit) {
            if (c_hemit.isDirectory()) collectFilesRecursively_hemit(c_hemit, out_hemit);
            else if (c_hemit.isFile()) out_hemit.add(c_hemit);
        }
    }

    private static String readFileUtf8_hemit(File file_hemit) {
        StringBuilder sb_hemit = new StringBuilder();
        try (BufferedReader r_hemit = new BufferedReader(new InputStreamReader(new FileInputStream(file_hemit), StandardCharsets.UTF_8))) {
            String line_hemit;
            while ((line_hemit = r_hemit.readLine()) != null) {
                sb_hemit.append(line_hemit).append('\n');
            }
        } catch (Exception e_hemit) {
            System.err.println("Failed to read " + file_hemit.getAbsolutePath() + ": " + e_hemit.getMessage());
        }
        return sb_hemit.toString();
    }
}
