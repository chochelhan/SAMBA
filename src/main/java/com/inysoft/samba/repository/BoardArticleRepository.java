package com.inysoft.samba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.inysoft.samba.entity.BoardArticle;

import java.util.List;

@Repository
public interface BoardArticleRepository extends JpaRepository<BoardArticle, Long>, JpaSpecificationExecutor<BoardArticle> {


    public BoardArticle getFindById(Long id);

    public List<BoardArticle> getFindByBidOrderByCreateAtDesc(String bid);


}
