package com.dori.Dori90v.world.fieldEntities;

import com.dori.Dori90v.enums.PortalType;
import com.dori.Dori90v.utils.utilEntities.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Portal {
    private int id;
    private PortalType type;
    private String name = "";
    private int targetMapId;
    private String targetPortalName = "";
    private Position position; // Instead of 2 ints -> x,y
    private int horizontalImpact;
    private int verticalImpact;
    private String script = "";
    private boolean onlyOnce;
    private boolean hideTooltip;
    private int delay;

    public Portal(int id) {
        this.id = id;
        this.position = new Position();
    }

    public Portal deepCopy() {
        return new Portal(
                getId(),
                getType(),
                getName(),
                getTargetMapId(),
                getTargetPortalName(),
                getPosition(),
                getHorizontalImpact(),
                getVerticalImpact(),
                getScript(), isOnlyOnce(),
                isHideTooltip(),
                getDelay());
    }
}
