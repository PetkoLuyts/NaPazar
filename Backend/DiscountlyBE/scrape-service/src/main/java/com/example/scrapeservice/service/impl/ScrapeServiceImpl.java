package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.model.Product;
import com.example.scrapeservice.model.Promotion;
import com.example.scrapeservice.model.Store;
import com.example.scrapeservice.repository.PromotionRepository;
import com.example.scrapeservice.service.ProductService;
import com.example.scrapeservice.service.PromotionService;
import com.example.scrapeservice.service.ScrapeService;
import com.example.scrapeservice.service.StoreService;
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
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ScrapeServiceImpl implements ScrapeService {
    @Value("${billa.url}")
    private String billaCategoryUrl;
    @Value("${lidl.url}")
    private String lidlUrl;
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
                return Date.valueOf(localDate);
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
                return Date.valueOf(localDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void scrapeLidlData() {
        List<String> lidlCategories = getLidlCategoriesUrls();

        for(String lidlCategory : lidlCategories) {
            List<String> productUrls = getLidlCategoryProductsUrl(lidlCategory);

            for(String productUrl : productUrls) {


            }
        }

        System.out.println("test");
    }

    public List<String> getLidlCategoryProductsUrl(String categoryUrl) {
        List<String> categoryProductsUrls = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(categoryUrl).get();

            if (doc != null) {
                Elements productDivs = doc.select("div.AProductGridbox__GridTilePlaceholder");

                for (Element productDiv : productDivs) {
                    String jsonData = productDiv.attr("data-grid-data");
                    JSONArray jsonArray = new JSONArray(jsonData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject productData = jsonArray.getJSONObject(i);

                        String title = productData.optJSONObject("keyfacts").optString("fullTitle", "N/A");
                        String discountPhrase = productData.optJSONObject("price").optJSONObject("discount").optString("discountText", "N/A");
                        double oldPrice = productData.optJSONObject("price").optDouble("oldPrice", 0.0);
                        double newPrice = productData.optJSONObject("price").optDouble("price", 0.0);
                        String quantity = productData.optJSONObject("price").optJSONObject("basePrice").optString("text", "N/A");
                        String description = productData.optJSONObject("keyfacts").optString("description", "N/A");
                        
                        String promotionText = productDiv.select(".ribbon__text").text();
                        String promotionStarts = extractPromotionStartDate(promotionText);
                        String promotionEnds = extractPromotionEndDate(promotionText);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return categoryProductsUrls;
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

    private String extractPromotionStartDate(String promotionText) {
        if (promotionText.contains("от")) {
            String[] parts = promotionText.split("от");
            if (parts.length > 1) {
                return parts[1].trim();
            }
        }
        return "N/A";
    }

    private String extractPromotionEndDate(String promotionText) {
        if (promotionText.contains("-")) {
            String[] parts = promotionText.split("-");
            if (parts.length > 1) {
                return parts[1].trim();
            }
        }
        return "N/A";
    }
}
