package app.services.foodService;

import app.persistence.FoodDTO;
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

            String url = "https://world.openfoodfacts.org/cgi/search.pl"
                    + "?search_terms=" + encodedSearch
                    + "&search_simple=1"
                    + "&action=process"
                    + "&json=1"
                    + "&page_size=20"
                    + "&fields=code,product_name,brands,quantity,"
                    + "image_front_small_url,nutriments";

            System.out.println("Søger efter: " + search);
            System.out.println("URL: " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "GetFit/1.0 (student project)")
                    .timeout(Duration.ofSeconds(1))
                    .GET()
                    .build();

            HttpResponse<String> response = null;

            for (int i = 0; i < 10; i++) {

                try {
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        break;
                    }

                    System.out.println("API gav status " + response.statusCode());

                    if (response.statusCode() == 503) {System.out.println("Open Food Facts er midlertidigt utilgængelig.");
                        Thread.sleep(5000);
                    }

                } catch (Exception e) {

                    System.out.println("Forbindelsesfejl: " + e.getMessage());

                    Thread.sleep(3000);
                }
            }

            if (response == null) {
                throw new RuntimeException("Kunne ikke få forbindelse til Open Food Facts.");
            }

            if (response.statusCode() != 200) {
                throw new RuntimeException("Open Food Facts returnerede HTTP " + response.statusCode());
            }

            JsonNode root = mapper.readTree(response.body());
            JsonNode products = root.path("products");
            for (JsonNode product : products) {
                System.out.println(product.path("product_name").asText());
            }

            if (!products.isArray() || products.isEmpty()) {
                System.out.println("Ingen produkter fundet for: " + search);

                return null;
            }

            /*
             * Open Food Facts har allerede lavet selve
             * fritekstsøgningen for os.
             *
             * Vi bruger stadig en simpel score til at
             * vælge det bedste resultat blandt de 20.
             */

            List<ProductResult> results = new ArrayList<>();

            for (JsonNode product : products) {

                String productName = product.path("product_name").asText("");

                String brand = product.path("brands").asText("");

                int score = calculateScore(search, productName, brand);

                results.add(new ProductResult(product, score));
            }

            results.sort(Comparator.comparingInt(ProductResult::score).reversed());

            ProductResult bestMatch = results.get(0);

            String bestName = bestMatch.product().path("product_name")
                    .asText("Ukendt produkt");


            return mapper.treeToValue(bestMatch.product(),FoodDTO.class);

        } catch (Exception e) {

            throw new RuntimeException("Fejl ved Open Food Facts API", e);
        }
    }

    private int calculateScore(String search, String productName, String brand) {

        String searchLower = search.toLowerCase().trim();

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