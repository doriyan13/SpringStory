package com.dori.SpringStory.utils.utilEntities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Triple<L, M, R> {
    private L left;
    private M middle;
    private R right;

}
