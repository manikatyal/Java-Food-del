package com.fooddel.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SafeFileNameTest {

    @Test
    void keepsNormalNamesAndBlocksTraversal() {
        assertEquals("food_2.png", FoodController.safeFileName("food_2.png"));
        assertEquals("food_2.png", FoodController.safeFileName("food 2.png"));
        assertEquals("evil.png", FoodController.safeFileName("../../etc/evil.png"));
        assertEquals("evil.png", FoodController.safeFileName("..\\..\\evil.png"));
        assertEquals("image.htaccess", FoodController.safeFileName(".htaccess"));
        assertEquals("image", FoodController.safeFileName(null));
    }
}
