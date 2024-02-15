package com.example.scrapeservice.controller;

import com.example.scrapeservice.service.ScrapeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/scrape")
@RestController
public class ScrapeController {
    private final ScrapeService scrapeService;

    @PostMapping("")
    public void scrapeData() {
        scrapeService.scrapeData();
    }
}
