package org.project.mealsearch.controller;

import org.project.mealsearch.model.CrawlResult;
import org.project.mealsearch.service.CrawlerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crawl")
@CrossOrigin
public class CrawlerController {

    private final CrawlerService crawlerService;

    public CrawlerController(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    // Simple endpoint: single page, required url query param, no other knobs.
    @GetMapping("/page")
    @ResponseStatus(HttpStatus.OK)
    public CrawlResult crawlPage(@RequestParam("url") String url) {
        return crawlerService.crawlPage(url);
    }
}
