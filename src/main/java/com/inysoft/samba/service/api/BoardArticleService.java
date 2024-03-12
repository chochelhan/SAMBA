package com.inysoft.samba.service.api;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.inysoft.samba.entity.BoardArticle;
import com.inysoft.samba.entity.Member;
import com.inysoft.samba.repository.BoardArticleRepository;
import com.inysoft.samba.repository.MemberRepository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class BoardArticleService {

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected BoardArticleRepository boardArticleRepository;

    @Transactional
    public HashMap<String, Object> insertBoardArticle(HashMap<String, Object> params, String uid) {

        HashMap<String, Object> result = new HashMap<String, Object>();

        String subject = (String) params.get("subject");
        String contents = (String) params.get("contents");
        String id = (String) params.get("id");
        if (!id.isEmpty()) {
            Long isId = Long.parseLong(id);
            BoardArticle isArticle = boardArticleRepository.getFindById(isId);
            BoardArticle boardArticle = BoardArticle.builder()
                    .bid(isArticle.getBid())
                    .uid(isArticle.getUid())
                    .name(isArticle.getName())
                    .subject(subject)
                    .contents(contents)
                    .answer(isArticle.getAnswer())
                    .answerCheck(isArticle.getAnswerCheck())
                    .createAt(isArticle.getCreateAt())
                    .hit(isArticle.getHit())
                    .actType("update")
                    .actId(isId)
                    .build();
            boardArticleRepository.save(boardArticle);


        } else {

            Optional<Member> member = memberRepository.findByUid(uid);

            String bid = (String) params.get("bid");
            String name = member.get().getName();
            String answerCheck = "no";
            int hit = 0;
            BoardArticle boardArticle = BoardArticle.builder()
                    .bid(bid)
                    .uid(uid)
                    .name(name)
                    .subject(subject)
                    .contents(contents)
                    .answerCheck(answerCheck)
                    .hit(hit)
                    .actType("insert")
                    .build();

            boardArticleRepository.save(boardArticle);

        }
        result.put("status", "success");

        return result;
    }

    @Transactional
    public HashMap<String, Object> deleteBoardArticle(HashMap<String, String> params, String uid) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        String id = params.get("id");
        Long isId = Long.parseLong(id);
        BoardArticle isArticle = boardArticleRepository.getFindById(isId);
        if (uid.equals(isArticle.getUid())) {
            boardArticleRepository.deleteById(isId);
            result.put("status", "success");
        } else {
            result.put("status", "fail");
        }
        return result;
    }
}
