package com.dori.SpringStory.wzHandlers.wzEntities;

import com.dori.SpringStory.enums.StringDataType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

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
}
