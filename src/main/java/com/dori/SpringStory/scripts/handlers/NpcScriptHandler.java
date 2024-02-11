package com.dori.SpringStory.scripts.handlers;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.connection.packet.packets.CScriptMan;
import com.dori.SpringStory.constants.ServerConstants;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.scripts.api.NpcMessage;
import com.dori.SpringStory.scripts.api.NpcScript;
import com.dori.SpringStory.scripts.api.ScriptApi;
import com.dori.SpringStory.utils.MapleUtils;
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
public class NpcScriptHandler {
    private static NpcScriptHandler instance;
    private static final Logger logger = new Logger(NpcScriptHandler.class);
    private static final Map<Integer, Method> npcScripts = new HashMap<>();

    public static NpcScriptHandler getInstance() {
        if (instance == null) {
            instance = new NpcScriptHandler();
        }
        return instance;
    }

    public static void initHandlers() {
        long start = System.currentTimeMillis();
        String handlersDir = ServerConstants.NPC_SCRIPTS_DIR;
        Set<File> files = new HashSet<>();
        MapleUtils.findAllFilesInDirectory(files, new File(handlersDir));
        for (File file : files) {
            try {
                // grab all files in the NPCs scripts dir, strip them to their package name, and remove .java extension -
                String className = file.getPath()
                        .replaceAll("[\\\\|/]", ".")
                        .split("src\\.main\\.java\\.")[1]
                        .replaceAll("\\.java", "");
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

    public void handleNpcScript(@NotNull MapleChar chr,
                                @NotNull Npc npc) {
        Method method = npcScripts.get(npc.getTemplateId());
        ScriptApi script = null;
        if (method == null) {
            script = new ScriptApi();
            script.sayOK("The Npc ");
            script.red(npc.getTemplateId())
                    .addMsg(" wasn't handled!");
        } else {
            try {
                script = (ScriptApi) method.invoke(this, chr);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (script != null) {
            chr.boundScript(script, npc.getTemplateId());
            NpcMessage msg = script.getCurrentMsg();
            chr.write(CScriptMan.scriptMessage((byte) 0, npc.getTemplateId(), msg.getType(), msg.getData()));
        }
    }
}
