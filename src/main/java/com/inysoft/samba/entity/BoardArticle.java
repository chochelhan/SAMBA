package com.inysoft.samba.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
        @Index(name = "index_board_article_bid", columnList = "bid"),
        @Index(name = "index_board_article_uid", columnList = "uid"),
        @Index(name = "index_board_article_name", columnList = "name"),
        @Index(name = "index_board_article_subject", columnList = "subject"),
        @Index(name = "index_board_article_createAt", columnList = "createAt"),
        @Index(name = "index_board_article_answerCheck", columnList = "answerCheck")
})
public class BoardArticle {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    protected long id;

    @Column(nullable = false, length = 15)
    protected String bid;

    @Column(nullable = true, length = 50)
    protected String uid;

    @Column(nullable = true, length = 50)
    protected String name;

    @Column(nullable = true, length = 10)
    protected String category;

    @Column(nullable = false, length = 100)
    protected String subject;

    @Column(nullable = false,columnDefinition="TEXT")
    protected String contents;

    @Column(nullable = true,columnDefinition="TEXT")
    protected String answer;

    @Column(nullable = false, length = 3)
    protected String answerCheck;

    @Column(nullable = true, length = 3)
    protected int hit;

    protected LocalDateTime createAt;

    protected LocalDateTime updateAt;

    @Builder
    public BoardArticle(String bid,
                        String uid,
                        String name,
                        String category,
                        String subject,
                        String contents,
                        String answer,
                        String answerCheck,
                        int hit,
                        LocalDateTime createAt,
                        String actType,
                        Long actId) {
        this.bid = bid;
        this.uid = uid;
        this.name = name;
        this.category = category;
        this.subject = subject;
        this.contents = contents;
        this.answer = answer;
        this.answerCheck = answerCheck;
        this.hit = hit;
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
