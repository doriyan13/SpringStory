package com.dori.SpringStory.scripts.handlers;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.packets.CScriptMan;
import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.scripts.api.NpcMessage;
import com.dori.SpringStory.scripts.api.NpcScript;
import com.dori.SpringStory.scripts.api.QuestScript;
import com.dori.SpringStory.scripts.api.ScriptApi;
import com.dori.SpringStory.utils.MapleUtils;
import com.dori.SpringStory.utils.ScriptUtils;
import com.dori.SpringStory.world.fieldEntities.Npc;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
public class ScriptHandler {
    private static ScriptHandler instance;
    private static final Logger logger = new Logger(ScriptHandler.class);
    private static final Map<Integer, Method> npcScripts = new HashMap<>();
    private static final Map<String, Method> questScripts = new HashMap<>();

    public static ScriptHandler getInstance() {
        if (instance == null) {
            instance = new ScriptHandler();
        }
        return instance;
    }

    private static void initNpcScripts() {
        long start = System.currentTimeMillis();
        String handlersDir = ServerConstants.NPC_SCRIPTS_DIR;
        Set<File> files = new HashSet<>();
        MapleUtils.findAllFilesInDirectory(files, new File(handlersDir));
        for (File file : files) {
            try {
                // grab all files in the NPCs scripts dir, strip them to their package name, and remove .java extension -
                String className = ScriptUtils.getClassName(file);
                Class<?> clazz = Class.forName(className);
                for (Method method : clazz.getMethods()) {
                    NpcScript npcScript = method.getAnnotation(NpcScript.class);
                    if (npcScript != null) {
                        int npcID = npcScript.id();
                        if (npcID == -1) {
                            logger.error("Found unMarked script! in the class - " + className);
                        } else {
                            npcScripts.put(npcID, method);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        logger.serverNotice("Initialized " + npcScripts.size() + " NPC scripts in " + (System.currentTimeMillis() - start) + "ms.");
    }

    private static void initQuestScripts() {
        long start = System.currentTimeMillis();
        String handlersDir = ServerConstants.QUEST_SCRIPTS_DIR;
        Set<File> files = new HashSet<>();
        MapleUtils.findAllFilesInDirectory(files, new File(handlersDir));
        for (File file : files) {
            try {
                // grab all files in the Quests scripts dir, strip them to their package name, and remove .java extension -
                String className = ScriptUtils.getClassName(file);
                Class<?> clazz = Class.forName(className);
                for (Method method : clazz.getMethods()) {
                    QuestScript questScript = method.getAnnotation(QuestScript.class);
                    if (questScript != null) {
                        int questID = questScript.id();
                        String extension = ScriptUtils.getQuestScriptExtension(questScript.start());
                        if (questID == -1) {
                            logger.error("Found unMarked script! in the class - " + className);
                        } else {
                            questScripts.put(questID + extension, method);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        logger.serverNotice("Initialized " + npcScripts.size() + " Quest scripts in " + (System.currentTimeMillis() - start) + "ms.");
    }

    public static void initHandlers() {
        initNpcScripts();
        initQuestScripts();
    }

    public void handleNpcScript(@NotNull MapleChar chr,
                                int npcID) {
        Method method = npcScripts.get(npcID);
        ScriptApi script = null;
        if (method == null) {
            script = new ScriptApi();
            script.sayOK("The Npc ");
            script.red(npcID)
                    .addMsg(" wasn't handled!");
        } else {
            try {
                script = (ScriptApi) method.invoke(this, chr);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (script != null) {
            chr.boundScript(script, npcID);
            NpcMessage msg = script.getCurrentMsg();
            chr.write(CScriptMan.scriptMessage((byte) 0, npcID, msg.getType(), msg.getData()));
        }
    }

    public void handleNpcScript(@NotNull MapleChar chr,
                                @NotNull Npc npc) {
        handleNpcScript(chr, npc.getTemplateId());
    }

    public void handleQuestScript(@NotNull MapleChar chr,
                                  int npcID,
                                  int questID,
                                  boolean start) {
        Method method = questScripts.get(questID + ScriptUtils.getQuestScriptExtension(start));
        ScriptApi script = null;
        if (method == null) {
            script = new ScriptApi();
            script.sayOK("The ");
            script.blue("Quest ");
            script.red(questID)
                    .addMsg(" in the ");
            script.blue("NPC ");
            script.red(npcID)
                    .addMsg(" wasn't handled!");
        } else {
            try {
                script = (ScriptApi) method.invoke(this, chr);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (script != null) {
            chr.boundScript(script, npcID);
            NpcMessage msg = script.getCurrentMsg();
            chr.write(CScriptMan.scriptMessage((byte) 0, npcID, msg.getType(), msg.getData()));
        }
    }
}
