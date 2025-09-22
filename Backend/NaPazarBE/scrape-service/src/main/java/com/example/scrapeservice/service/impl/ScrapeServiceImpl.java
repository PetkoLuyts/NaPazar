package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.constants.Constants;
import com.example.scrapeservice.dto.PromotionInterval;
import com.example.scrapeservice.dto.PromotionPeriod;
import com.example.scrapeservice.model.Product;
import com.example.scrapeservice.model.Promotion;
import com.example.scrapeservice.model.Store;
import com.example.scrapeservice.service.ProductService;
import com.example.scrapeservice.service.PromotionService;
import com.example.scrapeservice.service.ScrapeService;
import com.example.scrapeservice.service.StoreService;
import com.example.scrapeservice.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScrapeServiceImpl implements ScrapeService {
    @Value("${billa.url}")
    private String billaCategoryUrl;
    @Value("${lidl.url}")
    private String lidlUrl;
    @Value("${kaufland.url}")
    private String kauflandUrl;
    private final StoreService storeService;
    private final ProductService productService;
    private final PromotionService promotionService;

    @Override
    public void scrapeBillaData() {
        scrapeBillaPromotions();
    }

    @Override
    public void scrapeLidlData() {
        List<String> lidlCategories = getLidlCategoriesUrls();

        for(String lidlCategory : lidlCategories) {
            getLidlCategoryProductsUrl(lidlCategory);
        }
    }

    @Override
    public void scrapeKauflandData() {
        try {
            Document document = Jsoup.connect("https://www.kaufland.bg/aktualni-predlozheniya/oferti.html").get();
            Element script = document.selectFirst("script:containsData(window.SSR)");
            List<Product> products = new ArrayList<>();
            Store kauflandStore = storeService.getStoreById(Constants.KAUFLAND_ID);

            if (script == null) {
                log.error("No SSR script found on the page.");
                return;
            }

            String scriptContent = script.html();
            int firstBrace = scriptContent.indexOf('{');
            int lastBrace = scriptContent.lastIndexOf('}');
            if (firstBrace == -1 || lastBrace == -1) {
                log.error("No JSON braces found in SSR script");
                return;
            }

            String jsonString = scriptContent.substring(firstBrace, lastBrace + 1).trim();
            String textAfterEqual = null;
            String[] parts = jsonString.split("=", 2);
            if (parts.length > 1) {
                textAfterEqual = parts[1].trim();
            }
            JSONObject root = new JSONObject(textAfterEqual);
            JSONObject props = root.getJSONObject("props");
            JSONObject offerData = props.getJSONObject("offerData");

            JSONArray weekDates = props.getJSONObject("weekData").getJSONArray("currentWeekDates");
            String promoSalesFrom = weekDates.getString(0);
            String promoSalesTo = weekDates.getString(weekDates.length() - 1);

            JSONArray cycles = offerData.getJSONArray("cycles");
            for (int i = 0; i < cycles.length(); i++) {
                JSONObject cycle = cycles.getJSONObject(i);
                JSONArray categories = cycle.getJSONArray("categories");

                for (int j = 0; j < categories.length(); j++) {
                    JSONObject category = categories.getJSONObject(j);
                    String categoryName = category.getString("displayName");
                    String dateFrom = category.getString("dateFrom");
                    String dateTo = category.getString("dateTo");

                    JSONArray offers = category.getJSONArray("offers");
                    for (int k = 0; k < offers.length(); k++) {
                        JSONObject offer = offers.getJSONObject(k);
                        String title = offer.optString("title");
                        String subtitle = offer.optString("subtitle");
                        String oldPrice = offer.optString("formattedOldPrice");
                        String newPrice = offer.optString("formattedPrice");

                        Product product = Product.builder()
                                .title(title)
                                .discountPhrase(subtitle)
                                .newPrice(parsePrice(newPrice))
                                .oldPrice(parsePrice(oldPrice))
                                .build();

                        products.add(product);
                    }
                }
            }

            Promotion kauflandPromotion = Promotion.builder()
                    .startDate(parseDate(promoSalesFrom))
                    .endDate(parseDate(promoSalesTo))
                    .storeByStoreId(kauflandStore)
                    .build();

            Promotion promotion = promotionService.createPromotion(kauflandPromotion);

            for (Product product : products) {
                product.setPromotion(promotion);
            }

            productService.createProducts(products);
            log.info("Successfully inserted {} kaufland products", products.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private Double parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) return null;
        String cleaned = priceStr.replaceAll("[^\\d,\\.]", "").trim();
        if (cleaned.contains(",") && cleaned.contains(".")) {
            cleaned = cleaned.replace(".", "").replace(",", ".");
        } else if (cleaned.contains(",")) {
            cleaned = cleaned.replace(",", ".");
        }
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void scrapeBillaPromotions() {
        try {
            Document document = Jsoup.connect(billaCategoryUrl).get();
            PromotionPeriod promotionPeriod = getBillaPromotionPeriod(document);

            if (promotionPeriod == null) {
                log.warn("Could not determine Billa promotion period, skipping scrape.");
                return;
            }

            Store billaStore = storeService.getStoreById(Constants.BILLA_ID);

            Promotion billaPromotion = Promotion.builder()
                    .startDate(promotionPeriod.getStartDate())
                    .endDate(promotionPeriod.getEndDate())
                    .storeByStoreId(billaStore)
                    .build();

            Promotion savedPromotion = promotionService.createPromotion(billaPromotion);

            Elements products = document.select("div.product");

            for (int i = 5; i < products.size() - 10; i++) {
                Element productDiv = products.get(i);

                String productTitle = getProductTitle(productDiv, ".actualProduct");
                Double productOldPrice = getBillaProductOldPrice(productDiv);
                Double productNewPrice = getBillaProductNewPrice(productDiv);
                String productDiscountPhrase = getProductDiscountPhrase(productDiv);

                if (productNewPrice != null) {
                    Product product = Product.builder()
                            .title(productTitle)
                            .oldPrice(productOldPrice)
                            .newPrice(productNewPrice)
                            .discountPhrase(productDiscountPhrase)
                            .promotion(savedPromotion)
                            .build();

                    productService.createProduct(product);
                }
            }

            log.info("Successfully scraped Billa promotions: {} products added", products.size() - 15);

        } catch (IOException e) {
            log.error("Failed to scrape Billa promotions", e);
            throw new RuntimeException(e);
        }
    }

    private String getProductTitle(Element product, String className) {
        return Optional.ofNullable(product.selectFirst(className))
                .map(Element::text)
                .map(String::trim)
                .map(text -> text.length() > 255 ? text.substring(0, 255) : text)
                .orElse(null);
    }

    private String getProductDiscountPhrase(Element product) {
        return Optional.ofNullable(product.selectFirst(".discount"))
                .map(Element::text)
                .map(String::trim)
                .map(text -> text.length() > 2 ? text.substring(2) : "")
                .orElse(null);
    }

    private Double getBillaProductOldPrice(Element product) {
        return Optional.ofNullable(product.select(".price"))
                .filter(prices -> prices.size() == 2)
                .map(prices -> prices.get(0).text().trim())
                .flatMap(text -> {
                    try {
                        return Optional.of(Double.parseDouble(text));
                    } catch (NumberFormatException e) {
                        log.warn("Could not parse old price: '{}'", text);
                        return Optional.empty();
                    }
                })
                .orElse(null);
    }

    private Double getBillaProductNewPrice(Element product) {
        return Optional.ofNullable(product.select(".price"))
                .filter(prices -> !prices.isEmpty())
                .map(prices -> prices.size() == 2 ? prices.get(1).text().trim() : prices.get(0).text().trim())
                .flatMap(text -> {
                    try {
                        return Optional.of(Double.parseDouble(text));
                    } catch (NumberFormatException e) {
                        log.warn("Could not parse new price: '{}'", text);
                        return Optional.empty();
                    }
                })
                .orElse(null);
    }

    private PromotionPeriod getBillaPromotionPeriod(Document document) {
        Elements dateDivs = document.select("div.actualProduct");
        if (dateDivs.size() <= 2) return null;

        String text = dateDivs.get(2).text();

        try {
            String[] parts = text.split(" ");
            String startStr = parts[3];
            String endStr = parts[5];

            String year = endStr.split("\\.")[2];
            if (!startStr.endsWith(year)) {
                startStr = startStr + year;
            }

            Date startDate = convertToDate(LocalDate.parse(startStr, DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_PATTERN)));
            Date endDate = convertToDate(LocalDate.parse(endStr, DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_PATTERN)));

            return new PromotionPeriod(startDate, endDate);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Date convertToDate(LocalDate localDate) {
        return java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void getLidlCategoryProductsUrl(String categoryUrl) {
        Store lidlStore = storeService.getStoreById(Constants.LIDL_ID);

        try {
            Document doc = Jsoup.connect(categoryUrl).get();

            if (doc != null) {
                Elements productDivs = doc.select("div.AProductGridbox__GridTilePlaceholder");

                for (Element productDiv : productDivs) {
                    String jsonData = productDiv.attr("data-grid-data");
                    JSONArray jsonArray = new JSONArray(jsonData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject productData = jsonArray.getJSONObject(i);

                        JSONObject keyFactsObject = productData.optJSONObject("keyfacts");
                        String productTitle = (keyFactsObject != null) ? keyFactsObject.optString("fullTitle", "N/A") : "N/A";

                        JSONObject priceObject = productData.optJSONObject("price");
                        double productOldPrice = (priceObject != null) ? priceObject.optDouble("oldPrice", 0.0) : 0.0;
                        double productNewPrice = (priceObject != null) ? priceObject.optDouble("price", 0.0) : 0.0;

                        JSONObject discountObject = (priceObject != null) ? priceObject.optJSONObject("discount") : null;
                        String productDiscountPhrase = (discountObject != null) ? discountObject.optString("discountText", "N/A") : "N/A";

                        String promotionDateRange = extractPromotionDateRange(productData);
                        PromotionInterval promotionInterval = getLidlProductPromotionInterval(promotionDateRange);

                        if (promotionInterval != null && promotionInterval.getPromotionStarts() != null) {

                            Promotion productPromotion = Promotion.builder()
                                    .startDate(promotionInterval.getPromotionStarts())
                                    .endDate(promotionInterval.getPromotionExpires())
                                    .storeByStoreId(lidlStore)
                                    .build();

                            promotionService.createPromotion(productPromotion);

                            Product product = Product.builder()
                                    .title(productTitle)
                                    .oldPrice(productOldPrice)
                                    .newPrice(productNewPrice)
                                    .discountPhrase(productDiscountPhrase)
                                    .promotion(productPromotion)
                                    .build();

                            productService.createProduct(product);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLidlCategoriesUrls() {
        String promotionsUrl = getLidlPromotionsUrl();

        if (promotionsUrl == null) {
            return Collections.emptyList();
        }

        try {
            Document doc = Jsoup.connect(promotionsUrl).get();

            return doc.select("li.AHeroStageItems__Item a").stream()
                    .map(a -> lidlUrl + a.attr("href"))
                    .toList();

        } catch (IOException e) {
            log.error("Failed to fetch Lidl categories URLs", e);
            return Collections.emptyList();
        }
    }

    public String getLidlPromotionsUrl() {
        try {
            Document document = Jsoup.connect(lidlUrl).get();
            Elements navItems = document.select(".n-header__main-navigation-link.n-header__main-navigation-link--first");

            for (Element urlElement : navItems) {
                if ("Храни и напитки".equals(urlElement.text().trim())) {
                    return lidlUrl + urlElement.attr("href");
                }
            }

        } catch (IOException e) {
            log.error("Failed to fetch Lidl promotions URL", e);
            return null;
        }

        return null;
    }

    public PromotionInterval getLidlProductPromotionInterval(String promotionInterval) {
        Date promotionStarts = null;
        Date promotionExpires;
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_TIME_FORMATTER_PATTERN, Locale.getDefault());

        try {
            String[] words = promotionInterval.split("\\s+");

            if (!words[0].equalsIgnoreCase("само") && words.length >= 3) {
                promotionStarts = DateUtils.convertToDate(words[words.length - 3], formatter);
            }

            promotionExpires = DateUtils.convertToDate(words[words.length - 1], formatter);

            if (promotionExpires == null && promotionStarts != null) {
                promotionExpires = new Date(promotionStarts.getTime() + (7 * 24 * 60 * 60 * 1000L));
            }

            if (promotionStarts == null && promotionExpires == null) {
                log.error("Skipping unparseable promotion interval: " + promotionInterval);
                return null;
            }

        } catch (IndexOutOfBoundsException | DateTimeParseException e) {
            log.error("Skipping promotion due to unparseable format: " + promotionInterval);
            return null;
        }

        return new PromotionInterval(promotionStarts, promotionExpires);
    }

    private String extractPromotionDateRange(JSONObject productData) {
        return Optional.ofNullable(productData)
                .map(p -> p.optJSONObject("stockAvailability"))
                .map(s -> s.optJSONObject("badgeInfo"))
                .map(b -> b.optJSONArray("badges"))
                .filter(badges -> !badges.isEmpty())
                .map(badges -> badges.getJSONObject(0).optString("text", ""))
                .orElse(null);
    }
}
