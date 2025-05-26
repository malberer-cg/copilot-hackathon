package com.example.library.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class SQLiteLocalDateTimeConverterTest {

    private final SQLiteLocalDateTimeConverter converter = new SQLiteLocalDateTimeConverter();

    @Test
    void shouldConvertLocalDateTimeToTimestamp() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2025, 5, 26, 12, 30);
        
        // When
        Long timestamp = converter.convertToDatabaseColumn(dateTime);
        
        // Then
        assertThat(timestamp).isNotNull();
        LocalDateTime convertedBack = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(timestamp), 
            ZoneId.systemDefault()
        );
        assertThat(convertedBack).isEqualToIgnoringNanos(dateTime);
    }

    @Test
    void shouldHandleNullLocalDateTime() {
        // When
        Long timestamp = converter.convertToDatabaseColumn(null);
        
        // Then
        assertThat(timestamp).isNull();
    }

    @Test
    void shouldConvertTimestampToLocalDateTime() {
        // Given
        LocalDateTime originalDateTime = LocalDateTime.of(2025, 5, 26, 12, 30);
        Long timestamp = originalDateTime.atZone(ZoneId.systemDefault())
            .toInstant().toEpochMilli();
        
        // When
        LocalDateTime dateTime = converter.convertToEntityAttribute(timestamp);
        
        // Then
        assertThat(dateTime).isNotNull();
        assertThat(dateTime).isEqualToIgnoringNanos(originalDateTime);
    }

    @Test
    void shouldHandleNullTimestamp() {
        // When
        LocalDateTime dateTime = converter.convertToEntityAttribute(null);
        
        // Then
        assertThat(dateTime).isNull();
    }
}
