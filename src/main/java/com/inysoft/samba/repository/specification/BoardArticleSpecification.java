package com.inysoft.samba.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import com.inysoft.samba.entity.BoardArticle;

import java.time.LocalDateTime;

public class BoardArticleSpecification {

    public static Specification<BoardArticle> likeName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"),"%"+name+"%");
    }
    public static Specification<BoardArticle> likeUid(String uid) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("uid"),"%"+uid+"%");
    }
    public static Specification<BoardArticle> likeSubject(String subject) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("subject"),"%"+subject+"%");
    }
    public static Specification<BoardArticle> equalAnswerCheck(String answerCheck) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("answerCheck"),answerCheck);
    }
    public static Specification<BoardArticle> equalBid(String bid) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("bid"),bid);
    }


    public static Specification<BoardArticle> startCreateAt(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("createAt"),date);
    }
    public static Specification<BoardArticle> endCreateAt(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("createAt"),date);
    }


}
