package com.dori.SpringStory.client.character;

import com.dori.SpringStory.connection.packet.OutPacket;
import com.dori.SpringStory.utils.MapleUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor

@Entity
@Table(name = "extend_sp")
public class ExtendSP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "extend_sp")
    private List<SPSet> spSet;

    public ExtendSP() {
        this(0);
    }

    public ExtendSP(int subJobs) {
        spSet = new ArrayList<>();
        for(int i = 1; i <= subJobs; i++) {
            spSet.add(new SPSet((byte) i, 0));
        }
    }

    public int getTotalSp() {
        return spSet.stream().mapToInt(SPSet::getSp).sum();
    }

    public void encode(OutPacket outPacket) {
        outPacket.encodeByte(getSpSet().size());
        for(SPSet spSet : getSpSet()) {
            outPacket.encodeByte(spSet.getJobLevel());
            outPacket.encodeByte(spSet.getSp());
        }
    }

    public void setSpToJobLevel(int jobLevel, int sp) {
        getSpSet().stream().filter(sps -> sps.getJobLevel() == jobLevel).findFirst().ifPresent(spSet -> spSet.setSp(sp));
    }

    public int getSpByJobLevel(byte jobLevel) {
        SPSet spSet = MapleUtils.findWithPred(getSpSet(), sps -> sps.getJobLevel() == jobLevel);
        if(spSet != null) {
            return spSet.getSp();
        }
        return -1;
    }
}
