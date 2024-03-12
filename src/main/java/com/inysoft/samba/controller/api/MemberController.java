package com.inysoft.samba.controller.api;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inysoft.samba.service.api.*;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/api/member/")
public class MemberController {

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected MailService mailService;


    @Autowired
    protected SmsService smsService;

    @Autowired
    protected SettingService settingService;


    @Autowired
    protected SnsLoginService snsLoginService;


    @PostMapping("login")
    public ResponseEntity<HashMap<String, Object>> memberLogin(@RequestBody HashMap<String, String> params) throws IOException {
        String uid = params.get("uid");
        String upass = params.get("upass");
        String type  = params.get("type");
        HashMap<String, Object> resultMap = memberService.memberLogin(uid,upass,type);

        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }


    @PostMapping("loginBySns")
    public ResponseEntity<HashMap<String, Object>> loginBySns(@RequestBody HashMap<String, String> params) throws IOException {
        String uid = params.get("uid");
        HashMap<String, Object> resultMap = memberService.memberLoginBySns(uid);

        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }


    /**
     * @ 비번 찾기시 인증번호 메일로 전송
     * @param params
     * @return
     */
    @PostMapping("sendMemberAuthByEmail")
    public ResponseEntity<HashMap<String, Object>> sendMemberAuthByEmail(@RequestBody HashMap<String, String> params, HttpServletRequest request) {

        String email = params.get("email");
        HttpSession session = request.getSession();
        HashMap<String, Object> resultMap = memberService.sendMemberAuthByEmail(email,session);
        String status = (String) resultMap.get("status");
        if(status.equals("success")) {
            String to = (String) resultMap.get("mailTo");
            String subject = (String) resultMap.get("mailSubject");
            String content = (String) resultMap.get("mailContent");
            mailService.mailSend(to,subject,content);
        }

        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

    /**
     * @ 인증메일
     * @param params
     * @return
     */
    @PostMapping("updateAuthEmail")
    public ResponseEntity<HashMap<String, Object>> updateAuthEmail(@RequestBody HashMap<String, String> params) {
        String code = params.get("code");
        HashMap<String, Object> resultMap = memberService.setAuthEmail(code);

        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }
    /**
     * @ 비밀번호찾기시 인증코드 확인
     * @param params
     * @return
     */
    @PostMapping("findMemberUpass")
    public ResponseEntity<HashMap<String, Object>> findMemberUpass(@RequestBody HashMap<String, String> params, HttpServletRequest request) {

        HttpSession session = request.getSession();
        HashMap<String, Object> resultMap = memberService.checkAuthCode(params,session);
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

    /**
     * @ 인증후에 비밀번호 변경
     * @param params
     * @return
     */
    @PostMapping("updateEmailMemberUpass")
    public ResponseEntity<HashMap<String, Object>> updateEmailMemberUpass(@RequestBody HashMap<String, String> params) {
        HashMap<String, Object> resultMap = memberService.updateUpassWithEmail(params);
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }


    @PostMapping("checkUid")
    public ResponseEntity<HashMap<String, Object>> checkUid(@RequestBody HashMap<String, String> params) {

        String uid = params.get("uid");
        HashMap<String, Object> resultMap = memberService.checkDoubleKey("uid",uid);

        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

    @PostMapping("checkEmail")
    public ResponseEntity<HashMap<String, Object>> checkEmail(@RequestBody HashMap<String, String> params) {

        String email = params.get("email");
        HashMap<String, Object> resultMap = memberService.checkDoubleKey("email",email);

        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

    @PostMapping("checkNick")
    public ResponseEntity<HashMap<String, Object>> checkNick(@RequestBody HashMap<String, String> params) {

        String nick = params.get("nick");
        HashMap<String, Object> resultMap = memberService.checkDoubleKey("nick",nick);

        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

    /*
    *@ 회원가입
    * params: uid,nick,email.upass,emailSend,name
    *
    */

    @PostMapping("join")
    public ResponseEntity<HashMap<String, Object>> memberJoin(@RequestBody HashMap<String, String> params) {

        HashMap<String, Object> resultMap = memberService.joinMember(params);

        String status = (String) resultMap.get("status");
        String code = (String) resultMap.get("code");
        if(status.equals("success") && code.equals("emailAuth")) {
            String to = (String) resultMap.get("mailTo");
            String subject = (String) resultMap.get("mailSubject");
            String content = (String) resultMap.get("mailContent");
            mailService.mailSend(to,subject,content);
        }
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }



    @PostMapping("sendAuthNumber")
    public ResponseEntity<HashMap<String, Object>> sendAuthNumber(@RequestBody HashMap<String, String> params, HttpServletRequest request) throws Exception {

        HashMap<String, Object> resultMap = new HashMap<>();

        String pcs = params.get("pcs");
        boolean check = memberService.checkMemberByPcs(pcs);
        if(check) {
            HttpSession session = request.getSession();
            resultMap = smsService.sendAuthNumber(params, session);

        } else {
            resultMap.put("status","message");
            resultMap.put("code","double");
        }
        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

    @PostMapping("checkAuthNumber")
    public ResponseEntity<HashMap<String, Object>> checkAuthNumber(@RequestBody HashMap<String, String> params, HttpServletRequest request) {

        HttpSession session = request.getSession();
        HashMap<String, Object> resultMap = smsService.checkAuthNumber(params,session);


        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

    /**
     * 회원 이미지 가져오기
     * @param imgName
     * @return
     */
    @GetMapping("getMemberImage")
    public  @ResponseBody byte[] getMemberImage(@RequestParam(name = "imgName") String imgName) throws IOException {

        byte[] imageUrl = memberService.getMemberImage(imgName);
        return imageUrl;
    }





    /**
     * SNS 로그인 (네이버)
     */
    @GetMapping("snsCallBack/nv")
    public  @ResponseBody String snsCallBackNv(@RequestParam(value = "code") String code, HttpServletRequest request) throws IOException,Exception {

        HashMap<String, Object> result = settingService.getSetting();
        String resultSetting = (String) result.get("sns");
        String hostName = request.getServerName();

        JSONObject resultCode =  snsLoginService.naverLogin(code,resultSetting,hostName);
        return "<script>window.opener.snsLoginResult('"+resultCode.toString()+"');window.close();</script>";

    }

    /**
     * SNS 로그인 (카카오)
     */
    @GetMapping("snsCallBack/ka")
    public  @ResponseBody String snsCallBackKa(@RequestParam(value = "code") String code, HttpServletRequest request) throws IOException,Exception {

        HashMap<String, Object> result = settingService.getSetting();
        String resultSetting = (String) result.get("sns");
        String hostName = request.getServerName();

        JSONObject resultCode =  snsLoginService.kakaoLogin(code,resultSetting,hostName);
        return "<script>window.opener.snsLoginResult('"+resultCode.toString()+"');window.close();</script>";
    }

    /**
     * SNS 로그인 (구글)
     */
    @GetMapping("snsCallBack/gl")
    public  @ResponseBody String snsCallBackGl(@RequestParam(value = "code") String code, HttpServletRequest request)   throws IOException,Exception {

        HashMap<String, Object> result = settingService.getSetting();
        String resultSetting = (String) result.get("sns");
        String hostName = request.getServerName();

        JSONObject resultCode =  snsLoginService.googleLogin(code,resultSetting,hostName);
        return "<script>window.opener.snsLoginResult('"+resultCode.toString()+"');window.close();</script>";
    }

}



