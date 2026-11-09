package com.fooddel.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fooddel.util.Api;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** A menu item, stored in the "foods" collection. */
@Document(collection = "foods")
public class Food {

    @Id
    @JsonProperty("_id")
    private String id;
    private String name;
    private String description;
    private Double price;
    private String image;
    private String category;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @JsonIgnore
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    @JsonGetter("price")
    public Number getPriceJson() { return Api.compact(price); }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
