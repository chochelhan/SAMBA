package com.inysoft.samba.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;

    @Column(nullable = true, columnDefinition = "TEXT")
    protected String edu;

    @Column(nullable = true, columnDefinition = "TEXT")
    protected String egtype;

    @Column(nullable = true, columnDefinition = "TEXT")
    protected String sns;

    @Column(nullable = true, columnDefinition = "TEXT")
    protected String agree;

    @Column(nullable = true, columnDefinition = "TEXT")
    protected String email;

    @Builder
    public Setting(String sns,
                   String edu,
                   String egtype,
                   String agree,
                   String email,
                   String actType,
                   Long actId) {
        if (actType.equals("update")) {
            this.id = actId;
        }
        this.egtype = egtype;
        this.edu = edu;
        this.sns = sns;
        this.agree = agree;
        this.email = email;

    }
}
