package com.inysoft.samba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.inysoft.samba.entity.Favorite;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {


    public Favorite getFindById(Long id);




    @Query(value = "SELECT * FROM favorite  WHERE uid=:uid ORDER BY id DESC LIMIT 1", nativeQuery = true)
    public Favorite getFindByUid(@Param(value = "uid") String uid);
}
