package com.inysoft.samba.controller.admin;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.inysoft.samba.entity.Member;
import com.inysoft.samba.service.admin.AdminMemberService;
import com.inysoft.samba.service.api.MemberService;

import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/controller/member/")
public class AdminMemberController {

    @Autowired
    protected AdminMemberService adminMemberService;

    @Autowired
    protected MemberService memberService;

    @PostMapping("getAdminInfo")
    public ResponseEntity<HashMap<String, Object>> getAdminInfo(HttpServletRequest request) {

        HttpSession session = request.getSession();
        HashMap<String, Object> result = adminMemberService.getAdminInfo(session);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("updateAdminInfo")
    public ResponseEntity<HashMap<String, Object>> updateAdminInfo(@RequestBody HashMap<String, String> params,HttpServletRequest request) {

        HttpSession session = request.getSession();
        HashMap<String, Object> result = adminMemberService.updateAdminInfo(params,session);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("updateMemberInfo")
    public ResponseEntity<HashMap<String, Object>> updateMemberInfo(@RequestBody HashMap<String, String> params) {

        String uid = params.get("uid");
        params.put("adminUpass","insysdwrwe");
        HashMap<String, Object> result = memberService.updateMember(params,uid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("logout")
    public ResponseEntity<HashMap<String, Object>> logout(HttpServletRequest request) {

        HashMap<String, Object> result = adminMemberService.adminLogout(request);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("getMemberList")
    public ResponseEntity<HashMap<String, Object>> getMemberList(@RequestBody HashMap<String, String> params) {

        HashMap<String, Object> result = adminMemberService.getMemberListByIndi(params);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PostMapping("deleteMember")
    public ResponseEntity<HashMap<String, Object>> deleteMember(@RequestBody HashMap<String, Object> params) {

        HashMap<String, Object> result = adminMemberService.deleteMember(params);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



    @PostMapping("getAdminMainInfo")
    public ResponseEntity<HashMap<String, Object>> getAdminMainInfo(@RequestBody HashMap<String, Object> params) {

        HashMap<String, Object> result = adminMemberService.getAdminMainInfo();
        result.put("status", "success");
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }
}



