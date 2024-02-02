package com.dori.SpringStory.dataHandlers.dataEntities;

import com.dori.SpringStory.enums.StringDataType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "stringData")

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StringData {
    @Id
    @Column(name = "id")
    private long id;
    private String name;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private StringDataType type;

    @Override
    public String toString() {
        return "| " + id + " | Name: " + name;
    }
}
