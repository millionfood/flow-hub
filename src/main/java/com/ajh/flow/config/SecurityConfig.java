package com.ajh.flow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            //csrf 보안 기능 우선 비활성화 (개발 편의상) - 나중에 켜야함
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/user/join","/user/login","/user/api/**").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/user/login")
                    .loginProcessingUrl("/user/login")
                    .defaultSuccessUrl("/",true)
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutUrl("/user/logout")
                    .logoutSuccessUrl("/user/login")
                    .invalidateHttpSession(true) //로그아웃 시 톰캣 세션을 완전히 만료
                    .deleteCookies("JSESSIONID") //브라우저에 남아있는 세션 쿠키까지 깔끔하게 제거
            );

        return http.build();

    }
}
