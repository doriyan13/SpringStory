package com.dori.Dori90v.world.fieldEntities;

import com.dori.Dori90v.utils.utilEntities.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Life {
    private int objectId = -1;
    private Position position;
    protected int cy, fh, templateId, mobTime, rx0, rx1, type, x, y;
    protected boolean flip;
    private String lifeType = "";
    private boolean hide;
    private String limitedName = "";
    private boolean useDay;
    private boolean useNight;
    private boolean hold;
    private boolean noFoothold;
    private int regenStart;
    private int mobAliveReq;
    private boolean dummy;
    private boolean spine;
    private boolean mobTimeOnDie;
    private boolean respawnable;
    private byte moveAction;
    private Field field;
    private Position homePosition;
    private Position vPosition;
    private  byte team;

    public Life(int templateId) {
        this.templateId = templateId;
        this.position = new Position(0, 0);
    }
}
