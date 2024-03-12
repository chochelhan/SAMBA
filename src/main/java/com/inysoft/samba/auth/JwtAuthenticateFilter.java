package com.inysoft.samba.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.inysoft.samba.entity.common.Role;
import com.inysoft.samba.entity.Member;
import com.inysoft.samba.service.auth.UserDetailsServiceImpl;
import com.inysoft.samba.repository.MemberRepository;


import java.io.IOException;
import java.util.Optional;


@RequiredArgsConstructor
@Component
public class JwtAuthenticateFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @JsonIgnore
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization"); // 헤더 파싱
        String username = "", token = "";

        if (authorization != null && authorization.startsWith("Bearer ")) { // Bearer 토큰 파싱
            token = authorization.substring(7); // jwt token 파싱
            username = jwtUtil.getUsernameFromToken(token); // username 얻어오기
        } else {
            filterChain.doFilter(request, response);
        }


        // 현재 SecurityContextHolder 에 인증객체가 있는지 확인
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Boolean flag = false;
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!token.isEmpty() && jwtUtil.isValidToken(token, userDetails)) {
                String url = request.getRequestURI();
                Optional<Member> findMember = memberRepository.findByUid(username);
                if (url.contains("/api/admin/controller/")) {
                    if (findMember.isPresent()) {
                        Role role = findMember.get().getRole();
                        if (role.equals(Role.ROLE_ADMIN) || role.equals(Role.ROLE_MANAGER)) {
                            flag = true;
                        }
                    }

                } else {
                    if (findMember.isPresent()) {
                        flag = true;
                    }
                }


            }
            if (flag) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            filterChain.doFilter(request, response);
        }

    }
}


