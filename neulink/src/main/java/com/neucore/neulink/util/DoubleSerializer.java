package com.neucore.neulink.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public class DoubleSerializer implements JsonSerializer<Double> {
    private int decimal;
    public DoubleSerializer(int decimal){
        this.decimal = decimal;
    }
    @Override
    public JsonElement serialize(Double value, Type typeOfSrc, JsonSerializationContext context) {
        if (value.isNaN()) {
            return new JsonPrimitive(0); // Convert NaN to zero
        } else if (value.isInfinite() || value.doubleValue()<0.01) {
            return new JsonPrimitive(value); // Leave small numbers and infinite alone
        } else {
            // Keep 2 decimal digits only
            return new JsonPrimitive(new BigDecimal(value).setScale(decimal,BigDecimal.ROUND_HALF_UP));
        }
    }
}
