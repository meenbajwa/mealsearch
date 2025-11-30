package org.project.mealsearch.legacy.jasmeen;

import java.util.List;

public class WordCompletionAdapter {

    private final WordCompletionTask2.AvlBag bag;

    public WordCompletionAdapter(WordCompletionTask2.AvlBag bag) {
        this.bag = bag;
    }

    public List<WordCompletionTask2.PairWF> complete(String prefix, int limit) {
        List<WordCompletionTask2.PairWF> matches = bag.gatherPrefixFromSplit(prefix);
        return WordCompletionTask2.topKByFreqHeap(matches, limit);
    }
}
