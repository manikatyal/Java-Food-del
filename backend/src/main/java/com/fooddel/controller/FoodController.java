package com.fooddel.controller;

import com.fooddel.model.Food;
import com.fooddel.repository.FoodRepository;
import com.fooddel.util.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/food")
public class FoodController {

    private static final Logger log = LoggerFactory.getLogger(FoodController.class);

    private final FoodRepository foods;
    private final Path uploadDir;

    public FoodController(FoodRepository foods, Path uploadDir) {
        this.foods = foods;
        this.uploadDir = uploadDir;
    }

    /** Admin panel: multipart form with name, description, price, category, image. */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String description,
                                   @RequestParam(required = false) String price,
                                   @RequestParam(required = false) String category,
                                   @RequestParam(required = false) MultipartFile image) {
        try {
            if (image == null || image.isEmpty() || isBlank(name) || isBlank(description)
                    || isBlank(price) || isBlank(category)) {
                return Api.fail("Error");
            }
            double parsedPrice = Double.parseDouble(price.trim());

            // "<epoch millis><original name>", so names never clash.
            String fileName = System.currentTimeMillis() + safeFileName(image.getOriginalFilename());
            Files.createDirectories(uploadDir);
            try (InputStream in = image.getInputStream()) {
                Files.copy(in, uploadDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            }

            Food food = new Food();
            food.setName(name);
            food.setDescription(description);
            food.setPrice(parsedPrice);
            food.setCategory(category);
            food.setImage(fileName);
            foods.save(food);
            return Api.ok("food added");
        } catch (NumberFormatException | IOException e) {
            log.error("add food failed", e);
            return Api.fail("Error");
        }
    }

    @GetMapping("/list")
    public Map<String, Object> list() {
        return Api.ok("data", foods.findAll());
    }

    /** Body: {"name": "salad"}. Case-insensitive contains match. */
    @PostMapping("/search")
    public Map<String, Object> search(@RequestBody(required = false) Map<String, Object> body) {
        Object name = body == null ? null : body.get("name");
        // Pattern.quote: user text is matched literally, so input like "(a+)+" cannot hang the DB.
        String regex = Pattern.quote(name == null ? "" : String.valueOf(name));
        return Api.ok("data", foods.searchByName(regex));
    }

    /** Body: {"id": "<foodId>"}. Deletes the DB row and its image file. */
    @PostMapping("/remove")
    public Map<String, Object> remove(@RequestBody(required = false) Map<String, Object> body) {
        Object id = body == null ? null : body.get("id");
        if (id == null) {
            return Api.fail("Error");
        }
        Food food = foods.findById(String.valueOf(id)).orElse(null);
        if (food == null) {
            return Api.fail("Error");
        }
        deleteImageQuietly(food.getImage());
        foods.deleteById(food.getId());
        return Api.ok("Food Removed");
    }

    private void deleteImageQuietly(String image) {
        if (isBlank(image)) {
            return;
        }
        Path file = uploadDir.resolve(image).normalize();
        if (!file.startsWith(uploadDir)) {
            return; // never delete outside the upload folder
        }
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("could not delete image {}", file, e);
        }
    }

    /** Keeps only the last path part and safe characters, so "../../x.png" cannot escape the folder. */
    static String safeFileName(String original) {
        if (original == null) {
            return "image";
        }
        String base = original.replace('\\', '/');
        base = base.substring(base.lastIndexOf('/') + 1);
        base = base.replaceAll("[^A-Za-z0-9._-]", "_");
        return base.isEmpty() || base.startsWith(".") ? "image" + base : base;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
