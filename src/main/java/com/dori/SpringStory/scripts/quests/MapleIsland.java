package com.dori.SpringStory.scripts.quests;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.client.effects.parsers.AvatarOrientedEffect;
import com.dori.SpringStory.connection.packet.packets.CUserLocal;
import com.dori.SpringStory.enums.CharacterGender;
import com.dori.SpringStory.enums.UserEffectTypes;
import com.dori.SpringStory.scripts.api.QuestScript;
import com.dori.SpringStory.scripts.api.ScriptApi;
import com.dori.SpringStory.utils.NpcMessageUtils;

public class MapleIsland {

    private static final int ROGERS_APPLE = 2010007;

    @QuestScript(id = 1021)
    public static ScriptApi rogersAppleStart(MapleChar chr) {
        ScriptApi script = new ScriptApi();
        script.sayNext("Hey, " + (chr.getGender() == CharacterGender.Boy ? "Man " : "Miss ")
                + "~ What's up? Haha! I am Roger who teaches you new travellers with lots of information.")
                .addMsg("You are asking who made me do this? Ahahahaha! Myself! I wanted to do this and just be kind to you new travellers.")
                .askYesNo("So..... Let me just do this for fun! Abaracadabra~!", (answer) -> {
                    if (answer) {
                        chr.modifyHp(-25);
                        if (!chr.hasItem(ROGERS_APPLE,1)) {
                            chr.addItem(ROGERS_APPLE,1);
                            chr.forceStartQuest(1021);
                            script.sayNext("Surprised? If HP becomes 0, then you are in trouble. Now, I will give you  #rRoger's Apple#k. Please take it. You will feel stronger. Open the item window and double click to consume. Hey, It's very simple to open the item window. Just press #bI#k on your keyboard.")
                                    .addMsg("Please take all Roger's Apples that I gave you. You will be able to see the HP bar increasing right away. Please talk to me again when you recover your HP 100%.")
                                    ;
                            chr.write(CUserLocal.effect(UserEffectTypes.AvatarOriented,new AvatarOrientedEffect("UI/tutorial.img/28")));
                        }
                    } else {
                        chr.enableAction();
                    }
                });
        ;
        return script;
    }

    @QuestScript(id = 1021, start = false)
    public static ScriptApi rogersAppleEnd(MapleChar chr) {
        ScriptApi script = new ScriptApi();
        if (chr.getHp() < 50) {
            script.sayOK("Hey, your HP is not fully recovered yet. Did you take all the Roger's Apple that I gave you? Are you sure?");
        } else {
            script.sayNext("How easy is it to consume the item? Simple, right? You can set a #bhotkey#k on the right bottom slot. Haha you didn't know that! right? Oh, and if you are a beginner, HP will automatically recover itself as time goes by. Well it takes time but this is one of the strategies for the beginners.")
                    .sayNext("Alright! Now that you have learned alot, I will give you a present. This is a must for your travel in Maple World, so thank me! Please use this under emergency cases!")
                    .say("Okay, this is all I can teach you. I know it's sad but it is time to say good bye. Well take care of yourself and Good luck my friend!")
                    .addNewLine(NpcMessageUtils.wzImage("UI/UIWindow.img/QuestIcon/4/0"))
                    .addNewLine(NpcMessageUtils.itemImage(2010000)).addMsg(" 3 Apple")
                    .addNewLine(NpcMessageUtils.itemImage(2010009)).addMsg(" 3 Green Apple")
                    .addNewLine(NpcMessageUtils.wzImage("UI/UIWindow.img/QuestIcon/8/0")).addMsg("10 exp")
            ;
            chr.forceCompleteQuest(1021);
            chr.addItem(2010000, 3);
            chr.addItem(2010009, 3);
            chr.gainExp(10);
        }

        return script;
    }

}
