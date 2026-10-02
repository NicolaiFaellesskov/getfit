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

    public FoodDTO searchFood(String search) {

        try {
            String encodedSearch = URLEncoder.encode(search, StandardCharsets.UTF_8);
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

                System.out.println("Søger efter: " + search);
                System.out.println("Page: " + page);
                System.out.println("URL: " + url);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(1))
                        .GET()
                        .build();

                HttpResponse<String> response = null;

                for (int i = 0; i < 15; i++) {

                    try {
                        response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                            break;
                        }

                        System.out.println("API gav status " + response.statusCode());

                        if (response.statusCode() == 503) {
                            System.out.println("Open Food Facts er midlertidigt utilgængelig.");
                            Thread.sleep(40);
                        }

                    } catch (Exception e) {

                        System.out.println("Forbindelsesfejl: " + e.getMessage());
                        Thread.sleep(20);
                    }
                }

                if (response == null || response.statusCode() != 200) {
                    System.out.println("Kunne ikke hente page " + page + ". Springer over.");
                    continue;
                }

                JsonNode root = mapper.readTree(response.body());
                JsonNode products = root.path("products");

                if (!products.isArray() || products.isEmpty()) {
                    System.out.println("Ingen flere produkter fundet på page " + page);
                    break;
                }

                for (JsonNode product : products) {
                    allProducts.add(product);
                }
            }

            if (allProducts.isEmpty()) {
                System.out.println("Ingen produkter fundet for: " + search);
                return null;
            }

            List<ProductResult> results = new ArrayList<>();

            for (JsonNode product : allProducts) {

                String productName = product.path("product_name").asText("");
                String brand = product.path("brands").asText("");

                int score = calculateScore(search, productName, brand);

                results.add(new ProductResult(product, score));
            }

            results.sort(Comparator.comparingInt(ProductResult::score).reversed());

            ProductResult bestMatch = results.get(0);

            return mapper.treeToValue(bestMatch.product(), FoodDTO.class);

        } catch (Exception e) {

            throw new RuntimeException("Fejl ved Open Food Facts API", e);
        }
    }

    private int calculateScore(String search, String productName, String brand) {

        String searchLower = search.toLowerCase().trim().replace("_", " ");

        String nameLower = productName.toLowerCase();

        String brandLower = brand.toLowerCase();

        int score = 0;

        // Eksakt match
        if (nameLower.equals(searchLower)) {
            score += 100;
        }

        // Produktnavnet starter med søgningen
        if (nameLower.startsWith(searchLower)) {
            score += 50;
        }

        // Produktnavnet indeholder hele søgningen
        if (nameLower.contains(searchLower)) {
            score += 30;
        }

        // Brand matcher
        if (brandLower.contains(searchLower)) {
            score += 10;
        }

        // Match på individuelle ord
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