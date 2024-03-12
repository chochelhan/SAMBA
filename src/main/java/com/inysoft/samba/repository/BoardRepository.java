package com.inysoft.samba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inysoft.samba.entity.Board;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {


    public Board getFindByBid(String bid);

    public Board getFindById(Long id);

    public Board findTop1ByOrderByBrankDesc();

    public List<Board> getFindByBuse(String buse);


    public List<Board> getFindByBuseAndImpt(String buse,String impt);

}
