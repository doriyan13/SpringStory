package com.dori.SpringStory.connection.packet;

import com.dori.SpringStory.connection.packet.headers.InHeader;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {

    InHeader op() default InHeader.NO;

    InHeader[] ops() default {};
}
