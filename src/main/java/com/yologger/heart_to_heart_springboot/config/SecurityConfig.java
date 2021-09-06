package com.yologger.heart_to_heart_springboot.config;

import com.yologger.heart_to_heart_springboot.repository.MemberRepository;
import com.yologger.heart_to_heart_springboot.security.filter.VerifyAccessTokenFilter;
import com.yologger.heart_to_heart_springboot.security.service.MemberDetailsService;
import com.yologger.heart_to_heart_springboot.util.JwtManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@EnableWebSecurity
@Configuration
@AllArgsConstructor
@Log4j2
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MemberDetailsService memberDetailsService;
    private final JwtManager jwtManager;
    private final MemberRepository memberRepository;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(memberDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public VerifyAccessTokenFilter verifyAccessTokenFilter() {
        List<String> excludedUrls = new ArrayList<>();
        excludedUrls.add("/api/v1/auth/join");
        excludedUrls.add("/api/v1/auth/login");
        excludedUrls.add("/api/v1/auth/token");
        VerifyAccessTokenFilter filter = new VerifyAccessTokenFilter(jwtManager, memberRepository, excludedUrls);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors();

        http
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
            .addFilterBefore(verifyAccessTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        http
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
            // .antMatchers(HttpMethod.GET, "/test/api/test1").authenticated()
            .antMatchers(HttpMethod.GET, "/test/api/test2").authenticated()
            .anyRequest().permitAll();

        http
            .exceptionHandling()
            .authenticationEntryPoint((HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) -> {
                    log.info("authenticationEntryPoint()");
                    /**
                     * 인증(Authentication) 실패 시 호출
                     * (1) 잘못된 id 또는 password
                     * (2) Disabled user
                     * (3) locked user
                     * */
                    if (authException instanceof DisabledException) {

                    } else if (authException instanceof LockedException) {

                    } else if (authException instanceof BadCredentialsException) {

                    } else {

                    }
                })
                .accessDeniedHandler((HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) -> {
                    log.info("accessDeniedHandler()");
                    /**
                     * 인증은 성공했으나 권한이 없는(인가되지 않은, Unauthorized) 사용자
                     * */
            });
    }
}
