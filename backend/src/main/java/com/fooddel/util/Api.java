package com.fooddel.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds the {"success": ..., ...} JSON bodies the React apps expect.
 */
public final class Api {

    private Api() {
    }

    public static Map<String, Object> ok(String key, Object value) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put(key, value);
        return body;
    }

    public static Map<String, Object> ok(String message) {
        return ok("message", message);
    }

    public static Map<String, Object> fail(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("message", message);
        return body;
    }

    /** JSON numbers: 18.0 is written as 18, so "$18.00" in the UI does not become "$18.0.00". */
    public static Number compact(Double value) {
        if (value == null) {
            return null;
        }
        if (value == Math.rint(value) && !Double.isInfinite(value) && Math.abs(value) < 1e15) {
            return value.longValue();
        }
        return value;
    }
}
