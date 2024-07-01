package com.dori.SpringStory.scripts.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface QuestScript {
    int id() default -1;
    boolean start() default true;
}
