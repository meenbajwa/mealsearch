package org.project.mealsearch.controller;

import org.project.mealsearch.model.Meal;
import org.project.mealsearch.model.Site;
import org.project.mealsearch.service.MealService;
import org.project.mealsearch.service.SiteService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@CrossOrigin
public class SiteController {

    private final SiteService siteService;
    private final MealService mealService;

    public SiteController(SiteService siteService, MealService mealService) {
        this.siteService = siteService;
        this.mealService = mealService;
    }

    @GetMapping
    public List<Site> listSites() {
        return siteService.listSites();
    }

    @GetMapping("/{siteId}/meals")
    public List<Meal> meals(@PathVariable String siteId) {
        return mealService.mealsForSite(siteId);
    }
}
