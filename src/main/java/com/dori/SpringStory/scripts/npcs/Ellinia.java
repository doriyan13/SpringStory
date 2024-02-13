package com.dori.SpringStory.scripts.npcs;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.dataHandlers.ItemDataHandler;
import com.dori.SpringStory.enums.InventoryType;
import com.dori.SpringStory.enums.Job;
import com.dori.SpringStory.enums.Stat;
import com.dori.SpringStory.inventory.Equip;
import com.dori.SpringStory.scripts.api.NpcScript;
import com.dori.SpringStory.scripts.api.ScriptApi;
import com.dori.SpringStory.utils.JobUtils;
import com.dori.SpringStory.utils.NpcMessageUtils;

import java.util.HashMap;
import java.util.Map;

public class Ellinia {

    // Grendel the Really Old
    // Not fully GMS-like
    @NpcScript(id = 1032001)
    public static ScriptApi handleMagicianJobInstructor(MapleChar chr) {
        ScriptApi script = new ScriptApi();

        // If the character is a beginner, move forward with the job advancement script
        if (chr.getJob() == Job.Beginner.getId()) {
            script.sayNext("Do you want to be a Magician? You need to meet some requirements in order to do so. ")
                    .addMsg("You need to be at least at")
                    .addNewLine("").blue("Level 8 ").addMsg(". Let's see if you have what it ")
                    .addMsg("takes to become a Magician.");

            // If the character is not yet level 8, they cannot become a magician
            if (chr.getLevel() < 8) {
                script.sayOK("You need more training to be a Magician. In order to be one, you need to train yourself to be more powerful than you are right now. Please come back when you are much stronger.");
                return script;
            }

            // Otherwise, let them become a Magician!
            script.askYesNo("You definitely have the look of a Magician. You may not be there just yet, but I can " +
                    "already see the Magician in you. What do you think? Do you want to become a Magician?", (yes) -> {
                if (yes) {
                    String npcName = NpcMessageUtils.npcName(1032001);
                    script.sayNext("You're now a Magician from here on out! It isn't much, but as the head Magician, I, "
                            + npcName + ", will give you a little bit of what I have...", () -> {

                        // If the player cannot hold the Beginner equipment, let them know and return
                        if (chr.getInventoryByType(InventoryType.EQUIP).isFull()) {
                            script.sayOK("Please make sure that you have an empty slot in your Equip. inventory and then talk to me again.");
                            return;
                        }

                        chr.setJob(Job.Magician.getId());

                        // Provide beginner equipment
                        Equip equip = ItemDataHandler.getEquipByID(1372043);
                        if (equip != null) {
                            // TODO: Have this show in chat box
                            chr.addEquip(equip);
                        }

                        Map<Stat, Object> statsToUpdate = new HashMap<>();

                        // Change stats to 4/4/20/4
                        chr.setNStr(4);
                        chr.setNDex(4);
                        chr.setNInt(20);
                        chr.setNLuk(4);
                        statsToUpdate.put(Stat.Str, 4);
                        statsToUpdate.put(Stat.Dex, 4);
                        statsToUpdate.put(Stat.Inte, 20);
                        statsToUpdate.put(Stat.Luk, 4);

                        // Provide them with their remaining AP to distribute themselves
                        // Characters should have 60 AP by level 8 (5 each level up + 25 base stats)
                        chr.setAp(28 + Math.max(0, (chr.getLevel() - 8) * 5));
                        statsToUpdate.put(Stat.AbilityPoint, chr.getAp());

                        // Provide them with their remaining SP to distribute themselves
                        // Characters should have 1 SP by level 8 (+3 per level beyond that)
                        chr.setSp(1 + Math.max(0, (chr.getLevel() - 8) * 3));
                        statsToUpdate.put(Stat.SkillPoint, chr.getSp());

                        chr.changeStats(statsToUpdate);

                        if (chr.getLevel() > 8) {
                            script.say("I think you are a bit late with making a job advancement. But don't worry, I have ")
                                    .addMsg("compensated you with additional Skill Points that you didn't receive by making the ")
                                    .addMsg("advancement so late.");
                        }
                        script.say("You have just equipped yourself with more magical power. Please continue training and ")
                                .addMsg("improving. I'll be watching you here and there.");
                        script.say("I just gave you a little bit of ").blue("SP").addMsg(". When you open up the ").blue("Skill menu").addMsg(" on the ")
                                .addMsg("lower right corner of the screen, there are skills you can learn by using your SP. One ")
                                .addMsg("warning, though: You can't raise them all at once. There are also skills you can ")
                                .addMsg("acquire only after having learned a couple of skills first.");
                        script.say("One more warning, though it's kind of obvious. Once you have chosen your job, try your ")
                                .addMsg("best to stay alive. Every death will cost you a certain amouint of experience points, ")
                                .addMsg("and you don't want to lose those, do you?");
                        script.say("Okay! This is all I can teach you. Go explore, train and better yourself. Find me when ")
                                .addMsg("you feel like you've done all you can. I'll be waiting for you.");
                        script.say("Oh, and... if you have any questions about being the Magician, feel free to ask. I don't ")
                                .addMsg("know EVERYTHING, per se, but I'll help you out with all that I know of. Til then... ")
                                .addMsg("farewell...");
                    });
                } else {
                    script.sayOK("Really? Have to give more thought to it, huh? Take your time, take your time. This is not something you should take lightly... come talk to me once you have made your decision.");
                }
            });
        }

        // If the character is a Magician of any type, send a generic message
        // TODO: Does GMS provide information on Magicians here?
        else if (JobUtils.isMagician(chr.getJob())) {
            // TODO: Add 2nd job handling
            script.sayOK("You have chosen wisely.");
        }

        // Otherwise, the player is not a beginner OR a Magician of any type. Send a generic message
        else {
            script.sayOK("Would you like to have the power of nature itself in your hands? It may be a long, hard " +
                    "road to be on, but you'll surely be rewarded in the end, reaching the very top of wizardry...");
        }

        return script;
    }
}
