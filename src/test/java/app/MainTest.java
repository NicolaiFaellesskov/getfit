package app;

import app.DTOs.FoodDTO;
import app.services.foodService.FoodAPI;
import app.services.foodService.FoodSearchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testAverageSearchTime() {
        long averageSeacrhTime = 0;
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();
            FoodSearchService foodSearchService = new FoodSearchService();

            Future<FoodDTO> future = foodSearchService.searchFood("Chicken");

            FoodDTO food = null;
            try {
                food = future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            long end = System.currentTimeMillis();
            long time = end - start;
            averageSeacrhTime += time;
            System.out.println("Response tog: " + time + " ms");
            if (food == null) {
                System.out.println("Ingen produkter fundet.");

            }

        }
        System.out.println("Response tog: " + averageSeacrhTime/10 + " ms");
    }
    @Test
    void showAllChickenProducts() throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        String search = URLEncoder.encode(
                "Chicken_Breast",
                StandardCharsets.UTF_8
        );

        for (int page = 1; page <= 5; page++) {

            String url = "https://world.openfoodfacts.org/cgi/search.pl"
                    + "?search_terms=" + search
                    + "&search_simple=1"
                    + "&action=process"
                    + "&json=1"
                    + "&page=" + page
                    + "&page_size=20"
                    + "&fields=product_name,brands";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = null;

            // Prøv hver page op til 15 gange
            for (int attempt = 1; attempt <= 15; attempt++) {

                response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

                System.out.println(
                        "Page " + page
                                + " - forsøg " + attempt
                                + " - status " + response.statusCode()
                );

                if (response.statusCode() == 200) {
                    break;
                }

                Thread.sleep(500);
            }

            // Hvis page stadig fejler efter 15 forsøg
            if (response == null || response.statusCode() != 200) {

                System.out.println(
                        "Kunne ikke hente page " + page
                );

                continue;
            }

            JsonNode root = mapper.readTree(response.body());

            JsonNode products = root.path("products");

            System.out.println(
                    "\n===== PAGE " + page + " ====="
            );

            int number = 1;

            for (JsonNode product : products) {

                String name = product
                        .path("product_name")
                        .asText("");

                String brand = product
                        .path("brands")
                        .asText("");

                System.out.println(
                        number + ". " + name + " | " + brand
                );

                number++;
            }
        }
    }
}
