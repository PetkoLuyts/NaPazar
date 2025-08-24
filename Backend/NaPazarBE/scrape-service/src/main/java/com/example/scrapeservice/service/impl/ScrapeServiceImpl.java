package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.constants.Constants;
import com.example.scrapeservice.dto.PromotionInterval;
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
import java.util.Arrays;
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
        List<String> categoryUrls = getKauflandCategoriesUrls();
        Store kauflandStore = storeService.getStoreById(Constants.KAUFLAND_ID);

        for (String categoryUrl : categoryUrls) {
            List<String> productUrls = getKauflandCategoryProductsUrl(categoryUrl);

            for (String productUrl : productUrls) {
                try {
                    Document productPage = Jsoup.connect(productUrl).get();

                    String promotionText = getKauflandPromotionText(productPage, Arrays.asList("a-eye-catcher", "a-eye-catcher--secondary"));
                    Date promotionStarts = null;
                    Date promotionExpires = null;
                    if (promotionText != null) {
                        String[] parts = promotionText.split("\\s+");
                        String startDateStr = parts[0];
                        String endDateStr = parts[parts.length - 1];

                        promotionStarts = convertToDate(DateUtils.parsePartialDate(startDateStr));
                        promotionExpires = convertToDate(DateUtils.parsePartialDate(endDateStr));
                    }

                    String productTitle = getProductTitle(productPage, ".t-offer-detail__title");
                    String productDiscountPhrase = getKauflandProductDiscountPhrase(productPage, ".a-pricetag__discount", ".a-pricetag__old-price");
                    Double productOldPrice = getKauflandProductOldPrice(productPage, ".a-pricetag__old-price");
                    Double productNewPrice = getProductNewPrice(productPage, ".a-pricetag__price");

                    if (productTitle != null && !productDiscountPhrase.equals("само") && !productDiscountPhrase.equals("тази седмица"))
                    {
                        Promotion promotion = Promotion.builder()
                                .storeByStoreId(kauflandStore)
                                .startDate(promotionStarts)
                                .endDate(promotionExpires)
                                .build();

                        Promotion savedPromotion = promotionService.createPromotion(promotion);

                        Product product = Product.builder()
                                .title(productTitle)
                                .oldPrice(productOldPrice)
                                .newPrice(productNewPrice)
                                .discountPhrase(productDiscountPhrase)
                                .promotion(savedPromotion)
                                .build();

                        productService.createProduct(product);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void scrapeBillaPromotions() {
        final Document document;

        try {
            document = Jsoup.connect(billaCategoryUrl).get();
            Date billaPromotionStart = getBillaPromotionStart(document);
            Date billaPromotionEnd = getBillaPromotionEnd(document);

            Store billaStore = storeService.getStoreById(Constants.BILLA_ID);

            Promotion billaPromotion = Promotion.builder()
                    .startDate(billaPromotionStart)
                    .endDate(billaPromotionEnd)
                    .storeByStoreId(billaStore)
                    .build();

            Promotion savedPromotion = promotionService.createPromotion(billaPromotion);

            Elements products = document.select("div.product");

            for (int i = 5; i < products.size() - 10; i++) {
                String productTitle = getProductTitle(products.get(i), ".actualProduct");
                Double productOldPrice = getBillaProductOldPrice(products.get(i));
                Double productNewPrice = getBillaProductNewPrice(products.get(i));
                String productDiscountPhrase = getProductDiscountPhrase(products.get(i));

                Product product = Product.builder()
                        .title(productTitle)
                        .oldPrice(productOldPrice)
                        .newPrice(productNewPrice)
                        .discountPhrase(productDiscountPhrase)
                        .promotion(savedPromotion)
                        .build();

                if (product.getNewPrice() != null) {
                    productService.createProduct(product);
                }
            }
        } catch (IOException e) {
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

    private Date getBillaPromotionStart(Document document) {
        return Optional.ofNullable(document.selectFirst("div.date"))
                .map(Element::text)
                .map(text -> text.split(" "))
                .filter(parts -> parts.length >= 5)
                .map(parts -> parts[parts.length - 5])
                .flatMap(dateStr -> {
                    try {
                        LocalDate localDate = LocalDate.parse(dateStr,
                                DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_PATTERN));
                        return Optional.of(convertToDate(localDate));
                    } catch (Exception e) {
                        log.warn("Could not parse promotion start date: '{}'", dateStr, e);
                        return Optional.empty();
                    }
                })
                .orElse(null);
    }

    private Date getBillaPromotionEnd(Document document) {
        return Optional.ofNullable(document.selectFirst("div.date"))
                .map(Element::text)
                .map(text -> text.split(" "))
                .filter(parts -> parts.length >= 2)
                .map(parts -> parts[parts.length - 2])
                .flatMap(dateStr -> {
                    try {
                        LocalDate localDate = LocalDate.parse(dateStr,
                                DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_PATTERN));
                        return Optional.of(convertToDate(localDate));
                    } catch (Exception e) {
                        log.warn("Could not parse promotion end date: '{}'", dateStr, e);
                        return Optional.empty();
                    }
                })
                .orElse(null);
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

            return document.select(".n-header__main-navigation-link.n-header__main-navigation-link--first").stream()
                    .filter(urlElement -> "Нови предложения".equals(urlElement.text()))
                    .findFirst()
                    .map(urlElement -> lidlUrl + urlElement.parent().attr("href"))
                    .orElse(null);

        } catch (IOException e) {
            log.error("Failed to fetch Lidl promotions URL", e);
            return null;
        }
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

    public List<String> getKauflandCategoryProductsUrl(String categoryUrl) {
        try {
            Document document = Jsoup.connect(categoryUrl).get();

            return document.select("a.m-offer-tile__link, a.u-button--hover-children").stream()
                    .filter(product -> "_self".equals(product.attr("target")))
                    .map(product -> Constants.KAUFLAND_URL + product.attr("href"))
                    .toList();

        } catch (IOException e) {
            log.error("Failed to fetch category products URLs from: {}", categoryUrl, e);
            return Collections.emptyList();
        }
    }

    public List<String> getKauflandCategoriesUrls() {
        List<String> categories = new ArrayList<>();

        try {
            List<String> promotionsUrls = getKauflandPromotionsUrls();
            if (promotionsUrls.isEmpty()) {
                return categories;
            }

            Document document = Jsoup.connect(promotionsUrls.get(1)).get();

            Elements buttons = document.select(".a-button--primary");
            String mainCategoryUrl = null;

            for (Element button : buttons) {
                Element linkElement = button.selectFirst("a");

                if (linkElement != null && linkElement.text().startsWith("Разгледай всички предложения")) {
                    mainCategoryUrl = linkElement.attr("href");
                    break;
                }
            }

            if (mainCategoryUrl == null) {
                return categories;
            }

            if (!mainCategoryUrl.startsWith("https")) {
                mainCategoryUrl = kauflandUrl + mainCategoryUrl;
            }

            document = Jsoup.connect(mainCategoryUrl).get();

            Elements promotionSections = new Elements(
                    document.select("ul.m-accordion__list.m-accordion__list--level-2").subList(0, 2)
            );

            for (Element section : promotionSections) {
                Elements promotionLinks = section.select("a");
                for (Element link : promotionLinks) {
                    String categoryUrl = link.attr("href");
                    if (!categoryUrl.startsWith("https")) {
                        categoryUrl = kauflandUrl + categoryUrl;
                    }
                    categories.add(categoryUrl);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public List<String> getKauflandPromotionsUrls() {
        String mainPromotionsUrl = getKauflandPromotionsMain();
        if (mainPromotionsUrl == null) {
            return Collections.emptyList();
        }

        try {
            Document document = Jsoup.connect(mainPromotionsUrl).get();

            return document.select(".textimageteaser").stream()
                    .map(component -> Optional.ofNullable(component.selectFirst("a")))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(a -> {
                        String url = a.attr("href");
                        return url.startsWith("https:") ? url : Constants.KAUFLAND_URL + url;
                    })
                    .toList();

        } catch (IOException e) {
            log.error("Failed to fetch Kaufland promotions URLs", e);
            return Collections.emptyList();
        }
    }

    public String getKauflandPromotionsMain() {
        try {
            Document document = Jsoup.connect(kauflandUrl).get();

            return document.select(".m-accordion__link").stream()
                    .filter(link -> link.select("span").text().startsWith("Предложения"))
                    .findFirst()
                    .map(link -> Constants.KAUFLAND_URL + link.attr("href"))
                    .orElse(null);

        } catch (IOException e) {
            log.error("Failed to fetch Kaufland promotions main page", e);
            return null;
        }
    }

    public String getKauflandPromotionText(Document document, List<String> classNames) {
        return Optional.ofNullable(document.select("div." + String.join(".", classNames)))
                .filter(divs -> !divs.isEmpty())
                .map(divs -> divs.first().selectFirst("span"))
                .map(Element::text)
                .map(String::trim)
                .orElse(null);
    }

    public String getKauflandProductDiscountPhrase(Document soup, String className1, String className2) {
        try {
            Element discountElement = soup.selectFirst(className1);
            if (discountElement != null) {
                return discountElement.text().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Element oldPriceElement = soup.selectFirst(className2);

            if (oldPriceElement != null) {
                try {
                    Float.parseFloat(oldPriceElement.text().trim().replace(",", "."));
                    return null;
                } catch (NumberFormatException e) {
                    return oldPriceElement.text().trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Double getProductNewPrice(Document document, String className) {
        return Optional.ofNullable(document.selectFirst(className))
                .map(Element::text)
                .map(String::trim)
                .map(text -> text.replace(",", "."))
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

    public Double getKauflandProductOldPrice(Document soup, String className) {
        return Optional.ofNullable(soup.selectFirst(className))
                .map(Element::text)
                .map(String::trim)
                .map(text -> text.replace("\u00A0", ""))
                .map(text -> text.replaceAll("[^\\d,\\.]", ""))
                .map(text -> text.replace(",", "."))
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
}
