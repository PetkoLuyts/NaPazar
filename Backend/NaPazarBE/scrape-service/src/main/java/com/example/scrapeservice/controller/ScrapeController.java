package com.example.scrapeservice.controller;

import com.example.scrapeservice.service.ScrapeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RequestMapping("/scrape")
@RestController
public class ScrapeController {
    private final ScrapeService scrapeService;

    @PostMapping("/billa")
    public void scrapeBillaData() {
        log.info("Starting scrape for Billa...");
        try {
            scrapeService.scrapeBillaData();
            log.info("Successfully finished scrape for Billa.");
        } catch (Exception e) {
            log.error("Failed to scrape Billa data", e);
            throw e;
        }
    }

    @PostMapping("/lidl")
    public void scrapeLidlData() {
        log.info("Starting scrape for Lidl...");
        try {
            scrapeService.scrapeLidlData();
            log.info("Successfully finished scrape for Lidl.");
        } catch (Exception e) {
            log.error("Failed to scrape Lidl data", e);
            throw e;
        }
    }

    @PostMapping("/kaufland")
    public void scrapeKauflandData() {
        log.info("Starting scrape for Kaufland...");
        try {
            scrapeService.scrapeKauflandData();
            log.info("Successfully finished scrape for Kaufland.");
        } catch (Exception e) {
            log.error("Failed to scrape Kaufland data", e);
            throw e;
        }
    }
}
