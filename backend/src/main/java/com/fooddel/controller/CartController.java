package com.fooddel.controller;

import com.fooddel.model.User;
import com.fooddel.security.AuthInterceptor;
import com.fooddel.util.Api;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Cart lives inside the user document: users.cartData = {"<foodId>": qty}.
 * All routes need the "token" header (checked by AuthInterceptor).
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    // Food ids are 24-char hex ObjectIds. Blocks "." and "$" so a client cannot write other fields.
    private static final Pattern ITEM_ID = Pattern.compile("^[A-Za-z0-9_-]{1,64}$");

    private final MongoTemplate mongo;

    public CartController(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    /**
     * Body: {"itemId": "..."}. Adds 1.
     * Uses an atomic $inc, so two fast clicks never lose an update (no read-modify-write race).
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestAttribute(AuthInterceptor.USER_ID) String userId,
                                   @RequestBody(required = false) Map<String, Object> body) {
        String itemId = itemId(body);
        if (itemId == null) {
            return Api.fail("Error");
        }
        var result = mongo.updateFirst(
                Query.query(Criteria.where("_id").is(userId)),
                new Update().inc("cartData." + itemId, 1),
                User.class);
        return result.getMatchedCount() == 1 ? Api.ok("Added to cart") : Api.fail("Error");
    }

    /** Body: {"itemId": "..."}. Removes 1, never goes below 0. */
    @PostMapping("/remove")
    public Map<String, Object> remove(@RequestAttribute(AuthInterceptor.USER_ID) String userId,
                                      @RequestBody(required = false) Map<String, Object> body) {
        String itemId = itemId(body);
        if (itemId == null) {
            return Api.fail("Error");
        }
        if (!mongo.exists(Query.query(Criteria.where("_id").is(userId)), User.class)) {
            return Api.fail("Error");
        }
        // Only decrement when qty > 0, in one atomic step.
        mongo.updateFirst(
                Query.query(Criteria.where("_id").is(userId).and("cartData." + itemId).gt(0)),
                new Update().inc("cartData." + itemId, -1),
                User.class);
        return Api.ok("Removed From cart");
    }

    /** Returns {"success": true, "cartData": {"<foodId>": qty}}. */
    @PostMapping("/get")
    public Map<String, Object> get(@RequestAttribute(AuthInterceptor.USER_ID) String userId) {
        User user = mongo.findById(userId, User.class);
        if (user == null) {
            return Api.fail("Error");
        }
        Map<String, Object> cart = user.getCartData() == null ? new HashMap<>() : user.getCartData();
        return Api.ok("cartData", cart);
    }

    private static String itemId(Map<String, Object> body) {
        Object v = body == null ? null : body.get("itemId");
        if (v == null) {
            return null;
        }
        String id = String.valueOf(v);
        return ITEM_ID.matcher(id).matches() ? id : null;
    }
}
