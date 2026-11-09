package com.fooddel.controller;

import com.fooddel.model.Order;
import com.fooddel.model.User;
import com.fooddel.repository.OrderRepository;
import com.fooddel.security.AuthInterceptor;
import com.fooddel.util.Api;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    // Menu prices are in USD; Stripe charges INR. Flat demo rate of x80.
    private static final long USD_TO_INR = 80;
    private static final double DELIVERY_FEE_USD = 2;

    // Must match the <select> options in admin/src/pages/Orders/Orders.jsx.
    private static final Set<String> ALLOWED_STATUSES = Set.of(Order.DEFAULT_STATUS, "Out for Delivery", "Delivered");

    private final OrderRepository orders;
    private final MongoTemplate mongo;
    private final String frontendUrl;

    public OrderController(OrderRepository orders, MongoTemplate mongo,
                           @Value("${app.frontend-url}") String frontendUrl,
                           @Value("${app.stripe-secret-key}") String stripeKey) {
        this.orders = orders;
        this.mongo = mongo;
        this.frontendUrl = frontendUrl;
        Stripe.apiKey = stripeKey;
    }

    /**
     * Logged-in user places an order. Body: {"items": [...], "amount": 38, "address": {...}}.
     * Saves the order as unpaid, empties the cart, and returns a Stripe Checkout URL.
     */
    @PostMapping("/post")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> place(@RequestAttribute(AuthInterceptor.USER_ID) String userId,
                                                     @RequestBody(required = false) Map<String, Object> body) {
        Object items = body == null ? null : body.get("items");
        Object amount = body == null ? null : body.get("amount");
        Object address = body == null ? null : body.get("address");
        if (!(items instanceof List<?>) || !(amount instanceof Number n) || n.doubleValue() == 0
                || !(address instanceof Map<?, ?>)) {
            return ResponseEntity.badRequest().body(Api.fail("All fields are required"));
        }

        try {
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) items;
            SessionCreateParams.Builder params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT);
            for (Map<String, Object> item : itemList) {
                params.addLineItem(lineItem(String.valueOf(item.get("name")),
                        toDouble(item.get("price")), toLong(item.get("quantity"))));
            }
            params.addLineItem(lineItem("Delivery Charges", DELIVERY_FEE_USD, 1));

            Order order = new Order();
            order.setUserId(userId);
            order.setItems(itemList);
            order.setAmount(n.doubleValue());
            order.setAddress((Map<String, Object>) address);
            order.setDate(new Date());
            order = orders.save(order);

            mongo.updateFirst(Query.query(Criteria.where("_id").is(userId)),
                    new Update().set("cartData", new HashMap<>()), User.class);

            Session session = Session.create(params
                    .setSuccessUrl(frontendUrl + "/verify?success=true&orderId=" + order.getId())
                    .setCancelUrl(frontendUrl + "/verify?success=false&orderId=" + order.getId())
                    .build());
            return ResponseEntity.ok(Api.ok("session_url", session.getUrl()));
        } catch (StripeException | RuntimeException e) {
            log.error("place order failed", e);
            return ResponseEntity.internalServerError().body(Api.fail("Error processing order"));
        }
    }

    /**
     * Stripe sends the user back to /verify?success=...&orderId=..., and the frontend posts that here.
     * success=true marks it paid; anything else deletes the unpaid order.
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody(required = false) Map<String, Object> body) {
        Object orderId = body == null ? null : body.get("orderId");
        Object success = body == null ? null : body.get("success");
        if (orderId == null) {
            return ResponseEntity.status(404).body(Api.fail("Order not found"));
        }
        String id = String.valueOf(orderId);
        if ("true".equals(String.valueOf(success))) {
            var result = mongo.updateFirst(Query.query(Criteria.where("_id").is(id)),
                    new Update().set("payment", true), Order.class);
            if (result.getMatchedCount() == 0) {
                return ResponseEntity.status(404).body(Api.fail("Order not found"));
            }
            return ResponseEntity.ok(Api.ok("Paid"));
        }
        Order removed = mongo.findAndRemove(Query.query(Criteria.where("_id").is(id)), Order.class);
        if (removed == null) {
            return ResponseEntity.status(404).body(Api.fail("Order not found"));
        }
        return ResponseEntity.ok(Api.fail("Not Paid"));
    }

    /** Logged-in user's own orders. */
    @PostMapping("/userorders")
    public Map<String, Object> userOrders(@RequestAttribute(AuthInterceptor.USER_ID) String userId) {
        return Api.ok("data", orders.findByUserId(userId));
    }

    /** Admin panel: every order. */
    @GetMapping("/list")
    public Map<String, Object> list() {
        return Api.ok("data", orders.findAll());
    }

    /** Admin panel. Body: {"orderId": "...", "status": "Out for delivery"}. */
    @PostMapping("/status")
    public Map<String, Object> status(@RequestBody(required = false) Map<String, Object> body) {
        Object orderId = body == null ? null : body.get("orderId");
        Object status = body == null ? null : body.get("status");
        if (orderId == null || status == null || !ALLOWED_STATUSES.contains(String.valueOf(status))) {
            return Api.fail("Error");
        }
        mongo.updateFirst(Query.query(Criteria.where("_id").is(String.valueOf(orderId))),
                new Update().set("status", String.valueOf(status)), Order.class);
        return Api.ok("Status Updated");
    }

    private static SessionCreateParams.LineItem lineItem(String name, double priceUsd, long quantity) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(quantity)
                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("inr")
                        .setUnitAmount(Math.round(priceUsd * 100 * USD_TO_INR)) // Stripe wants paise
                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(name)
                                .build())
                        .build())
                .build();
    }

    private static double toDouble(Object v) {
        if (v instanceof Number n) {
            return n.doubleValue();
        }
        return Double.parseDouble(String.valueOf(v));
    }

    private static long toLong(Object v) {
        if (v instanceof Number n) {
            return n.longValue();
        }
        return Long.parseLong(String.valueOf(v));
    }
}
