package com.dori.SpringStory.scripts.api;

import com.dori.SpringStory.enums.NpcMessageType;
import com.dori.SpringStory.scripts.message.*;
import com.dori.SpringStory.utils.NpcMessageUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.dori.SpringStory.enums.NpcMessageType.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

@SuppressWarnings("unused")
public class ScriptApi {
    private int npcID;
    private List<NpcMessage> npcMessages = new ArrayList<>();
    private Consumer<?> askResponseAction;
    private List<Runnable> responseAction;
    private int index;
    private NpcMessageType currMsgType;

    private void addSayMsg(String msg,
                           NpcMessageType type) {
        SayMsg npcMsg = new SayMsg(msg, type, (byte) 0/*in swordie they put always 4*/, npcID);
        npcMessages.add(new NpcMessage(type, npcMsg));
    }

    private void addImageMsg(String[] images) {
        SayImageMsg npcMsg = new SayImageMsg(images);
        npcMessages.add(new NpcMessage(SayImage, npcMsg));
    }

    private void addBaseAskMsg(String msg,
                               NpcMessageType type) {
        BaseAskMsg npcMsg = new BaseAskMsg(msg);
        npcMessages.add(new NpcMessage(type, npcMsg));
    }

    private void addAskTextMsg(String msg,
                               String defaultText,
                               int min,
                               int max) {
        AskTextMsg askTextMsg = new AskTextMsg(msg, defaultText, (short) min, (short) max);
        npcMessages.add(new NpcMessage(AskText, askTextMsg));
    }

    private void addAskNumberMsg(String msg,
                                 int defaultNum,
                                 int min,
                                 int max) {
        AskNumberMsg askTextMsg = new AskNumberMsg(msg, defaultNum, (short) min, (short) max);
        npcMessages.add(new NpcMessage(AskNumber, askTextMsg));
    }

    public void sayOK(String msg) {
        addSayMsg(msg, SayOk);
    }

    public ScriptApi sayNext(String msg) {
        addSayMsg(msg, SayNext);
        return this;
    }

    public void sayPrev(String msg) {
        addSayMsg(msg, SayPrev);
    }

    public ScriptApi say(String msg) {
        addSayMsg(msg, Say);
        return this;
    }

    public void sayImageMsg(String[] images) {
        addImageMsg(images);
    }

    public void sayImageMsg(String image) {
        addImageMsg(new String[]{image});
    }

    public void askYesNo(String msg, @NotNull Consumer<Boolean> responseAction) {
        addBaseAskMsg(msg, AskYesNo);
        this.askResponseAction = responseAction;
    }

    public void askText(String msg,
                        String defaultText,
                        int min,
                        int max,
                        @NotNull Consumer<String> responseAction) {
        addAskTextMsg(msg, defaultText, min, max);
        this.askResponseAction = responseAction;
    }

    public void askText(String msg,
                        int min,
                        int max,
                        @NotNull Consumer<String> responseAction) {
        addAskTextMsg(msg, "", min, max);
        this.askResponseAction = responseAction;
    }

    public void askNumber(String msg,
                          int defaultNum,
                          int min,
                          int max,
                          @NotNull Consumer<Integer> responseAction) {
        addAskNumberMsg(msg, defaultNum, min, max);
        this.askResponseAction = responseAction;
    }

    public void askNumber(String msg,
                          int min,
                          int max,
                          @NotNull Consumer<Integer> responseAction) {
        addAskNumberMsg(msg, 0, min, max);
        this.askResponseAction = responseAction;
    }

    public void askMenu(@NotNull MenuOption... menuOptions) {
        addBaseAskMsg("", AskMenu);
        addMenuItems(menuOptions);
    }

    public void askAccept(String msg) {
        addBaseAskMsg(msg, AskAccept);
    }

    private ScriptApi applyActionToMsg(Consumer<NpcMessageData> action) {
        NpcMessageData msg = this.npcMessages.getLast().getData();
        action.accept(msg);
        return this;
    }

    private ScriptApi applyInputToMsg(String additionalMsg) {
        return applyActionToMsg(msg -> msg.setMsg(msg.getMsg() + " " + additionalMsg));
    }

    public ScriptApi blue() {
        return applyActionToMsg(msg -> msg.setMsg(NpcMessageUtils.blue(msg.getMsg())));
    }

    public ScriptApi red() {
        return applyActionToMsg(msg -> msg.setMsg(NpcMessageUtils.red(msg.getMsg())));
    }

    public ScriptApi green() {
        return applyActionToMsg(msg -> msg.setMsg(NpcMessageUtils.green(msg.getMsg())));
    }

    public ScriptApi purple() {
        return applyActionToMsg(msg -> msg.setMsg(NpcMessageUtils.purple(msg.getMsg())));
    }

    public ScriptApi black() {
        return applyActionToMsg(msg -> msg.setMsg(NpcMessageUtils.black(msg.getMsg())));
    }

    public ScriptApi bold() {
        return applyActionToMsg(msg -> msg.setMsg(NpcMessageUtils.bold(msg.getMsg())));
    }

    public ScriptApi itemName(int itemID) {
        return applyInputToMsg(NpcMessageUtils.itemName(itemID));
    }

    public ScriptApi itemCount(int input) {
        return applyInputToMsg(NpcMessageUtils.itemCount(input));
    }

    public ScriptApi itemImage(int input) {
        return applyInputToMsg(NpcMessageUtils.itemImage(input));
    }

    public ScriptApi itemDetails(int itemID) {
        return applyInputToMsg(NpcMessageUtils.itemDetails(itemID));
    }

    public ScriptApi wzImage(String input) {
        return applyInputToMsg(NpcMessageUtils.wzImage(input));
    }

    public ScriptApi mapName(int mapID) {
        return applyInputToMsg(NpcMessageUtils.mapName(mapID));
    }

    public ScriptApi mobName(int mobTemplateID) {
        return applyInputToMsg(NpcMessageUtils.mobName(mobTemplateID));
    }

    public ScriptApi npcName(int npcTemplateID) {
        return applyInputToMsg(NpcMessageUtils.npcName(npcTemplateID));
    }

    public ScriptApi skillName(int skillID) {
        return applyInputToMsg(NpcMessageUtils.skillName(skillID));
    }

    public ScriptApi skillImage(int skillID) {
        return applyInputToMsg(NpcMessageUtils.skillImage(skillID));
    }

    public ScriptApi toProgressBar(int progressInNum) {
        return applyInputToMsg(NpcMessageUtils.toProgressBar(progressInNum));
    }

    public ScriptApi addMsg(String msg) {
        //TODO: need to add handling if this is the first msg (didn't create a npcMsg yet?)
        return applyInputToMsg(msg);
    }

    public ScriptApi addMsg(Integer msg) {
        //TODO: need to add handling if this is the first msg (didn't create a npcMsg yet?)
        return addMsg(msg.toString());
    }

    private void menuLine(int index, String val) {
        applyInputToMsg(NpcMessageUtils.menuLine(index, val));
    }

    public void addMenuItems(String... menuItems) {
        for (int i = 0; i < menuItems.length; i++) {
            menuLine(i, menuItems[i]);
        }
    }

    public void addMenuItems(MenuOption... menuOptions) {
        this.responseAction = new ArrayList<>();
        for (int i = 0; i < menuOptions.length; i++) {
            menuLine(i, menuOptions[i].getMsg());
            this.responseAction.add(menuOptions[i].getAction());
        }
    }

    public MenuOption addMenuOption(String msg, Runnable action) {
        return new MenuOption(msg, action);
    }

    public NpcMessage getCurrentMsg() {
        if (index < this.npcMessages.size()) {
            NpcMessage msg = this.npcMessages.get(index);
            this.currMsgType = msg.getType();
            index++;
            return msg;
        }
        return null;
    }

    public NpcMessage getNextMsg() {
        index++;
        if (index < npcMessages.size()) {
            NpcMessage msg = this.npcMessages.get(index);
            this.currMsgType = msg.getType();
            return msg;
        }
        return null;
    }

    public NpcMessage getPrevMsg() {
        if (index > 0) {
            index--;
            NpcMessage msg = this.npcMessages.get(index);
            this.currMsgType = msg.getType();
            return msg;
        }
        return null;
    }

    public void applyResponseAction(int selectedAction) {
        this.responseAction.get(selectedAction).run();
    }

    @SuppressWarnings("unchecked")
    public <T> void applyAskResponseAction(T response) {
        ((Consumer<T>) this.askResponseAction).accept(response);
    }
}
