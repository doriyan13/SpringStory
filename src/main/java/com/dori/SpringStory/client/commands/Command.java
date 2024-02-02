package com.dori.SpringStory.client.commands;

import com.dori.SpringStory.enums.AccountType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String[] names() default "";
    AccountType requiredPermission() default AccountType.Player;
}
