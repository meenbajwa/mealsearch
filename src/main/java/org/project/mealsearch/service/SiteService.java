package org.project.mealsearch.service;

import org.project.mealsearch.config.MealDataLoader;
import org.project.mealsearch.model.Site;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteService {
    private final MealDataLoader loader;

    public SiteService(MealDataLoader loader) {
        this.loader = loader;
    }

    public List<Site> listSites() {
        return loader.getSites();
    }
}
