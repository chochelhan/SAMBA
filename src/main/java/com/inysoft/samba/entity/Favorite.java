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
@Table(indexes = {
        @Index(name = "index_favorite_uid", columnList = "uid"),
        @Index(name = "index_favorite_gtype", columnList = "gtype")
})
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;

    @Column(nullable = false, length = 80)
    protected String uid;

    @Column(nullable = false, length = 10)
    protected String gtype;

    @Column(nullable = true,columnDefinition = "TEXT")
    protected String ids;


    @Builder
    public Favorite(String uid,
                    String ids,
                    String gtype,
                    String actType,
                    Long actId) {

        if(actType.equals("update")) {
            this.id = actId;
        }
        this.uid = uid;
        this.gtype = (gtype == null || gtype.isEmpty())?"prb":gtype;
        this.ids = ids;
    }

}

