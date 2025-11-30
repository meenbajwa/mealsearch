package org.project.mealsearch.model;

public record SpellCheckResult(boolean corrected, String original, String suggestion) {}
