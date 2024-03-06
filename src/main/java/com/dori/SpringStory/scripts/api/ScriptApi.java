package com.dori.SpringStory.scripts.api;

import com.dori.SpringStory.enums.AvatarMsgType;
import com.dori.SpringStory.enums.NpcMessageType;
import com.dori.SpringStory.scripts.message.*;
import com.dori.SpringStory.utils.NpcMessageUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private void addSayMsg(@NotNull String msg,
                           NpcMessageType type,
                           @Nullable Runnable action) {
        SayMsg npcMsg = new SayMsg(msg, type, (byte) 0/*in swordie they put always 4*/, npcID, action);
        npcMessages.add(new NpcMessage(type, npcMsg));
    }

    private void addSayMsg(@NotNull String msg,
                           NpcMessageType type) {
        addSayMsg(msg, type, null);
    }

    private void addImageMsg(String[] images) {
        SayImageMsg npcMsg = new SayImageMsg(images);
        npcMessages.add(new NpcMessage(SayImage, npcMsg));
    }

    private void addBaseAskMsg(@NotNull String msg,
                               NpcMessageType type) {
        BaseAskMsg npcMsg = new BaseAskMsg(msg);
        npcMessages.add(new NpcMessage(type, npcMsg));
    }

    private void addAskTextMsg(@NotNull String msg,
                               String defaultText,
                               int min,
                               int max) {
        AskTextMsg askTextMsg = new AskTextMsg(msg, defaultText, (short) min, (short) max);
        npcMessages.add(new NpcMessage(AskText, askTextMsg));
    }

    private void addAskNumberMsg(@NotNull String msg,
                                 int defaultNum,
                                 int min,
                                 int max) {
        AskNumberMsg askTextMsg = new AskNumberMsg(msg, defaultNum, (short) min, (short) max);
        npcMessages.add(new NpcMessage(AskNumber, askTextMsg));
    }

    public void sayOK(@NotNull String msg) {
        addSayMsg(msg, SayOk);
    }

    public ScriptApi sayOK(@NotNull String msg,
                           Runnable action) {
        addSayMsg(msg, SayOk, action);
        return this;
    }

    public ScriptApi sayNext(@NotNull String msg) {
        addSayMsg(msg, SayNext);
        return this;
    }

    public ScriptApi sayNext(@NotNull String msg,
                             Runnable action) {
        addSayMsg(msg, SayNext, action);
        return this;
    }

    public void sayPrev(@NotNull String msg) {
        addSayMsg(msg, SayPrev);
    }

    public ScriptApi say(@NotNull String msg) {
        addSayMsg(msg, Say);
        return this;
    }

    public ScriptApi say(@NotNull String msg,
                         Runnable action) {
        addSayMsg(msg, Say, action);
        return this;
    }

    public void sayImageMsg(String[] images) {
        addImageMsg(images);
    }

    public void sayImageMsg(@NotNull String image) {
        addImageMsg(new String[]{image});
    }

    public void askYesNo(@NotNull String msg, @NotNull Consumer<Boolean> responseAction) {
        addBaseAskMsg(msg, AskYesNo);
        this.askResponseAction = responseAction;
    }

    public void askText(@NotNull String msg,
                        String defaultText,
                        int min,
                        int max,
                        @NotNull Consumer<String> responseAction) {
        addAskTextMsg(msg, defaultText, min, max);
        this.askResponseAction = responseAction;
    }

    public void askText(@NotNull String msg,
                        int min,
                        int max,
                        @NotNull Consumer<String> responseAction) {
        addAskTextMsg(msg, "", min, max);
        this.askResponseAction = responseAction;
    }

    public void askNumber(@NotNull String msg,
                          int defaultNum,
                          int min,
                          int max,
                          @NotNull Consumer<Integer> responseAction) {
        addAskNumberMsg(msg, defaultNum, min, max);
        this.askResponseAction = responseAction;
    }

    public void askNumber(@NotNull String msg,
                          int min,
                          int max,
                          @NotNull Consumer<Integer> responseAction) {
        addAskNumberMsg(msg, 0, min, max);
        this.askResponseAction = responseAction;
    }

    public void askMenu(@NotNull String msg, @NotNull MenuOption... menuOptions) {
        addBaseAskMsg(msg, AskMenu);
        addMenuItems(menuOptions);
    }

    public void askMenu(@NotNull String msg, @NotNull List<MenuOption> menuOptions) {
        addBaseAskMsg(msg, AskMenu);
        this.responseAction = new ArrayList<>();
        for (int i = 0; i < menuOptions.size(); i++) {
            menuLine(i, menuOptions.get(i).getMsg());
            this.responseAction.add(menuOptions.get(i).getAction());
        }
    }

    public void askAccept(@NotNull String msg) {
        addBaseAskMsg(msg, AskAccept);
    }

    private void askAvatarLook(@NotNull String msg,
                               @NotNull List<Integer> options,
                               @NotNull AvatarMsgType type) {
        AvatarMsg npcMsg = new AvatarMsg(msg, options, type);
        npcMessages.add(new NpcMessage(AskAvatar, npcMsg));
    }

    public void askAvatarHair(@NotNull String msg,
                              @NotNull List<Integer> options) {
        askAvatarLook(msg, options, AvatarMsgType.Hair);
    }

    public void askAvatarFace(@NotNull String msg,
                              @NotNull List<Integer> options) {
        askAvatarLook(msg, options, AvatarMsgType.Face);
    }

    public void askAvatarSkin(@NotNull String msg,
                              @NotNull List<Integer> options) {
        askAvatarLook(msg, options, AvatarMsgType.Skin);
    }

    private ScriptApi applyActionToMsg(Consumer<NpcMessageData> action) {
        NpcMessageData msg = this.npcMessages.getLast().getData();
        action.accept(msg);
        return this;
    }

    private ScriptApi applyInputToMsg(@NotNull String additionalMsg) {
        return applyActionToMsg(msg -> msg.setMsg(msg.getMsg() + additionalMsg));
    }

    public ScriptApi addMsg(@NotNull String msg) {
        if (npcMessages.isEmpty()) {
            return sayNext(msg);
        }
        return applyInputToMsg(msg);
    }

    public ScriptApi addMsg(@NotNull Integer msg) {
        if (npcMessages.isEmpty()) {
            return sayNext(String.valueOf(msg));
        }
        return addMsg(msg.toString());
    }

    public ScriptApi addNewLine(@NotNull String msg) {
        if (npcMessages.isEmpty()) {
            return sayNext(msg);
        }
        return applyInputToMsg(NpcMessageUtils.newLine(msg));
    }

    public ScriptApi blue() {
        return applyActionToMsg(msg -> msg.setMsg(NpcMessageUtils.blue(msg.getMsg())));
    }

    public ScriptApi blue(@NotNull String msg) {
        return addMsg(NpcMessageUtils.blue(msg));
    }

    public ScriptApi blue(@NotNull Integer msg) {
        return addMsg(NpcMessageUtils.blue(msg));
    }

    public ScriptApi red() {
        return applyActionToMsg(msg -> msg.setMsg(NpcMessageUtils.red(msg.getMsg())));
    }

    public ScriptApi red(@NotNull String msg) {
        return addMsg(NpcMessageUtils.red(msg));
    }

    public ScriptApi red(@NotNull Integer msg) {
        return addMsg(NpcMessageUtils.red(msg));
    }

    public ScriptApi green() {
        return applyActionToMsg(msg -> msg.setMsg(NpcMessageUtils.green(msg.getMsg())));
    }

    public ScriptApi green(@NotNull String msg) {
        return addMsg(NpcMessageUtils.green(msg));
    }

    public ScriptApi green(@NotNull Integer msg) {
        return addMsg(NpcMessageUtils.green(msg));
    }

    public ScriptApi purple() {
        return applyActionToMsg(msg -> msg.setMsg(NpcMessageUtils.purple(msg.getMsg())));
    }

    public ScriptApi purple(@NotNull String msg) {
        return addMsg(NpcMessageUtils.purple(msg));
    }

    public ScriptApi purple(@NotNull Integer msg) {
        return addMsg(NpcMessageUtils.purple(msg));
    }

    public ScriptApi black() {
        return applyActionToMsg(msg -> msg.setMsg(NpcMessageUtils.black(msg.getMsg())));
    }

    public ScriptApi black(@NotNull String msg) {
        return addMsg(NpcMessageUtils.black(msg));
    }

    public ScriptApi black(Integer msg) {
        return addMsg(NpcMessageUtils.black(msg));
    }

    public ScriptApi bold() {
        return applyActionToMsg(msg -> msg.setMsg(NpcMessageUtils.bold(msg.getMsg())));
    }

    public ScriptApi bold(@NotNull String msg) {
        return addMsg(NpcMessageUtils.bold(msg));
    }

    public ScriptApi bold(@NotNull Integer msg) {
        return addMsg(NpcMessageUtils.bold(msg));
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

    public ScriptApi wzImage(@NotNull String input) {
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

    private void menuLine(int index, @NotNull String val) {
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

    public MenuOption addMenuOption(@NotNull String msg, Runnable action) {
        return new MenuOption(msg, action);
    }

    public NpcMessage getCurrentMsg() {
        if (index < this.npcMessages.size()) {
            NpcMessage msg = this.npcMessages.get(index);
            this.currMsgType = msg.getType();
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
        this.askResponseAction = null;
    }
}
