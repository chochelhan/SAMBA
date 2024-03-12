package com.inysoft.samba.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import com.inysoft.samba.entity.Member;
import com.inysoft.samba.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> findMember = memberRepository.findByUid(username);
        if (!findMember.isPresent()) throw new UsernameNotFoundException("존재하지 않는 username 입니다.");
        String token = "km";
        if(findMember.get().getUidEncode()!=null) {
            token = findMember.get().getUidEncode();
        }

        return new User(findMember.get().getUid(),token, AuthorityUtils.createAuthorityList());

    }


}


