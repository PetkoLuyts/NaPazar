package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.service.ScrapeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class ScrapingScheduler {

    private final ScrapeService scrapeService;

    @Scheduled(cron = "0 0 3 ? * MON")
    public void scrapeBillaWeekly() {
        log.info("Starting Billa scraping...");
        scrapeService.scrapeBillaData();
        log.info("Billa scraping finished.");
    }

    @Scheduled(cron = "0 0 3 ? * TUE")
    public void scrapeLidlWeekly() {
        log.info("Starting Lidl scraping...");
        scrapeService.scrapeLidlData();
        log.info("Lidl scraping finished.");
    }

    @Scheduled(cron = "0 0 3 ? * WED")
    public void scrapeKauflandWeekly() {
        log.info("Starting Kaufland scraping...");
        scrapeService.scrapeKauflandData();
        log.info("Kaufland scraping finished.");
    }
}

