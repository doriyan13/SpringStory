package com.dori.SpringStory.utils.utilEntities;

import com.dori.SpringStory.temporaryStats.characters.CharacterTemporaryStat;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UnsignedInt128BitBlock {
    private int[] dataBlock = new int[4];

    private int getBitBlockPos(int bitPos){
        return bitPos / 32;
    }

    public void setBit(int bitPos){
        if(bitPos >= CharacterTemporaryStat.Pad.getBitPos() && bitPos <= CharacterTemporaryStat.GuidedBullet.getBitPos()){
            dataBlock[getBitBlockPos(bitPos)] |= (1 << bitPos);
        }
    }

    public int[] getArrayLE(){
        int j = dataBlock.length;
        int[] retVal = new int[j];
        for (int i = 0; i < retVal.length; i++) {
            retVal[j - 1] = dataBlock[i];
            j -= 1;
        }
        return retVal;
    }
}
