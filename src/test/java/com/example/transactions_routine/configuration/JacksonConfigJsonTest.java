package com.example.transactions_routine.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@DisplayName("Jackson Configuration JSON Tests")
@Import(JacksonConfig.class)
class JacksonConfigJsonTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    private JacksonTester<SampleRecord> json;

    @Autowired
    private ObjectMapper objectMapper;

    // SampleRecord to use across tests
    record SampleRecord(
            UUID id,
            String name,
            Integer age,
            LocalDateTime createdAt,
            Optional<String> note
    ) {
    }

    @Test
    @DisplayName("Should use snake_case naming strategy")
    void shouldUseSnakeCaseNamingStrategy() {
        assertEquals(PropertyNamingStrategies.SNAKE_CASE, objectMapper.getPropertyNamingStrategy());
    }

    @Test
    @DisplayName("Should have the correct Jackson configuration")
    void shouldHaveCorrectObjectMapperConfiguration() {
        assertFalse(objectMapper.getDeserializationConfig().isEnabled(
                        com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES),
                "FAIL_ON_UNKNOWN_PROPERTIES should be false");

        assertFalse(objectMapper.getSerializationConfig().isEnabled(
                        SerializationFeature.FAIL_ON_EMPTY_BEANS),
                "FAIL_ON_EMPTY_BEANS should be false");

        assertFalse(objectMapper.getSerializationConfig().isEnabled(
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS),
                "WRITE_DATES_AS_TIMESTAMPS should be false (use ISO format)");

        assertEquals(PropertyNamingStrategies.SNAKE_CASE,
                objectMapper.getPropertyNamingStrategy(),
                "Naming strategy should be SNAKE_CASE");
    }

    @Test
    @DisplayName("Should serialize and deserialize SampleRecord according to Jackson configuration")
    void shouldSerializeAndDeserializeSampleRecord() throws Exception {
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        Integer age = 30;
        LocalDateTime createdAt = LocalDateTime.of(2024, 6, 30, 10, 0);
        Optional<String> note = Optional.of("This is a note");

        SampleRecord sampleRecord = new SampleRecord(id, name, age, createdAt, note);

        String json = objectMapper.writeValueAsString(sampleRecord);
        JsonNode jsonNode = objectMapper.readTree(json);

        assertEquals(id.toString(), jsonNode.get("id").asText());
        assertEquals(name, jsonNode.get("name").asText());
        assertEquals(age.intValue(), jsonNode.get("age").asInt());
        assertEquals(
                createdAt.format(DATE_TIME_FORMATTER),
                jsonNode.get("created_at").asText()
        );
        assertEquals(note.get(), jsonNode.get("note").asText());

        SampleRecord deserialized = objectMapper.readValue(json, SampleRecord.class);

        assertEquals(id, deserialized.id());
        assertEquals(name, deserialized.name());
        assertEquals(age, deserialized.age());
        assertEquals(createdAt, deserialized.createdAt());
        assertEquals(note, deserialized.note());
    }

    @Test
    @DisplayName("Should ignore unknown properties on deserialization")
    void shouldIgnoreUnknownPropertiesOnDeserialization() throws Exception {
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        Integer age = 30;
        String createdAtString = "2024-06-30T10:00:00";
        LocalDateTime createdAt = LocalDateTime.of(2024, 6, 30, 10, 0);
        Optional<String> note = Optional.of("This is a note");

        String jsonWithExtraFields = String.format("""
                {
                    "id": "%s",
                    "name": "%s",
                    "age": %d,
                    "created_at": "%s",
                    "note": "%s",
                    "unknown_field1": "should be ignored",
                    "unknown_field2": "should be ignored too"
                }
                """, id, name, age, createdAtString, note.get());

        SampleRecord sampleRecord = objectMapper.readValue(jsonWithExtraFields, SampleRecord.class);

        assertEquals(id, sampleRecord.id());
        assertEquals(name, sampleRecord.name());
        assertEquals(age, sampleRecord.age());
        assertEquals(createdAt, sampleRecord.createdAt());
        assertEquals(note, sampleRecord.note());
    }

    @Test
    @DisplayName("Should ignore NULL fields on serialization")
    void shouldIgnoreNullFieldsOnSerialization() throws Exception {
        UUID id = UUID.randomUUID();
        String name = "Jane";
        Integer age = null;
        LocalDateTime createdAt = LocalDateTime.of(2024, 6, 30, 10, 0);
        Optional<String> note = null;

        SampleRecord sampleRecord = new SampleRecord(id, name, age, createdAt, note);

        String json = objectMapper.writeValueAsString(sampleRecord);
        JsonNode jsonNode = objectMapper.readTree(json);

        assertFalse(jsonNode.has("age"));
        assertFalse(jsonNode.has("note"));

        assertEquals(id.toString(), jsonNode.get("id").asText());
        assertEquals(name, jsonNode.get("name").asText());
        assertEquals(
                createdAt.format(DATE_TIME_FORMATTER),
                jsonNode.get("created_at").asText()
        );
    }

    @Test
    @DisplayName("Should ignore unknown and null fields together on serialization and deserialization")
    void shouldHandleUnknownAndNullFieldsTogether() throws Exception {
        String json = """
                {
                    "id": "e7b8e5b2-8f70-4c88-9e94-dc88c93915d9",
                    "name": "John Doe",
                    "created_at": "2024-06-30T10:00:00",
                    "unknown_field_1": "should be ignored",
                    "unknown_field_2": "ignored too"
                }
                """;

        UUID expectedId = UUID.fromString("e7b8e5b2-8f70-4c88-9e94-dc88c93915d9");
        LocalDateTime expectedCreatedAt = LocalDateTime.of(2024, 6, 30, 10, 0);

        SampleRecord sampleRecord = objectMapper.readValue(json, SampleRecord.class);

        assertEquals(expectedId, sampleRecord.id());
        assertEquals("John Doe", sampleRecord.name());
        assertEquals(expectedCreatedAt, sampleRecord.createdAt());

        assertNull(sampleRecord.age());
        assertEquals(Optional.empty(), sampleRecord.note());

        String serialized = objectMapper.writeValueAsString(sampleRecord);
        JsonNode jsonNode = objectMapper.readTree(serialized);

        assertFalse(jsonNode.has("age"));
        assertTrue(jsonNode.has("note"));
        assertTrue(jsonNode.get("note").isNull());

        assertFalse(jsonNode.has("unknown_field_1"));
        assertFalse(jsonNode.has("unknown_field_2"));

        assertEquals(expectedId.toString(), jsonNode.get("id").asText());
        assertEquals("John Doe", jsonNode.get("name").asText());
        assertEquals(
                expectedCreatedAt.format(DATE_TIME_FORMATTER),
                jsonNode.get("created_at").asText()
        );
    }

    @Test
    @DisplayName("Should handle Optional.empty as null on serialization")
    void shouldHandleOptionalEmptyAsNull() throws Exception {
        UUID id = UUID.randomUUID();
        String name = "Jane";
        Integer age = 25;
        LocalDateTime createdAt = LocalDateTime.of(2024, 6, 30, 10, 0);
        Optional<String> note = Optional.empty();

        SampleRecord sampleRecord = new SampleRecord(id, name, age, createdAt, note);

        String json = objectMapper.writeValueAsString(sampleRecord);
        JsonNode node = objectMapper.readTree(json);

        assertTrue(node.has("note"));
        assertTrue(node.get("note").isNull());
    }

    @Test
    @DisplayName("Should NOT fail on empty beans serialization")
    void shouldNotFailOnEmptyBeans() throws JsonProcessingException {
        class Empty {
        }

        String json = objectMapper.writeValueAsString(new Empty());

        assertEquals("{}", json);
    }

    @Test
    @DisplayName("Should serialize LocalDateTime in ISO 8601 format")
    void shouldSerializeDateInISO8601Format() throws Exception {
        LocalDateTime date = LocalDateTime.of(2024, 6, 30, 10, 0);

        String json = objectMapper.writeValueAsString(date);

        assertEquals("\"2024-06-30T10:00:00\"", json);
    }

    @Test
    @DisplayName("Should serialize LocalDateTime with milliseconds")
    void shouldSerializeDateWithMilliseconds() throws Exception {
        LocalDateTime date = LocalDateTime.of(2024, 6, 30, 10, 0, 0, 123_000_000); // 123ms

        String json = objectMapper.writeValueAsString(date);

        assertEquals("\"2024-06-30T10:00:00.123\"", json);
    }

    @Test
    @DisplayName("Should serialize and deserialize Optional correctly")
    void shouldHandleOptionalCorrectly() throws Exception {
        record Wrapper(Optional<String> phone) {
        }

        Wrapper withValue = new Wrapper(Optional.of("12345"));
        String json = objectMapper.writeValueAsString(withValue);
        JsonNode node = objectMapper.readTree(json);

        assertTrue(node.has("phone"));
        assertEquals("12345", node.get("phone").asText());

        Wrapper empty = new Wrapper(Optional.empty());
        String emptyJson = objectMapper.writeValueAsString(empty);
        JsonNode emptyNode = objectMapper.readTree(emptyJson);

        assertTrue(emptyNode.has("phone"));
        assertTrue(emptyNode.get("phone").isNull());
    }
}
