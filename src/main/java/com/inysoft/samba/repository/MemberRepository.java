package com.inysoft.samba.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.inysoft.samba.entity.common.Role;
import com.inysoft.samba.entity.Member;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {


    public Member getById(Long id);

    public Member findByUidAndPasswdAndRole(String uid, String passwd, Role role);


    public Member findByUidEncode(String code);
    public Member findByEmail(String email);
    public Member findByPcs(String pcs);

    public Member findByEmailAndIdNot(String email,Long id);


    public List<Member>  findByRole(Role role);
    public List<Member>  findByRoleAndUout(Role role,String uout);
    public List<Member>  findByRoleNotAndUout(Role role,String uout);


    @Query(value = "SELECT COUNT(id) AS total FROM member WHERE role=:role AND uout='no'", nativeQuery = true)
    public Map<String,Long> getMemberTotalByRole(@Param(value = "role") String role);

    @Query(value = "UPDATE member SET uid_encode=:uidEncode WHERE uid=:uid", nativeQuery = true)
    @Modifying
    @Transactional
    public void updateSQLUidEncode(@Param(value = "uidEncode") String uidEncode,@Param(value = "uid") String uid);


    @Query(value = "SELECT COUNT(id) AS total,role FROM member WHERE uout='no' GROUP BY role", nativeQuery = true)
    public List<Map<String,Long>> getSQLMemberByTotal();

    Optional<Member> findByUid(String uid);
    Optional<Member> findByName(String name);




    @Query(value = "SELECT * FROM member WHERE uout='no' AND uid in (:uids)", nativeQuery = true)
    public List<Member> getSQLMemberListByOnlineUids(@Param(value = "uids") List<String> uids);


}
