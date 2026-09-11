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

public class FoodAPI {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public FoodDTO searchFood(String search) {

        try {
            String encodedSearch = URLEncoder.encode(search, StandardCharsets.UTF_8);

            String url = "https://world.openfoodfacts.org/api/v2/search?search_terms="
                    + encodedSearch
                    + "&page_size=1";
                   // + "&fields=code,product_name,brands,quantity,image_front_small_url,nutriments";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = null;

            for (int i = 0; i < 10; i++) {
                try {
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        break;
                    }

                    System.out.println("API gav status " + response.statusCode() + ", prøver igen...");
                    Thread.sleep(1000);

                } catch (Exception e) {
                    System.out.println("Forbindelsesfejl, prøver igen...");
                    Thread.sleep(1000);
                }
            }

            if (response == null || response.statusCode() != 200) {
                throw new RuntimeException("Kunne ikke hente produktet.");
            }

            JsonNode root = mapper.readTree(response.body());
            JsonNode products = root.path("products");

                System.out.println(root.toPrettyString());
            if (products.isEmpty()) {
                return null; // eller throw new RuntimeException("Produkt ikke fundet");
            }

            // Mapper det første produkt direkte til FoodDTO
            return mapper.treeToValue(products.get(0), FoodDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("Fejl ved Open Food Facts API", e);
        }
    }
}