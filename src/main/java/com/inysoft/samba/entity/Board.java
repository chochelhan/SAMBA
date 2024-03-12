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
        @Index(name = "index_board_bname", columnList = "bname"),
        @Index(name = "index_board_buse", columnList = "buse"),
        @Index(name = "index_board_impt", columnList = "impt"),
        @Index(name = "index_board_brank", columnList = "brank"),
})
public class Board {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    protected long id;

    @Column(unique = true,nullable = false, length = 20)
    protected String bid;

    @Column(nullable = false, length = 50)
    protected String bname;

    @Column(nullable = false, length = 3)
    protected String buse;

    @Column(nullable = false, length = 3)
    protected String categoryUse;

    @Column(nullable = false, length = 3)
    protected String impt;

    @Column(nullable = true,columnDefinition = "TEXT")
    protected String categoryList;

    @Column(nullable = false, length = 5)
    protected String wauth;

    @Column(nullable = true, length = 5)
    protected int brank;

    @Builder
    public Board(String bid,
                          String bname,
                          String buse,
                          String categoryUse,
                          String impt,
                          String categoryList,
                          String wauth,
                          int brank,
                          String actType,
                          Long actId) {
        if(actType.equals("update")) {
            this.id = actId;
        }
        this.bid = bid;
        this.bname = bname;
        this.buse = buse;
        this.categoryUse = categoryUse;
        this.impt = impt;
        this.categoryList = categoryList;
        this.wauth = wauth;
        this.brank = brank;

    }
}
