package org.example.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JsonSchemaValidationService {

    private final ObjectMapper objectMapper;

    public JsonSchemaValidationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private JsonSchema loadSchema(String schemaFileName) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        InputStream schemaStream = getClass().getResourceAsStream("/" + schemaFileName);
        if (schemaStream == null) {
            throw new IllegalArgumentException("Nie znaleziono pliku schematu: " + schemaFileName);
        }
        return factory.getSchema(schemaStream);
    }

    public Set<String> validateAttributes(Map<String, Object> attributes, String schemaFileName){
        JsonSchema schema = loadSchema(schemaFileName);
        JsonNode jsonNode = objectMapper.valueToTree(attributes);
        Set<ValidationMessage> errors = schema.validate(jsonNode);

        return errors.stream().map(ValidationMessage::getMessage).collect(Collectors.toSet());
    }
}