package org.project.mealsearch.service;

import org.project.mealsearch.config.MealDataLoader;
import org.project.mealsearch.model.Meal;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MealService {
    private final MealDataLoader loader;

    public MealService(MealDataLoader loader) {
        this.loader = loader;
    }

    public List<Meal> mealsForSite(String siteId) {
        return loader.getMealsBySite().getOrDefault(siteId, Collections.emptyList());
    }
}
