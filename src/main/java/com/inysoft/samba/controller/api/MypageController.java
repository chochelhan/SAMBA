package com.inysoft.samba.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.inysoft.samba.entity.Member;
import com.inysoft.samba.entity.common.Role;
import com.inysoft.samba.service.api.MemberService;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;

@RestController
@RequestMapping("/api/controller/mypage/")
public class MypageController {

    @Autowired
    protected MemberService memberService;



    /**
     *  로그아웃
     * @return
     */
    @PostMapping("logout")
    public ResponseEntity<HashMap<String, Object>> memberLogout() {
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("status","success");
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }


    /**
     *  회원활동 내역 가져오기
     * @return
     */
    @PostMapping("getMyMain")
    public ResponseEntity<HashMap<String, Object>> getMyMain(@RequestBody HashMap<String, Object> params, Principal principal) throws IOException {

        HashMap<String, Object> result = new HashMap<>();
        String uid = principal.getName();
        HashMap<String, Object> memberInfo = memberService.getMemberInfo(uid);
        result.put("memberInfo",memberInfo.get("memberInfo"));
        result.put("status","success");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     *  회원활동 내역(게시글 정보) 가져오기
     * @return
     */
    @PostMapping("getMyArticleList")
    public ResponseEntity<HashMap<String, Object>> getMyArticleList(@RequestBody HashMap<String, Object> params,Principal principal) throws IOException {


        HashMap<String, Object> result = new HashMap<>();

        result.put("status","success");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    /**
     *  회원정보 가져오기
     * @return
     */
    @PostMapping("getMemberInfo")
    public ResponseEntity<HashMap<String, Object>> getMemberInfo(Principal principal) {

        String uid = principal.getName();
        HashMap<String, Object> result = memberService.getMemberInfo(uid);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     *  회원정보 수정
     * @return
     */
    @PostMapping("updateMemberInfo")
    public ResponseEntity<HashMap<String, Object>> updateMemberInfo(@RequestBody HashMap<String, String> params,Principal principal) throws IOException {

        String uid = principal.getName();
        HashMap<String, Object> result = memberService.updateMember(params,uid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    /*
     *@ 이미지 등록
     */
    @PostMapping("updateMemberImage")
    public ResponseEntity<HashMap<String, Object>> updateMemberImage(@RequestParam(name = "image") MultipartFile img,
                                                                     @RequestParam(name = "type") String type,
                                                                     Principal principal) throws IOException {

        String uid = principal.getName();
        HashMap<String, Object> result = memberService.updateImage(img,uid,type);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    /**
     *  회원비번 변경
     * @return
     */
    @PostMapping("updateMemberPassword")
    public ResponseEntity<HashMap<String, Object>> updateMemberPassword(@RequestBody HashMap<String, String> params,Principal principal) {

        String uid = principal.getName();
        HashMap<String, Object> result = memberService.updateMemberPassword(params,uid);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     *  회원 탈퇴
     * @return
     */
    @PostMapping("memberOut")
    public ResponseEntity<HashMap<String, Object>> memberOut(Principal principal) {

        String uid = principal.getName();
        HashMap<String, Object> resultMap = memberService.memberOut(uid);
        String status = (String) resultMap.get("status");
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }



    @PostMapping("updateRefreshToken")
    public ResponseEntity<HashMap<String, Object>> updateRefreshToken(Principal principal) {
        String uid = principal.getName();
        HashMap<String, Object> resultMap = memberService.updateRefreshToken(uid);
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }
}
