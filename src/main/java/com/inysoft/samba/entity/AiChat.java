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
        @Index(name = "index_aichat_uid", columnList = "uid")
})
public class AiChat {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    protected long id;

    @Column(unique = true,nullable = false, length = 80)
    protected String uid;

    @Column(nullable = true,columnDefinition = "LONGTEXT")
    protected String content;

    @Builder
    public AiChat(String uid,
                  String content,
                  String actType,
                  Long actId) {
        if(actType.equals("update")) {
            this.id = actId;
        }
        this.uid = uid;
        this.content= content;

    }
}
