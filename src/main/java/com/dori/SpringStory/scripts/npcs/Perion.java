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

public class Perion {


    // Dances with Balrog
    // Not fully GMS-like
    @NpcScript(id = 1022000)
    public static ScriptApi handleWarriorJobInstructor(MapleChar chr) {
        ScriptApi script = new ScriptApi();

        // If the character is a beginner, move forward with the job advancement script
        if (chr.getJob() == Job.Beginner.getId()) {
            script.sayNext("Do you wish to become a Warrior? You need to meet some criteria in order to do so. ")
                    .blue("You should be at least Lv. 10")
                    .addMsg(". Let's see...");

            // If the character is not yet level 10, they cannot become a warrior
            if (chr.getLevel() < 10) {
                script.sayOK("You need more training to be a Warrior. In order to be one, you need to train yourself to be more powerful than you are right now. Please come back when you are much stronger.");
                return script;
            }

            // Otherwise, let them become a Warrior!
            script.askYesNo("You definitely have the look of a Warrior. You may not be there just yet, but I can already see the Warrior in you. What do you think? Do you want to become a Warrior?",
                    (yes) -> {
                        if (yes) {
                            script.sayNext("From now on, you are going to be a Warrior! Please persist in your discipline. I'll enhance your abilities in hopes that you'll train to be even stronger than you are now.", () -> {

                                // If the player cannot hold the Beginner equipment, let them know and return
                                if (chr.getInventoryByType(InventoryType.EQUIP).isFull()) {
                                    script.sayOK("Please make sure that you have an empty slot in your Equip. inventory and then talk to me again.");
                                    return;
                                }

                                chr.setJob(Job.Warrior.getId());

                                // Provide beginner equipment
                                Equip equip = ItemDataHandler.getEquipByID(1302077);
                                if (equip != null) {
                                    // TODO: Have this show in chat box
                                    chr.addEquip(equip);
                                }

                                Map<Stat, Object> statsToUpdate = new HashMap<>();

                                // Change stats to 35/4/4/4
                                chr.setNStr(35);
                                chr.setNDex(4);
                                chr.setNInt(4);
                                chr.setNLuk(4);
                                statsToUpdate.put(Stat.Str, 35);
                                statsToUpdate.put(Stat.Dex, 4);
                                statsToUpdate.put(Stat.Inte, 4);
                                statsToUpdate.put(Stat.Luk, 4);

                                // Provide them with their remaining AP to distribute themselves
                                // Characters should have 70 AP by level 10 (5 each level up + 25 base stats)
                                chr.setAp(23 + Math.max(0, (chr.getLevel() - 10) * 5));
                                statsToUpdate.put(Stat.AbilityPoint, chr.getAp());

                                // Provide them with their remaining SP to distribute themselves
                                // Characters should have 1 SP by level 10 (+3 per level beyond that)
                                chr.setSp(1 + Math.max(0, (chr.getLevel() - 10) * 3));
                                statsToUpdate.put(Stat.SkillPoint, chr.getSp());

                                chr.changeStats(statsToUpdate);

                                if (chr.getLevel() > 10) {
                                    script.say("I think you are a bit late with making a job advancement. But don't worry, I have ")
                                            .addMsg("compensated you with additional Skill Points that you didn't receive by making the ")
                                            .addMsg("advancement so late.");
                                }
                                script.say("You've gotten much stronger now. Plus, every single one of your inventories have added slots--a whole row, to be exact. See for yourself, I just gave you a little bit of ")
                                        .blue("SP")
                                        .addMsg(". When you open up the ")
                                        .blue("Skill menu")
                                        .addMsg(" on the lower right corner of the screen, there are skills you can learn by using SP. One warning, though, you can't raise it altogether all at once. There are also skills you can acquire only after having learned a couple of skills first.");
                                script.say("One more warning, though it's kind of obvious. Once you have chosen your job, try your ")
                                        .addMsg("best to stay alive. Every death will cost you a certain amount of experience points, ")
                                        .addMsg("and you don't want to lose those, do you?");
                            });
                        } else {
                            script.sayOK("Really? Do you need more time to think about it? By all means... this is not something you should take lightly. Come talk to me once your have made your decision.");
                        }
                    });
        }

        // If the character is a Warrior of any type, send a generic message
        // TODO: Does GMS provide information on Warriors here?
        else if (JobUtils.isWarrior(chr.getJob())) {
            // TODO: Add 2nd job handling
            script.sayOK("You have chosen wisely.");
        }

        // Otherwise, the player is not a beginner OR a Warrior of any type. Send a generic message
        else {
            script.sayOK("Awesome body! Awesome power! Warriors are the way to go! What do you think? How about making the job advancement as a Warrior?");
        }

        return script;
    }
}
