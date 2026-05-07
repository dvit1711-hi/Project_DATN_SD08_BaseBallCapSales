package com.example.project_datn_sd08_baseballcapsales.Model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Locale;

@Converter(autoApply = false)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {

    @Override
    public String convertToDatabaseColumn(OrderStatus attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        String normalized = normalizeEnumValue(dbData);
        try {
            return OrderStatus.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return Arrays.stream(OrderStatus.values())
                    .filter(value -> normalizeEnumValue(value.name()).equals(normalized))
                    .findFirst()
                    .orElse(null);
        }
    }

    private String normalizeEnumValue(String value) {
        return value == null
                ? null
                : value.trim()
                       .replaceAll("[^A-Za-z0-9]+", "_")
                       .toUpperCase(Locale.ROOT);
    }
}
