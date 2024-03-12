package com.inysoft.samba.controller.admin;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.inysoft.samba.entity.common.Role;
import com.inysoft.samba.entity.Member;
import com.inysoft.samba.auth.JwtUtil;
import com.inysoft.samba.service.admin.AdminMemberService;
import com.inysoft.samba.service.auth.UserDetailsServiceImpl;
import com.inysoft.samba.repository.MemberRepository;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/controller/")
public class AdminLoginController {

    @Autowired
    protected AdminMemberService adminMemberService;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("checkStatus")
    public ResponseEntity<HashMap<String, Object>> checkStatus(HttpServletRequest request, HttpServletResponse response) {

        HashMap<String, Object> result = new HashMap<String, Object>();

        String authorization = request.getHeader("Authorization"); // 헤더 파싱
        String username = "", token = "";

        result.put("status", "message");
        result.put("data", "noLogin");
        if (authorization != null && authorization.startsWith("Bearer ")) { // Bearer 토큰 파싱
            token = authorization.substring(7); // jwt token 파싱
            username = jwtUtil.getUsernameFromToken(token); // username 얻어오기
            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.isValidToken(token, userDetails)) {
                    Boolean flag = false;
                    HttpSession session = request.getSession();
                    if (session.getAttribute("user_id") != null) {

                        //String url = request.getRequestURI();
                        Optional<Member> findMember = memberRepository.findByUid(username);
                        Role role = findMember.get().getRole();
                        if (role.equals(Role.ROLE_ADMIN) || role.equals(Role.ROLE_MANAGER)) {
                            flag = true;
                        }
                    }
                    if (flag) { // 관리자로 로그인
                        result.put("status", "success");
                        result.put("data", "success");
                    }
                }
            }
        } else {
            List<Member> adminMmember = memberRepository.findByRole(Role.ROLE_ADMIN);
            if (adminMmember.size() < 1) {
                result.put("data", "emptyAdmin");
            }

        }

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    /*
    @GetMapping("insertAdmin")
    public ResponseEntity<HashMap<String, Object>> insertAdmin(HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        HashMap<String, Object> resultMap = adminMemberService.insertAdmin(session);

        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

     */

    @PostMapping("login")
    public ResponseEntity<HashMap<String, Object>> login(@RequestBody HashMap<String, String> params, HttpServletRequest request) {

        String uid = params.get("uid");
        String upass = params.get("upass");
        HttpSession session = request.getSession();
        HashMap<String, Object> resultMap = adminMemberService.adminLogin(uid, upass, session);

        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

}



