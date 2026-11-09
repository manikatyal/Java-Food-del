package com.fooddel.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fooddel.util.Api;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Same "orders" collection as before.
 * items and address are stored as loose JSON, exactly what the React app sends,
 * e.g. items = [{"_id": "...", "name": "Veg Salad", "price": 18, "quantity": 2, ...}].
 */
@Document(collection = "orders")
public class Order {

    public static final String DEFAULT_STATUS = "Food Processing";

    @Id
    @JsonProperty("_id")
    private String id;
    private String userId;
    private List<Map<String, Object>> items;
    private Double amount;
    private Map<String, Object> address;
    private String status = DEFAULT_STATUS;
    private Date date = new Date();
    private Boolean payment = false;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<Map<String, Object>> getItems() { return items; }
    public void setItems(List<Map<String, Object>> items) { this.items = items; }

    @JsonIgnore
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    @JsonGetter("amount")
    public Number getAmountJson() { return Api.compact(amount); }

    public Map<String, Object> getAddress() { return address; }
    public void setAddress(Map<String, Object> address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Boolean getPayment() { return payment; }
    public void setPayment(Boolean payment) { this.payment = payment; }
}
