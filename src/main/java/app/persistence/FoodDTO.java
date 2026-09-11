package app.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodDTO {
    @JsonProperty("code")
    private String barcode;

    @JsonProperty("product_name")
    private String name;

    @JsonProperty("brands")
    private String brand;

    @JsonProperty("quantity")
    private String quantity;

    @JsonProperty("image_front_small_url")
    private String imageUrl;

    @JsonProperty("nutriments")
    private Nutriments nutriments;

    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Nutriments {
        @JsonProperty("energy-kcal_100g")
        private Double energyKcal;

        @JsonProperty("proteins_100g")
        private Double protein;

        @JsonProperty("fat_100g")
        private Double fat;

        @JsonProperty("saturated-fat_100g")
        private Double saturatedFat;

        @JsonProperty("carbohydrates_100g")
        private Double carbohydrates;

        @JsonProperty("sugars_100g")
        private Double sugars;

        @JsonProperty("fiber_100g")
        private Double fiber;

        @JsonProperty("salt_100g")
        private Double salt;

        @JsonProperty("sodium_100g")
        private Double sodium;

        public double getOrZero(Double value) {
            if (value != null) {
                return value;
            } else {
                return 0.0;
            }
        }
    }

}
