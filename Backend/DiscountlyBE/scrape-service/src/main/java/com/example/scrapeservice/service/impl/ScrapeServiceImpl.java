package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.model.Product;
import com.example.scrapeservice.model.Promotion;
import com.example.scrapeservice.model.Store;
import com.example.scrapeservice.repository.PromotionRepository;
import com.example.scrapeservice.service.ProductService;
import com.example.scrapeservice.service.PromotionService;
import com.example.scrapeservice.service.ScrapeService;
import com.example.scrapeservice.service.StoreService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class ScrapeServiceImpl implements ScrapeService {
    @Value("${billa.url}")
    private String billaCategoryUrl;
    @Value("${lidl.url}")
    private String lidlUrl;
    @Value("${kaufland.url}")
    private String kauflandUrl;
    private static final int BILLA_ID = 1;
    private static final int LIDL_ID = 2;

    private final PromotionRepository promotionRepository;
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
        var urls = getKauflandCategoriesUrls();

        System.out.println(urls);
    }

    private void scrapeBillaPromotions() {
        final Document document;

        try {
            document = Jsoup.connect(billaCategoryUrl).get();
            Date billaPromotionStart = getBillaPromotionStart(document);
            Date billaPromotionEnd = getBillaPromotionEnd(document);

            Store billaStore = storeService.getStoreById(BILLA_ID);

            Promotion billaPromotion = Promotion.builder()
                    .startDate(billaPromotionStart)
                    .endDate(billaPromotionEnd)
                    .storeByStoreId(billaStore)
                    .build();

            Promotion savedPromotion = promotionService.createPromotion(billaPromotion);

            Elements products = document.select("div.product");

            for (int i = 5; i < products.size() - 10; i++) {
                String productTitle = getProductTitle(products.get(i), ".actualProduct");
                Double productOldPrice = getBillaProductOldPrice(products.get(i), ".price");
                Double productNewPrice = getBillaProductNewPrice(products.get(i), ".price");
                String productDiscountPhrase = getProductDiscountPhrase(products.get(i), ".discount");

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
        try {
            Element productTitleElement = product.selectFirst(className);

            if (productTitleElement != null) {
                if (productTitleElement.text().length() > 255) {
                    return productTitleElement.text().substring(0, 255);
                }

                return productTitleElement.text();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getProductDiscountPhrase(Element product, String className) {
        try {
            Element discountPhraseElement = product.selectFirst(className);

            if (discountPhraseElement != null) {
                return discountPhraseElement.text().trim().substring(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Double getBillaProductOldPrice(Element product, String className) {
        try {
            Elements productPrices = product.select(className);
            Double productOldPrice = null;

            if (productPrices.size() == 2) {
                String oldPriceText = productPrices.get(0).text().trim();
                productOldPrice = Double.parseDouble(oldPriceText);
            }

            return productOldPrice;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Double getBillaProductNewPrice(Element product, String className) {
        try {
            Elements productPrices = product.select(className);
            Double productNewPrice = null;

            if (productPrices.size() == 2) {
                productNewPrice = Double.parseDouble(productPrices.get(1).text().trim());
            } else if (productPrices.size() == 1) {
                productNewPrice = Double.parseDouble(productPrices.get(0).text().trim());
            }

            return productNewPrice;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Date getBillaPromotionStart(Document document) {
        Element dateDiv = document.selectFirst("div.date");

        if (dateDiv != null) {
            String[] promotionText = dateDiv.text().split(" ");
            try {
                LocalDate localDate = LocalDate.parse(promotionText[promotionText.length - 5], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                return convertToDate(localDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private Date getBillaPromotionEnd(Document document) {
        Element dateDiv = document.selectFirst("div.date");

        if (dateDiv != null) {
            String[] promotionText = dateDiv.text().split(" ");
            try {
                LocalDate localDate = LocalDate.parse(promotionText[promotionText.length - 2], DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                return convertToDate(localDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private Date convertToDate(LocalDate localDate) {
        return java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void getLidlCategoryProductsUrl(String categoryUrl) {
        List<String> categoryProductsUrls = new ArrayList<>();
        Store lidlStore = storeService.getStoreById(LIDL_ID);

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
                        String productDescription = (keyFactsObject != null) ? keyFactsObject.optString("description", "N/A") : "N/A";

                        JSONObject priceObject = productData.optJSONObject("price");
                        double productOldPrice = (priceObject != null) ? priceObject.optDouble("oldPrice", 0.0) : 0.0;
                        double productNewPrice = (priceObject != null) ? priceObject.optDouble("price", 0.0) : 0.0;

                        JSONObject discountObject = (priceObject != null) ? priceObject.optJSONObject("discount") : null;
                        String productDiscountPhrase = (discountObject != null) ? discountObject.optString("discountText", "N/A") : "N/A";

                        JSONObject basePriceObject = (priceObject != null) ? priceObject.optJSONObject("basePrice") : null;
                        String productQuantity = (basePriceObject != null) ? basePriceObject.optString("text", "N/A") : "N/A";

                        String promotionDateRange = extractPromotionDateRange(productData);
                        PromotionInterval promotionInterval = getLidlProductPromotionInterval(promotionDateRange);

                        if (promotionInterval != null && promotionInterval.promotionStarts != null) {

                            Promotion productPromotion = Promotion.builder()
                                    .startDate(promotionInterval.promotionStarts)
                                    .endDate(promotionInterval.promotionExpires)
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

                            System.out.println("Created Product: " + product.getTitle());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLidlCategoriesUrls() {
        List<String> categoriesUrls = new ArrayList<>();
        String promotionsUrl = getLidlPromotionsUrl();

        if (promotionsUrl != null) {
            try {
                Document doc = Jsoup.connect(promotionsUrl).get();
                Elements categories = doc.select("li.AHeroStageItems__Item a");

                for (Element category : categories) {
                    String categoryUrl = lidlUrl + category.attr("href");
                    categoriesUrls.add(categoryUrl);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return categoriesUrls;
    }

    public String getLidlPromotionsUrl() {
        try {
            Document document = Jsoup.connect(lidlUrl).get();
            Elements navItems = document.select(".n-header__main-navigation-link.n-header__main-navigation-link--first");

            for (Element urlElement : navItems) {
                if (urlElement.text().equals("Нови предложения")) {
                    return lidlUrl + urlElement.parent().attr("href");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PromotionInterval getLidlProductPromotionInterval(String promotionInterval) {
        java.util.Date promotionStarts = null;
        java.util.Date promotionExpires = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        try {
            String[] words = promotionInterval.split("\\s+");

            if (words[0].equalsIgnoreCase("само")) {
                promotionStarts = null;
            } else if (words.length >= 3) {
                promotionStarts = convertToDate(words[words.length - 3], formatter);
            }

            promotionExpires = convertToDate(words[words.length - 1], formatter);

            if (promotionExpires == null && promotionStarts != null) {
                promotionExpires = new Date(promotionStarts.getTime() + (7 * 24 * 60 * 60 * 1000L));
            }

            if (promotionStarts == null && promotionExpires == null) {
                System.out.println("Skipping unparseable promotion interval: " + promotionInterval);
                return null;
            }

        } catch (IndexOutOfBoundsException | DateTimeParseException e) {
            System.out.println("Skipping promotion due to unparseable format: " + promotionInterval);
            return null;
        }

        return new PromotionInterval(promotionStarts, promotionExpires);
    }

    private static java.util.Date convertToDate(String dateString, SimpleDateFormat formatter) {
        try {
            String fullDateString = dateString + new SimpleDateFormat("yyyy").format(new java.util.Date());
            return formatter.parse(fullDateString);
        } catch (ParseException e) {
            System.out.println("Failed to parse date: " + dateString);
            return null;
        }
    }


    private String extractPromotionDateRange(JSONObject productData) {
        JSONObject stockAvailability = productData.optJSONObject("stockAvailability");
        if (stockAvailability != null) {
            JSONObject badgeInfo = stockAvailability.optJSONObject("badgeInfo");
            if (badgeInfo != null) {
                JSONArray badges = badgeInfo.optJSONArray("badges");
                if (badges != null && badges.length() > 0) {
                    String promotionDateRange = badges.getJSONObject(0).optString("text", "");
                    return promotionDateRange;
                }
            }
        }
        return null;
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
        List<String> promotionsUrls = new ArrayList<>();
        String mainPromotionsUrl = getKauflandPromotionsMain();

        if (mainPromotionsUrl == null) {
            return promotionsUrls;
        }

        try {
            Document document = Jsoup.connect(mainPromotionsUrl).get();
            Elements components = document.select(".textimageteaser");

            for (Element component : components) {
                String url = component.selectFirst("a").attr("href");
                if (!url.startsWith("https:")) {
                    url = "https://www.kaufland.bg" + url;
                }
                promotionsUrls.add(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return promotionsUrls;
    }

    public String getKauflandPromotionsMain() {
        try {
            Document document = Jsoup.connect(kauflandUrl).get();

            Elements navigationLinks = document.select(".m-accordion__link");

            for (Element link : navigationLinks) {
                if (link.select("span").text().startsWith("Предложения")) {
                    return "https://www.kaufland.bg" + link.attr("href");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    @Data
    @AllArgsConstructor
    private class PromotionInterval {
        private java.util.Date promotionStarts;
        private java.util.Date promotionExpires;
    }
}
