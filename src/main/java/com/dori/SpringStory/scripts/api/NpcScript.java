package com.dori.SpringStory.scripts.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NpcScript {
    int id() default -1;
}
