package com.inysoft.samba.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.inysoft.samba.entity.common.Role;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "index_member_uidEncode", columnList = "uidEncode"),
        @Index(name = "index_member_role", columnList = "role"),
        @Index(name = "index_member_auth", columnList = "auth"),
        @Index(name = "index_member_uout", columnList = "uout"),
        @Index(name = "index_member_name", columnList = "name"),
        @Index(name = "index_member_email", columnList = "email"),
        @Index(name = "index_member_emailSend", columnList = "emailSend"),
        @Index(name = "index_member_snsType", columnList = "snsType"),
        @Index(name = "index_member_marketAgree", columnList = "marketAgree"),
        @Index(name = "index_member_createAt", columnList = "createAt")
})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;

    @Column(unique = true, nullable = false, length = 80)
    protected String uid;

    @Column(nullable = true, length = 200)
    protected String uidEncode;

    @Column(nullable = false, length = 3)
    protected String auth;

    @Column(nullable = false, length = 100)
    protected String passwd;

    @Enumerated(EnumType.STRING)
    protected Role role;

    @Column(nullable = false, length = 30)
    protected String name;

    @Column(nullable = false, length = 60)
    protected String email;

    @Column(length = 3)
    protected String emailSend;

    @Column(length = 100)
    protected String img;

    @Column(length = 20)
    protected String pcs;


    @Column(length = 4)
    protected String snsType;


    @Column(length = 4)
    protected String marketAgree;



    @Column(length = 3)
    protected String uout;

    protected LocalDateTime createAt;

    protected LocalDateTime updateAt;


    @Builder
    public Member(String uid,
                  String uidEncode,
                  String auth,
                  String passwd,
                  Role role,
                  String name,
                  String email,
                  String emailSend,
                  String pcs,
                  String snsType,
                  String img,
                  String uout,
                  String marketAgree,
                  LocalDateTime createAt,
                  String actType,
                  Long actId) {

        this.name = name;
        this.email = email;
        this.uid = uid;
        this.uidEncode = uidEncode;
        this.passwd = passwd;
        this.role = role;
        this.emailSend = emailSend;
        this.pcs = pcs;
        this.snsType = snsType;
        this.img = img;
        this.uout = uout;
        this.auth = auth;

        this.marketAgree = marketAgree;

        LocalDateTime now = LocalDateTime.now();
        LocalDate dayNow = LocalDate.now();

        if (actType.equals("update")) {
            this.id = actId;
            this.updateAt = now;
            this.createAt = (createAt == null) ? now : createAt;

        } else {
            this.updateAt = now;
            this.createAt = now;
        }

    }
}

