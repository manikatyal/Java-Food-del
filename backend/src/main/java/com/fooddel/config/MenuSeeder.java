package com.fooddel.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddel.model.Food;
import com.fooddel.repository.FoodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/**
 * On a fresh database the menu would be empty. This loads the 32 starter dishes from
 * seed/foods.json (photos are in uploads/) the first time the app starts.
 * It does nothing when the foods collection already has data. Turn off with SEED_MENU=false.
 */
@Component
public class MenuSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MenuSeeder.class);

    private final FoodRepository foods;
    private final ObjectMapper mapper;
    private final boolean enabled;

    public MenuSeeder(FoodRepository foods, ObjectMapper mapper, @Value("${app.seed-menu}") boolean enabled) {
        this.foods = foods;
        this.mapper = mapper;
        this.enabled = enabled;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }
        try {
            if (foods.count() > 0) {
                return;
            }
            try (InputStream in = new ClassPathResource("seed/foods.json").getInputStream()) {
                List<SeedFood> seed = mapper.readValue(in, new TypeReference<>() { });
                foods.saveAll(seed.stream().map(SeedFood::toFood).toList());
                log.info("Seeded {} menu items", seed.size());
            }
        } catch (Exception e) {
            // Never block startup because of the seed (for example, DB briefly unreachable).
            log.warn("Menu seed skipped: {}", e.getMessage());
        }
    }

    record SeedFood(String name, String description, double price, String category, String image) {
        Food toFood() {
            Food f = new Food();
            f.setName(name);
            f.setDescription(description);
            f.setPrice(price);
            f.setCategory(category);
            f.setImage(image);
            return f;
        }
    }
}
