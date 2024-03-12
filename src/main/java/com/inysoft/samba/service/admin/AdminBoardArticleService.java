package com.inysoft.samba.service.admin;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.inysoft.samba.entity.BoardArticle;
import com.inysoft.samba.repository.BoardArticleRepository;
import com.inysoft.samba.repository.specification.BoardArticleSpecification;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Service
public class AdminBoardArticleService {

    @Autowired
    protected BoardArticleRepository boardArticleRepository;

    public HashMap<String, Object> getBoardArticleList(HashMap<String, String> params) {

        HashMap<String, Object> result = new HashMap<String, Object>();

        String orderByField = (params.get("orderByField")!=null && !params.get("orderByField").isEmpty()) ? params.get("orderByField") : "createAt";
        String orderBySort = (params.get("orderBySort")!=null && !params.get("orderBySort").isEmpty()) ? params.get("orderBySort") : "desc";
        Sort.Direction sort = Sort.Direction.ASC;

        if (orderBySort.equals("desc")) {
            sort = Sort.Direction.DESC;

        }

        String limit = (params.get("limit")!=null && !params.get("limit").isEmpty()) ? params.get("limit") : "20";
        int limitNum = Integer.parseInt(limit);
        String page = (params.get("page")!=null && !params.get("page").isEmpty()) ? params.get("page") : "1";
        int pageNum = Integer.parseInt(page) - 1;

        Specification<BoardArticle> where = (root, query, criteriaBuilder) -> null;
        if(params.get("keywordCmd")!=null && params.get("keyword")!=null) {
            String keywordCmd = params.get("keywordCmd");
            String keyword = params.get("keyword");
            if (!keywordCmd.isEmpty() && !keyword.isEmpty()) {
                switch (keywordCmd) {
                    case "name":
                        where = where.and(BoardArticleSpecification.likeName(keyword));
                        break;
                    case "subject":
                        where = where.and(BoardArticleSpecification.likeSubject(keyword));
                        break;
                    case "all":
                        where = where.or(BoardArticleSpecification.likeName(keyword));
                        where = where.or(BoardArticleSpecification.likeSubject(keyword));
                        break;
                }
            }
        }
        if(params.get("bid")!=null) {
            String bid = params.get("bid");
            if (!bid.isEmpty()) {
                where = where.and(BoardArticleSpecification.equalBid(bid));

            }
        }
        if(params.get("answerCheck")!=null) {
            String answerCheck = params.get("answerCheck");
            if (!answerCheck.isEmpty()) {
                where = where.and(BoardArticleSpecification.equalAnswerCheck(answerCheck));

            }
        }
        if(params.get("stdate")!=null && params.get("endate")!=null) {
            String stdateString = params.get("stdate");
            String endateString = params.get("endate");
            if (!stdateString.isEmpty() && !endateString.isEmpty()) {
                stdateString = stdateString + " 00:00:00.111";
                endateString = endateString + " 23:59:59.111";

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                LocalDateTime stdate = LocalDateTime.parse(stdateString, formatter);
                LocalDateTime endate = LocalDateTime.parse(endateString, formatter);

                where = where.and(BoardArticleSpecification.startCreateAt(stdate));
                where = where.and(BoardArticleSpecification.endCreateAt(endate));
            }
        }

        Pageable paging = PageRequest.of(pageNum, limitNum, sort, orderByField);
        Page<BoardArticle> boardArticle = boardArticleRepository.findAll(where, paging);
        result.put("status", "success");
        result.put("data", boardArticle);
        return result;
    }

    public HashMap<String, Object> getBoardArticleInfo(HashMap<String, Object> params) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        Long id = (Long) params.get("id");
        BoardArticle boardArticle = boardArticleRepository.getFindById(id);
        result.put("status", "success");
        result.put("data", boardArticle);
        return result;
    }


    @Transactional
    public HashMap<String, Object> updateBoardArticleAnswer(HashMap<String, String> params) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        Long id = Long.parseLong(params.get("id"));
        String answer = params.get("answer");
        BoardArticle isArticle = boardArticleRepository.getFindById(id);

        BoardArticle boardArticle = BoardArticle.builder()
                .bid(isArticle.getBid())
                .uid(isArticle.getUid())
                .name(isArticle.getName())
                .subject(isArticle.getSubject())
                .contents(isArticle.getContents())
                .answer(answer)
                .answerCheck("yes")
                .createAt(isArticle.getCreateAt())
                .hit(isArticle.getHit())
                .actType("update")
                .actId(id)
                .build();

        boardArticleRepository.save(boardArticle);


        result.put("status", "success");
        return result;
    }

    @Transactional
    public HashMap<String, Object> deleteBoardArticle(HashMap<String, Object> params) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        List<String> dataList = (List<String>) params.get("ids");
        for (String idString : dataList) {
            Long id = Long.parseLong(idString);
            boardArticleRepository.deleteById(id);
        }
        result.put("status", "success");
        return result;
    }
}


