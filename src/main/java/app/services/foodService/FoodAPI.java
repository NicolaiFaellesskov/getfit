package app.services.foodService;

import app.DTOs.FoodDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FoodAPI {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    public List<FoodDTO> searchFood(String search) {

        try {
            String encodedSearch = URLEncoder.encode(
                    search, StandardCharsets.UTF_8
            );

            List<JsonNode> allProducts = new ArrayList<>();

            for (int page = 1; page <= 5; page++) {

                String url = "https://world.openfoodfacts.org/cgi/search.pl"
                        + "?search_terms=" + encodedSearch
                        + "&search_simple=1"
                        + "&action=process"
                        + "&json=1"
                        + "&page=" + page
                        + "&page_size=20"
                        + "&fields=code,product_name,brands,quantity,"
                        + "image_front_small_url,nutriments";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();

                HttpResponse<String> response = null;

                for (int i = 0; i < 3; i++) {
                    try {
                        response = client.send(
                                request,
                                HttpResponse.BodyHandlers.ofString()
                        );

                        if (response.statusCode() == 200) {
                            break;
                        }

                        response = null;
                        Thread.sleep(200L * (i + 1));

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw e;
                    } catch (Exception e) {
                        response = null;
                        Thread.sleep(200L * (i + 1));
                    }
                }

                if (response == null) {
                    continue;
                }

                if (response.statusCode() != 200) {
                    continue;
                }

                JsonNode products = mapper.readTree(response.body())
                        .path("products");

                if (!products.isArray() || products.isEmpty()) {
                    break;
                }

                for (JsonNode product : products) {
                    allProducts.add(product);
                }
            }

            List<ProductResult> results = new ArrayList<>();

            for (JsonNode product : allProducts) {

                String productName =
                        product.path("product_name").asText("");

                String brand =
                        product.path("brands").asText("");

                if (productName.isBlank()) {
                    continue;
                }

                int score = calculateScore(
                        search, productName, brand
                );

                results.add(new ProductResult(product, score));
            }

            results.sort(
                    Comparator.comparingInt(ProductResult::score)
                            .reversed()
            );

            List<FoodDTO> foods = new ArrayList<>();

            for (ProductResult result : results) {
                FoodDTO food = mapper.treeToValue(
                        result.product(), FoodDTO.class
                );

                foods.add(food);
            }

            return foods;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(
                    "Foodsøgning blev afbrudt", e
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Fejl ved Open Food Facts API", e
            );
        }
    }

    private int calculateScore(
            String search,
            String productName,
            String brand
    ) {
        String searchLower = search.toLowerCase()
                .trim()
                .replace("_", " ");

        String nameLower = productName.toLowerCase();
        String brandLower = brand.toLowerCase();

        int score = 0;

        if (nameLower.equals(searchLower)) {
            score += 100;
        }

        if (nameLower.startsWith(searchLower)) {
            score += 50;
        }

        if (nameLower.contains(searchLower)) {
            score += 30;
        }

        if (brandLower.contains(searchLower)) {
            score += 10;
        }

        String[] words = searchLower.split("\\s+");

        for (String word : words) {
            if (word.length() >= 3
                    && nameLower.contains(word)) {
                score += 15;
            }
        }

        return score;
    }

    private record ProductResult(
            JsonNode product,
            int score
    ) {
    }
}