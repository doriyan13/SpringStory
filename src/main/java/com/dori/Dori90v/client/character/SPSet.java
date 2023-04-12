package com.dori.Dori90v.client.character;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
