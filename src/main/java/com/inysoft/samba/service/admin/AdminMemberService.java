package com.inysoft.samba.service.admin;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.inysoft.samba.entity.Member;
import com.inysoft.samba.entity.common.Role;
import com.inysoft.samba.repository.MemberRepository;
import com.inysoft.samba.repository.specification.MemberSpecification;
import com.inysoft.samba.auth.JwtUtil;
import com.inysoft.samba.service.api.MemberService;
import com.inysoft.samba.service.auth.UserDetailsServiceImpl;
;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class AdminMemberService {

    @Autowired
    protected UserDetailsServiceImpl userDetailsService;

    @Autowired
    protected MemberRepository memberRepository;


    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    MemberService memberService;

    /*
     *@ 관리자 초기 정보 입력
     * params :  uid,upass,email,name
     * return : {status: -> message,success.fail}
     */
    @Transactional
    public HashMap<String, Object> insertAdmin()  {

        HashMap<String, Object> result = new HashMap<String, Object>();

        String uid = "admin11";//params.get("uid");
        String email = "admin11@test.com";//params.get("email");
        String upass = "admin";//params.get("upass");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        upass = encoder.encode(upass);

        String name = "관dsd";
        String emailSend = "no";
        Role role = Role.ROLE_MEMBER;
        Member member = Member.builder()
                .uid(uid)
                .passwd(upass)
                .role(role)
                .name(name)
                .auth("yes")
                .email(email)
                .emailSend(emailSend)
                .uout("no")
                .actType("insert")
                .build();

        memberRepository.save(member);



        return result;
    }


    /*
     *@ 관리자 로그인
     * params : uid,upass
     * return : {status: -> message,success.fail} access_token
     */
    public HashMap<String, Object> adminLogin(String uid, String pass, HttpSession session) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        if (!uid.isEmpty() && !pass.isEmpty()) {
            Optional<Member> findMember = memberRepository.findByUid(uid);
            if (!findMember.isPresent()) {
                result.put("status", "message");
            } else {
                if(!findMember.get().getUid().equals(uid)) {
                    result.put("status", "message");
                    return result;
                }
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                if (encoder.matches(pass, findMember.get().getPasswd())) {
                    if (findMember.get().getRole().equals(Role.ROLE_ADMIN) || findMember.get().getRole().equals(Role.ROLE_MANAGER)) {

                        UserDetails userDetails = userDetailsService.loadUserByUsername(findMember.get().getUid());
                        HashMap<String,Object> tokenResult =  jwtUtil.generateToken(userDetails);

                        HashMap<String,String> tokenParams = new HashMap<>();
                        tokenParams.put("updateType","token");
                        tokenParams.put("uidEncode",(String) tokenResult.get("accessToken"));
                        memberService.memberUpdateWithToken(findMember.get().getId(),tokenParams);

                        result.put("access_token",tokenResult.get("accessToken"));
                        result.put("memberInfo", findMember.get());
                        session.setMaxInactiveInterval(360000);
                        session.setAttribute("user_id", findMember.get().getId());
                        result.put("status", "success");
                    } else {
                        result.put("status", "message");
                    }
                } else {
                    result.put("status", "message");
                }
            }

        } else {
            result.put("status", "fail");
        }

        return result;
    }


    public HashMap<String, Object> getAdminMainInfo() {

        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("totalList",memberRepository.getSQLMemberByTotal());

        return result;
    }

    public HashMap<String, Object> getAdminInfo(HttpSession session) {

        HashMap<String, Object> result = new HashMap<String, Object>();

        if (session.getAttribute("user_id") != null) {
            Long user_id = (Long) session.getAttribute("user_id");
            Member member = memberRepository.getById(user_id);
            if (member.getRole().equals(Role.ROLE_ADMIN)) {
                result.put("name", member.getName());
                result.put("email", member.getEmail());
                result.put("upass", member.getPasswd());
                result.put("status", "success");
            } else {
                result.put("status", "fail");
            }
        } else {
            result.put("status", "fail");
        }
        return result;
    }

    @Transactional
    public HashMap<String, Object> updateAdminInfo(HashMap<String, String> params, HttpSession session) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        if (session.getAttribute("user_id") != null) {
            Long user_id = (Long) session.getAttribute("user_id");
            Member member = memberRepository.getById(user_id);
            if (member.getRole().equals(Role.ROLE_ADMIN)) {

                String name = params.get("name");
                String email = params.get("email");
                String nowUpass = params.get("nowUpass");
                String newPass = params.get("upass");

                if (nowUpass == null || nowUpass.isEmpty()) {
                    result.put("status", "fail");
                    return result;
                }
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                if (encoder.matches(nowUpass, member.getPasswd())) {

                    String upass = member.getPasswd();
                    if (newPass != null && !newPass.isEmpty()) {
                        upass = encoder.encode(newPass);
                    }
                    String img = member.getImg();
                    Long id = member.getId();
                    Member UpdMember = Member.builder()
                            .uid(member.getUid())
                            .passwd(upass)
                            .role(member.getRole())
                            .name(name)
                            .auth("yes")
                            .email(email)
                            .img(img)
                            .uout("no")
                            .actType("update")
                            .actId(id)
                            .build();

                    memberRepository.save(UpdMember);
                    result.put("status", "success");
                } else {
                    result.put("status", "message");
                }
            } else {

                result.put("status", "fail");
            }

        } else {
            result.put("status", "fail");
        }
        return result;
    }


    public HashMap<String, Object> adminLogout(HttpServletRequest request) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        HttpSession session = request.getSession();
        session.invalidate();
        result.put("status", "success");

        return result;
    }

    /*
     *@ 회원정보 목록
     * params : role=>회원구분 (indi:일반,busi:기업)
     *         orderByField=>정렬 기준필드
     *          orderBySort=>정렬 asc, desc
     *          limit => 한번에 보일 목록수
     *          page => 페이지
     *          keywordCmd => 검색필드
     *          keyword => 검색어
     *          stdate => 검색 시작 등록일
     *          endate => 검색 종료 등록일
     *          emailSend => 메일수신 동의 여부 (yes,no)
     *          mstatus => 탈퇴여부 (out,ing)
     * return : {status: ->success} {data: org.springframework.data.domain.Page}
     */
    public HashMap<String, Object> getMemberListByIndi(HashMap<String, String> params) {

        HashMap<String, Object> result = new HashMap<String, Object>();

        String roleString = (!params.get("role").isEmpty()) ? params.get("role") : "student";
        Role role = Role.ROLE_MEMBER;

        String orderByField = (!params.get("orderByField").isEmpty()) ? params.get("orderByField") : "createAt";
        String orderBySort = (!params.get("orderBySort").isEmpty()) ? params.get("orderBySort") : "desc";
        Sort.Direction sort = Sort.Direction.ASC;

        if (orderBySort.equals("desc")) {
            sort = Sort.Direction.DESC;

        }
        String limit = (!params.get("limit").isEmpty()) ? params.get("limit") : "20";
        int limitNum = Integer.parseInt(limit);
        String page = (!params.get("page").isEmpty()) ? params.get("page") : "1";
        int pageNum = Integer.parseInt(page) - 1;

        Specification<Member> where = (root, query, criteriaBuilder) -> null;
        where = where.and(MemberSpecification.equalRole(role));

        String keywordCmd = params.get("keywordCmd");
        String keyword = params.get("keyword");
        if (!keywordCmd.isEmpty() && !keyword.isEmpty()) {
            switch (keywordCmd) {
                case "name":
                    where = where.and(MemberSpecification.likeName(keyword));
                    break;
                case "uid":
                    where = where.and(MemberSpecification.likeUid(keyword));
                    break;
                case "email":
                    where = where.and(MemberSpecification.likeEmail(keyword));
                    break;
            }
        }
        /*
        String emailSend = params.get("emailSend");
        if (!emailSend.isEmpty()) {
            where = where.and(MemberSpecification.equalEmailSend(emailSend));

        }

         */
        String auth = params.get("auth");
        if (!auth.isEmpty()) {
            where = where.and(MemberSpecification.equalAuth(auth));

        }
        String mstatus = params.get("mstatus");
        if(mstatus.equals("out")) {
            where = where.and(MemberSpecification.equalNotUout("no"));
        }

        String stdateString = params.get("stdate");
        String endateString = params.get("endate");
        if (!stdateString.isEmpty() && !endateString.isEmpty()) {
            stdateString = stdateString + " 00:00:00.111";
            endateString = endateString + " 23:59:59.111";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            LocalDateTime stdate = LocalDateTime.parse(stdateString, formatter);
            LocalDateTime endate = LocalDateTime.parse(endateString, formatter);

            where = where.and(MemberSpecification.startCreateAt(stdate));
            where = where.and(MemberSpecification.endCreateAt(endate));
        }

        Pageable paging = PageRequest.of(pageNum, limitNum, sort, orderByField);
        Page<Member> member = memberRepository.findAll(where, paging);
        result.put("status", "success");
        result.put("data", member);

        return result;
    }

    /*
     *@ 회원정보 수정

     */
    @Transactional
    public HashMap<String, Object> updateMemberInfo(HashMap<String, Object> params) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        String uid = (String) params.get("uid");
        Optional<Member> member = memberRepository.findByUid(uid);

        Long id = member.get().getId();
        String name = (String) params.get("name");
        String emailSend = (String) params.get("emailSend");
        String email = (String) params.get("email");
        String auth = (String) params.get("auth");

        Member emailMember = memberRepository.findByEmailAndIdNot(email, id);
        if (emailMember != null) {
            result.put("code", "doubleEmail");
            result.put("status", "message");
            return result;
        }

        String img = member.get().getImg();
        Member UpdMember = Member.builder()
                .uid(member.get().getUid())
                .auth(auth)
                .passwd(member.get().getPasswd())
                .role(member.get().getRole())
                .name(name)
                .email(email)
                .emailSend(emailSend)
                .img(img)
                .uout(member.get().getUout())
                .createAt(member.get().getCreateAt())
                .actType("update")
                .actId(id)
                .build();

        memberRepository.save(UpdMember);
        result.put("status", "success");


        return result;
    }



    /*
     *@ 회원정보 삭제

     */
    @Transactional
    public HashMap<String, Object> deleteMember(HashMap<String, Object> params) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        List<String> dataList = (List<String>) params.get("list");
        for (String idString : dataList) {
            Long id = Long.parseLong(idString);
            memberRepository.deleteById(id);
        }
        result.put("status", "success");
        return result;
    }



}


