package com.dori.SpringStory.scripts.api;

public class testAPI {
    public static void main(String[] args) {
        ScriptApi cm = new ScriptApi();
        cm.say("Hello")
                .bold()
                .sayNext("next")
                .blue()
                .askYesNo("do you like?", (result) -> {
                    if (result) {
                        cm.addMenuItems(
                                cm.addMenuOption("first", () -> System.out.println("false")),
                                cm.addMenuOption("second", () -> System.out.println("false")),
                                cm.addMenuOption("third", () -> System.out.println("true"))
                        );
                        System.out.println("Yes!");
                    } else {
                        System.out.println("Lost!");
                    }
                });
        cm.applyAskResponseAction(true);

        ScriptApi cm2 = new ScriptApi();
        cm2.say("Hello")
                .bold()
                .sayNext("next")
                .addMenuItems(
                        cm2.addMenuOption("first", () -> System.out.println("false")),
                        cm2.addMenuOption("second", () -> System.out.println("false")),
                        cm2.addMenuOption("third", () -> System.out.println("true"))
                );

        ScriptApi cm3 = new ScriptApi();
        cm3.say("Hello")
                .bold()
                .sayNext("next")
                .blue()
                .askYesNo("do you like?", (result) -> {
                    if (result) {
                        cm3.sayNext("second step!")
                                .sayOK("DONE");
                    } else {
                        System.out.println("Lost!");
                    }
                });

        System.out.println("TEST");
    }
}
