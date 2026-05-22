package com.ajh.flow.service;

import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.domain.PrincipalDetails;
import com.ajh.flow.domain.User;
import com.ajh.flow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @NonNull
    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        if(user.getUseYn() == UseYn.N){
            throw new DisabledException("정지되거나 탈퇴한 계정입니다.");
        }

        return new PrincipalDetails(user);
    }

}
