package com.dori.SpringStory.client.character;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "sp_set")
public class SPSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private byte jobLevel;
    private int sp;

    public SPSet(byte jobLevel, int sp) {
        this.jobLevel = jobLevel;
        this.sp = sp;
    }
}
