package com.inysoft.samba.service.api;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.inysoft.samba.entity.common.Role;
import com.inysoft.samba.entity.Member;
import com.inysoft.samba.entity.Setting;
import com.inysoft.samba.repository.specification.MemberSpecification;

import com.inysoft.samba.service.auth.UserDetailsServiceImpl;
import com.inysoft.samba.repository.MemberRepository;
import com.inysoft.samba.repository.SettingRepository;
import com.inysoft.samba.auth.JwtUtil;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MemberService {

    @Autowired
    protected UserDetailsServiceImpl userDetailsService;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    SettingRepository settingRepository;

    @Autowired
    protected JwtUtil jwtUtil;
   /*
    @Autowired
    MemberConnectSearch memberConnectSearch;
    */
    public String imagePath = "fileUpload/member";

    /*
     *@  로그인
     * params : uid,upass
     * return : {status: -> message,success.fail} access_token
     */
    @Transactional
    public HashMap<String, Object> memberLogin(String uid, String pass,String type) throws IOException {

        HashMap<String, Object> result = new HashMap<String, Object>();

        if (!uid.isEmpty() && !pass.isEmpty()) {
            Optional<Member> findMember = memberRepository.findByUid(uid);
            if (!findMember.isPresent()) {
                result.put("code", "wrong");
                result.put("status", "message");
            } else {
                if (!findMember.get().getUid().equals(uid)) {
                    result.put("code", "wrong");
                    result.put("status", "message");
                    return result;
                }
                if (!findMember.get().getRole().equals(Role.ROLE_ADMIN)) {
                    if (!findMember.get().getAuth().equals("yes")) {
                        result.put("code", "notauth");
                        result.put("status", "message");
                        return result;
                    }
                    if (findMember.get().getUout().equals("yes")) {
                        result.put("code", "uout");
                        result.put("status", "message");
                        return result;
                    }
                }
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                if (encoder.matches(pass, findMember.get().getPasswd())) {
                    if(type.equals("success")) {
                        result = loginSuccess(findMember);
                    } else {
                        result.put("status", "success");
                    }
                } else {
                    result.put("code", "wrong");
                    result.put("status", "message");
                }
            }
        } else {
            result.put("status", "fail");
        }

        return result;
    }

    public HashMap<String, Object> updateRefreshToken(String uid) {
        Optional<Member> findMember = memberRepository.findByUid(uid);
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (findMember.isPresent()) {
            result = createToken(findMember);
        } else {
            result.put("status", "message");
        }
        return result;
    }

    public HashMap<String, Object> memberLoginBySns(String uid) throws IOException {
        Optional<Member> findMember = memberRepository.findByUid(uid);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result = loginSuccess(findMember);
        return result;
    }

    public HashMap<String, Object> loginSuccess(Optional<Member> findMember) throws IOException {

        HashMap<String, String> connectParams = new HashMap<String, String>();
        /*
        connectParams.put("uid", findMember.get().getUid());
        connectParams.put("userRole", role);
        connectParams.put("userName", findMember.get().getName());
        memberConnectSearch.insert(connectParams);
        */
        return createToken(findMember);
    }

    private HashMap<String,Object> createToken(Optional<Member> findMember) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(findMember.get().getUid());
        HashMap<String, Object> tokenResult = jwtUtil.generateToken(userDetails);


        HashMap<String, String> tokenParams = new HashMap<>();
        tokenParams.put("updateType", "token");
        tokenParams.put("uidEncode", (String) tokenResult.get("accessToken"));
        memberUpdateActive(findMember.get().getId(), tokenParams);

        HashMap<String, Object> result = new HashMap<String, Object>();

        result.put("access_token", tokenResult.get("accessToken"));
        result.put("token_expired", tokenResult.get("tokenExpired"));
        result.put("memberInfo", findMember.get());
        result.put("status", "success");

        return result;
    }

    /*
     *@  토큰 재발행
     * params : uid,upass
     * return : {status: -> message,success.fail} access_token
     */
    @Transactional
    public HashMap<String, Object> getRefreshToken(HashMap<String, String> params) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        String uid = params.get("uid");
        String token = params.get("token");

        Optional<Member> findMember = memberRepository.findByUid(uid);
        if (findMember.isPresent()) {
            if (findMember.get().getUidEncode().equals(token)) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(findMember.get().getUid());
                HashMap<String, Object> tokenResult = jwtUtil.generateToken(userDetails);

                HashMap<String, String> tokenParams = new HashMap<>();
                tokenParams.put("updateType", "token");
                tokenParams.put("uidEncode", (String) tokenResult.get("accessToken"));
                memberUpdateActive(findMember.get().getId(), tokenParams);


                result.put("access_token", tokenResult.get("accessToken"));
                result.put("token_expired", tokenResult.get("tokenExpired"));
                result.put("status", "success");
            } else {
                result.put("status", "message");
            }
        } else {
            result.put("status", "message");
        }
        return result;
    }

    public void memberUpdateWithToken(Long id, HashMap<String, String> params) {
        memberUpdateActive(id, params);
    }


    /*
     *@ 회원정보
     * params : session
     * return : {status: -> message,success.fail}
     */
    public void memberRemoveToken(String uid, HashMap<String, String> params) {
        HashMap<String, Object> member = getMemberInfo(uid);
        HashMap<String, Object> memberInfo = (HashMap<String, Object>) member.get("memberInfo");
        memberUpdateActive((Long) memberInfo.get("id"), params);
    }

    public HashMap<String, Object> getMemberInfo(String uid) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        Optional<Member> member = memberRepository.findByUid(uid);
        if (member != null) {
            HashMap<String, Object> memberInfo = new HashMap<String, Object>();
            memberInfo.put("id", member.get().getId());
            memberInfo.put("uid", member.get().getUid());
            memberInfo.put("name", member.get().getName());
            memberInfo.put("email", member.get().getEmail());
            memberInfo.put("emailSend", member.get().getEmailSend());
            memberInfo.put("pcs", member.get().getPcs());
            memberInfo.put("role", member.get().getRole());
            memberInfo.put("snsType", member.get().getSnsType());
            memberInfo.put("img", member.get().getImg());
            result.put("memberInfo", memberInfo);
            result.put("status", "success");
        } else {
            result.put("status", "fail");
        }


        return result;
    }

    public boolean checkMemberByPcs(String pcs) {
        Member pcsMember = memberRepository.findByPcs(pcs);
        if (pcsMember != null) {
            return false;
        } else {
            return true;
        }
    }

    /*
     *@ 아이디,닉네임,이메일 중복 여부 검사
     * params : type=> (uid,email,nick) , key=>value
     * return : {status: -> message,success.fail}
     */
    public HashMap<String, Object> checkDoubleKey(String type, String key) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        if (!key.isEmpty() && !type.isEmpty()) {
            Boolean flag = false;
            switch (type) {
                case "uid":
                    Optional<Member> member = memberRepository.findByUid(key);
                    if (member.isEmpty()) flag = true;
                    break;
                case "email":
                    Member emailMember = memberRepository.findByEmail(key);
                    if (emailMember == null) flag = true;
                    break;
            }

            if (flag) {
                result.put("status", "success");
            } else {
                result.put("status", "message");

            }
        } else {
            result.put("status", "fail");
        }

        return result;
    }

    /*
     *@ 회원가입
     * params :  uid,nick,email.upass,emailSend,name
     * return : {status: -> message,success.fail}
     */
    @Transactional
    public HashMap<String, Object> joinMember(HashMap<String, String> params) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        String uid = params.get("uid");
        Boolean flag = false;
        if (!uid.isEmpty()) {
            Optional<Member> member = memberRepository.findByUid(uid);
            if (!member.isEmpty()) {
                result.put("code", "doubleUid");
                result.put("status", "message");
                return result;
            }
        } else {
            flag = true;
        }
        String roleStr = params.get("role");
        if (roleStr.isEmpty()) {
            flag = true;
        }
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String upass = params.get("upass");
        if (upass.isEmpty()) {
            flag = true;
        } else {
            upass = encoder.encode(upass);
        }
        String name = params.get("name");
        if (name.isEmpty()) {
            flag = true;
        }

        String uidEncode = encoder.encode(uid);
        String pcs = params.get("pcs");
        Member pcsMember = memberRepository.findByPcs(pcs);
        if (pcsMember != null) {
            result.put("code", "doublePcs");
            result.put("status", "message");
            return result;
        }

        String marketAgree = params.get("marketAgree");
        String email = uid;
        if (!params.get("email").isEmpty()) {
            email = params.get("email");
            Member eMember = memberRepository.findByEmail(email);
            if (eMember != null) {
                result.put("code", "doubleEmail");
                result.put("status", "message");
                return result;
            }


        }
        String snsType = "none";
        if (!params.get("snsType").isEmpty()) {
            snsType = params.get("snsType");
        }
        String auth = "yes";
        String sUserType = params.get("sUserType");
        String oPcs = params.get("oPcs");
        String oUserName = params.get("oUserName");

        Role memberRole = Role.ROLE_MEMBER;
        Role role = Role.ROLE_MEMBER;

        //String emailSend = (!params.get("emailSend").isEmpty()) ? params.get("emailSend") : "no";
        String emailSend = "no";
        if (flag) {
            result.put("status", "fail");
            return result;
        }
        Member member = Member.builder()
                .uid(uid)
                .uidEncode(uidEncode)
                .auth(auth)
                .pcs(pcs)
                .passwd(upass)
                .role(role)
                .name(name)
                .email(email)
                .emailSend(emailSend)
                .snsType(snsType)
                .marketAgree(marketAgree)
                .uout("no")
                .actType("insert")
                .build();

        Member resultMember = memberRepository.save(member);


        String subject = "";
        String content = "";
        String use = "no";
        List<Setting> settingList = settingRepository.findAll();
        if (settingList.size() > 0) {
            Setting setting = settingRepository.getFindById(settingList.get(0).getId());
            String emailSettingString = setting.getEmail();
            JSONObject jObject = new JSONObject(emailSettingString);
            JSONObject obj = jObject.getJSONObject("join");
            use = obj.getString("use");
            if (use.equals("yes")) {
                auth = "no";
                subject = obj.getString("subject");
                content = obj.getString("content");
            }
        }

        if (use.equals("yes") && resultMember != null && !resultMember.getUid().isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            String joinDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            content = content.replace("[:uid:]", uid);
            content = content.replace("[:name:]", name);
            content = content.replace("[:joinDate:]", joinDate);

            subject = subject.replace("[:uid:]", uid);
            subject = subject.replace("[:name:]", name);
            subject = subject.replace("[:joinDate:]", joinDate);

            result.put("mailTo", email);
            result.put("mailSubject", subject);
            result.put("mailContent", content);
            result.put("code", "emailAuth");
            result.put("status", "success");
        } else if (resultMember != null && !resultMember.getUid().isEmpty()) {
            result.put("code", "auth");
            result.put("status", "success");
        } else {
            result.put("code", "error");
            result.put("status", "message");
            return result;
        }
        return result;
    }


    /*
     *@  비번 찾기시 인증번호 메일로 전송
     * params :
     * return : {status:(message,success,fail)}
     */
    public HashMap<String, Object> sendMemberAuthByEmail(String email, HttpSession session) {
        HashMap<String, Object> result = new HashMap<String, Object>();

        Member member = memberRepository.findByEmail(email);
        if (member != null && !member.getUid().isEmpty()) {
            if (member.getAuth().equals("yes")) {
                List<Setting> settingList = settingRepository.findAll();
                if (settingList.size() > 0) {
                    Setting setting = settingRepository.getFindById(settingList.get(0).getId());
                    String emailSettingString = setting.getEmail();
                    JSONObject jObject = new JSONObject(emailSettingString);
                    JSONObject obj = jObject.getJSONObject("findpass");
                    String use = obj.getString("use");
                    if (use.equals("yes")) {
                        String subject = obj.getString("subject");
                        String content = obj.getString("content");

                        int maxValue = 9999;
                        int minValue = 1000;

                        int authCode = (int) Math.floor(Math.random() * (maxValue - minValue + 1) + minValue);
                        session.setMaxInactiveInterval(180);
                        session.setAttribute("authCodeByPass_" + email, String.valueOf(authCode));
                        content = content.replace("[:authCode:]", String.valueOf(authCode));
                        // email = "cch6721@nate.com";

                        result.put("mailTo", email);
                        result.put("mailSubject", subject);
                        result.put("mailContent", content);
                        result.put("authCode", authCode);
                        result.put("status", "success");

                    } else {
                        result.put("code", "noEmailSetting");
                        result.put("status", "message");
                    }
                } else {
                    result.put("code", "noEmailSetting");
                    result.put("status", "message");
                }

            } else {
                result.put("code", "noAuth");
                result.put("status", "message");
            }

        } else {
            result.put("code", "fail");
            result.put("status", "message");
        }
        return result;
    }

    /*
     *@  비번 찾기시 인증번호 확인
     * params :
     * return : {status:(message,success,fail)}
     */
    public HashMap<String, Object> checkAuthCode(HashMap<String, String> params, HttpSession session) {
        HashMap<String, Object> result = new HashMap<String, Object>();

        String email = params.get("email");
        String authNumber = params.get("authNumber");
        String sessAuthCode = (String) session.getAttribute("authCodeByPass_" + email);
        if (authNumber.equals(sessAuthCode)) {
            result.put("status", "success");
        } else {
            result.put("status", "message");
        }

        return result;
    }

    /*
     *@  회원 인증메일 확인
     * params :
     * return : {status:(message,success,fail)}
     */
    public HashMap<String, Object> setAuthEmail(String code) {
        HashMap<String, Object> result = new HashMap<String, Object>();

        Member member = memberRepository.findByUidEncode(code);
        if (member != null && !member.getUid().isEmpty()) {
            if (member.getAuth().equals("no")) {
                HashMap<String, String> updateParams = new HashMap<String, String>();
                updateParams.put("updateType", "auth");
                updateParams.put("auth", "yes");
                memberUpdateActive(member.getId(), updateParams);
                result.put("status", "success");
            } else {
                result.put("status", "fail");
            }

        } else {
            result.put("status", "fail");
        }


        return result;
    }


    /*
     *@  비밀번호 찾기시 비밀번호 변경
     * params :
     * return : {status:(message,success,fail)}
     */
    public HashMap<String, Object> updateUpassWithEmail(HashMap<String, String> params) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        String email = params.get("email");
        String newPass = params.get("upass");
        Member member = memberRepository.findByEmail(email);
        if (member != null && !member.getUid().isEmpty()) {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            String upass = encoder.encode(newPass);
            HashMap<String, String> updateParams = new HashMap<String, String>();
            updateParams.put("updateType", "password");
            updateParams.put("password", upass);
            memberUpdateActive(member.getId(), updateParams);
            result.put("status", "success");

        } else {
            result.put("status", "fail");
        }
        return result;

    }

    /*
     *@  회원정보 변경
     * params :
     * return : {status:(message,success,fail)}
     */
    public HashMap<String, Object> updateMember(HashMap<String, String> params, String uid) {


        HashMap<String, Object> result = new HashMap<String, Object>();
        Optional<Member> member = memberRepository.findByUid(uid);

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!params.get("upass").isEmpty()) {
            if (params.get("adminUpass") != null) {
                if (!params.get("adminUpass").equals("insysdwrwe")) {
                    result.put("code", "fail");
                    result.put("status", "message");
                    return result;
                }
            } else {
                String pass = params.get("nowupass");
                if (!encoder.matches(pass, member.get().getPasswd())) {
                    result.put("code", "wrongNowUpass");
                    result.put("status", "message");
                    return result;
                }
            }
        }

        Role isRole = member.get().getRole();



        if (!params.get("upass").isEmpty()) {
            String upass = encoder.encode(params.get("upass"));
            HashMap<String, String> passParams = new HashMap<String, String>();
            passParams.put("updateType", "password");
            passParams.put("password", upass);
            memberUpdateActive(member.get().getId(), passParams);
        }


        HashMap<String, String> updateParams = new HashMap<String, String>();
        updateParams.put("updateType", "memberInfo");
        updateParams.put("name", params.get("name"));
        updateParams.put("pcs", params.get("pcs"));
        memberUpdateActive(member.get().getId(), updateParams);
        result.put("status", "success");
        return result;
    }

    private void memberUpdateActive(Long id, HashMap<String, String> params) {
        Member isMember = memberRepository.getById(id);
        if (isMember != null) {
            String img = isMember.getImg();
            String emailSend = isMember.getEmailSend();
            String password = isMember.getPasswd();
            String auth = isMember.getAuth();
            String uout = isMember.getUout();
            String snsType = isMember.getSnsType();
            String pcs = isMember.getPcs();
            String name = isMember.getName();


            String uidEncode = isMember.getUidEncode();
            String marketAgree = isMember.getMarketAgree();
            switch (params.get("updateType")) {
                case "img":
                    img = params.get("img");
                    break;
                case "password":
                    password = params.get("password");
                    break;
                case "token":
                    uidEncode = params.get("uidEncode");
                    break;

                case "memberInfo":
                    name = params.get("name");
                    pcs = params.get("pcs");
                    break;
                case "auth":
                    auth = params.get("auth");
                    break;
                case "out":
                    uout = "yes";
                    break;
            }
            Member member = Member.builder()
                    .uid(isMember.getUid())
                    .passwd(password)
                    .auth(auth)
                    .uidEncode(uidEncode)
                    .role(isMember.getRole())
                    .name(name)
                    .email(isMember.getEmail())
                    .pcs(pcs)
                    .snsType(snsType)
                    .emailSend(emailSend)
                    .img(img)
                    .uout(uout)
                    .marketAgree(marketAgree)
                    .createAt(isMember.getCreateAt())
                    .actType("update")
                    .actId(id)
                    .build();

            memberRepository.save(member);
        }
    }


    /*
     *@  비밀번호 변경
     * params :
     * return : {status:(message,success,fail)}
     */
    public HashMap<String, Object> updateMemberPassword(HashMap<String, String> params, String uid) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        Optional<Member> isMember = memberRepository.findByUid(uid);

        if (isMember != null) {
            String nowPass = params.get("nowPass");
            String newPass = params.get("newPass");
            if (nowPass == null || nowPass.isEmpty() || newPass == null || newPass.isEmpty()) {
                result.put("status", "fail");
                return result;
            }
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(nowPass, isMember.get().getPasswd())) {

                String upass = encoder.encode(newPass);
                HashMap<String, String> updateParams = new HashMap<String, String>();
                updateParams.put("updateType", "password");
                updateParams.put("password", upass);
                memberUpdateActive(isMember.get().getId(), updateParams);
                result.put("status", "success");
            } else {
                result.put("status", "message");
            }

        } else {
            result.put("status", "fail");
        }
        return result;
    }

    /*
     *@  회원탈퇴
     * params :
     * return : {status:(message,success,fail)}
     */
    public HashMap<String, Object> memberOut(String uid) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        Optional<Member> isMember = memberRepository.findByUid(uid);
        if (isMember != null) {
            if (!isMember.get().getUid().isEmpty()) {

                HashMap<String, String> updateParams = new HashMap<String, String>();
                updateParams.put("updateType", "out");
                memberUpdateActive(isMember.get().getId(), updateParams);
                result.put("status", "success");
            } else {
                result.put("status", "fail");
            }

        } else {
            result.put("status", "fail");
        }
        return result;
    }


    /*
     *@  이미지 저장
     * params :
     * return : {status:(message,success,fail)}
     */
    public HashMap<String, Object> updateImage(MultipartFile dFile, String uid, String type) throws IOException {

        HashMap<String, Object> result = new HashMap<String, Object>();
        Path currentPath = Paths.get("");
        String sitePath = currentPath.toAbsolutePath().toString();

        Date now = new Date();
        Long nowTime = now.getTime();

        String absolutePath = new File(sitePath).getAbsolutePath() + "/"; // 파일이 저장될 절대 경로
        String newFileName = "image_" + nowTime;
        if (type.equals("tax")) {
            newFileName = "tax_" + uid + "_" + nowTime;
        }
        String fileExtension = '.' + dFile.getOriginalFilename().replaceAll("^.*\\.(.*)$", "$1"); // 정규식 이용하여 확장자만 추출

        try {
            if (!dFile.isEmpty()) {
                File file = new File(absolutePath + imagePath);
                if (!file.exists()) {
                    file.mkdirs(); // mkdir()과 다르게 상위 폴더가 없을 때 상위폴더까지 생성
                }
                File saveFile = new File(absolutePath + imagePath + "/" + newFileName + fileExtension);
                dFile.transferTo(saveFile);
                if (type.equals("tax")) {
                    result.put("fileName", newFileName + fileExtension);
                    result.put("status", "success");
                } else {
                    Optional<Member> isMember = memberRepository.findByUid(uid);
                    if (isMember != null) {
                        HashMap<String, String> updateParams = new HashMap<String, String>();
                        updateParams.put("updateType", "img");
                        updateParams.put("img", newFileName + fileExtension);
                        memberUpdateActive(isMember.get().getId(), updateParams);

                        result.put("memImg", newFileName + fileExtension);
                        result.put("status", "success");
                    } else {
                        result.put("status", "fail");
                    }
                }
            } else {
                result.put("status", "fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     *@ 회원 이미지 가져오기
     * params :
     * return :
     */
    public byte[] getMemberImage(String imgName) throws IOException {

        FileInputStream fis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Path currentPath = Paths.get("");
        String sitePath = currentPath.toAbsolutePath().toString();
        String absolutePath = new File(sitePath).getAbsolutePath() + "/"; // 파일이 저장될 절대 경로

        try {
            fis = new FileInputStream(absolutePath + imagePath + "/" + imgName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int readCount = 0;
        byte[] buffer = new byte[1024];
        byte[] fileArray = null;


        try {
            while ((readCount = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, readCount);
            }
            fileArray = baos.toByteArray();
            fis.close();
            baos.close();
        } catch (IOException e) {
            throw new RuntimeException("File Error");
        }
        return fileArray;
    }

    private String getSiteUrl(HttpServletRequest request) {
        String protocol = request.isSecure() ? "https://" : "http://";
        String url = protocol + request.getServerName();
        return url;
    }

}
