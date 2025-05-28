package com.mateusz113.order_service_adapters.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class FixedPrecisionBigDecimalSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            BigDecimal formatted = value.setScale(2, RoundingMode.HALF_UP);
            gen.writeNumber(formatted.toPlainString());
        } else {
            gen.writeNull();
        }
    }
}
