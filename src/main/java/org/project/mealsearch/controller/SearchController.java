package org.project.mealsearch.controller;

import org.project.mealsearch.model.SearchResponse;
import org.project.mealsearch.model.TopSearch;
import org.project.mealsearch.service.SearchService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/suggest")
    public SearchResponse suggest(@RequestParam(name = "query", defaultValue = "") String query) {
        return searchService.suggest(query);
    }

    @GetMapping("/top")
    public List<TopSearch> top() {
        return searchService.top();
    }
}
