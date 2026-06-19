package org.example.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Data
@Setter
@Getter
public class CategoryConfig {

    @Id
    private String category;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> attributes;

}