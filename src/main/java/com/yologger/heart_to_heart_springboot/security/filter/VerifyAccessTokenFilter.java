package com.yologger.heart_to_heart_springboot.security.filter;

import com.yologger.heart_to_heart_springboot.repository.MemberRepository;
import com.yologger.heart_to_heart_springboot.repository.entity.MemberEntity;
import com.yologger.heart_to_heart_springboot.security.service.MemberDetailsService;
import com.yologger.heart_to_heart_springboot.util.JwtManager;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
public class VerifyAccessTokenFilter extends OncePerRequestFilter {

    private final AntPathMatcher antPathMatcher;
    private final JwtManager jwtManager;
    private final MemberRepository memberRepository;
    private final List<String> excludedUrls;

    public VerifyAccessTokenFilter(JwtManager jwtManager, MemberRepository memberRepository, List<String> excludedUrls) {
        this.antPathMatcher = new AntPathMatcher();
        this.jwtManager = jwtManager;
        this.memberRepository = memberRepository;
        this.excludedUrls = excludedUrls;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Invoking VerifyAccessTokenFilter");

        // Handle excluded urls
        for (String url : excludedUrls) {
            if (antPathMatcher.match(url, request.getRequestURI())) {
                // Skip filtering.
                log.info("Skipping VerifyAccessTokenFilter");
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Check if 'Authorization' header exists.
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !StringUtils.hasText(authHeader)) {
            // Header
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json;charset=utf-8");
            // response.addHeader("key", "value");

            // Body
            JSONObject body = new JSONObject();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("code", -1);
            body.put("error", "Field 'Authorization' in headers must not be empty");
            response.getWriter().print(body);
            return;
        }

        // Check if 'Authorization' header includes 'Bearer'.
        if (!authHeader.startsWith("Bearer")) {
            // Header
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json;charset=utf-8");

            // Body
            JSONObject body = new JSONObject();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("code", -2);
            body.put("error", "'Authorization' header must start with 'Bearer'");
            response.getWriter().print(body);
            return;
        }

        // Check if 'Authorization' header includes access token.
        if (authHeader.trim().length() <= 6) {
            // Header
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json;charset=utf-8");

            // Body
            JSONObject body = new JSONObject();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("code", -3);
            body.put("error", "'Authorization' header must contain access token");
            response.getWriter().print(body);
            return;
        }

        String accessToken = authHeader.substring(7);

        if (accessToken == null || accessToken.trim().isEmpty()) {
            // Header
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json;charset=utf-8");

            // Body
            JSONObject body = new JSONObject();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("code", -4);
            body.put("error", "'Authorization' header must contain access token");
            response.getWriter().print(body);
            return;
        }

        try {
            Long memberId = jwtManager.verifyAccessTokenAndGetMemberId(accessToken);

            Optional<MemberEntity> result = memberRepository.findById(memberId);

            // Compare with ex-access token
            if (result.isEmpty()) {
                // Header
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType("application/json;charset=utf-8");

                // Body
                JSONObject body = new JSONObject();
                body.put("timestamp", LocalDateTime.now());
                body.put("code", -5);
                body.put("status", HttpStatus.BAD_REQUEST.value());
                body.put("error", "Member does not exist");
                response.getWriter().print(body);
                return;
            }

            if (!(accessToken.equals(result.get().getAccessToken()))) {
                // Header
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType("application/json;charset=utf-8");

                // Body
                JSONObject body = new JSONObject();
                body.put("timestamp", LocalDateTime.now());
                body.put("code", -6);
                body.put("status", HttpStatus.BAD_REQUEST.value());
                body.put("message", "Invalid access token.");
                response.getWriter().print(body);
                return;
            }

            // Verify access token
            jwtManager.verifyAccessToken(accessToken);
            filterChain.doFilter(request, response);
            return;

        } catch (UnsupportedEncodingException e) {

            // Header
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=utf-8");

            // Body
            JSONObject body = new JSONObject();
            body.put("timestamp", LocalDateTime.now());
            body.put("code", -7);
            body.put("status", HttpStatus.UNAUTHORIZED.value());
            body.put("message", e.getLocalizedMessage());
            response.getWriter().print(body);
            return;
        } catch (UnsupportedJwtException e) {
            // Header
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=utf-8");

            // Body
            JSONObject body = new JSONObject();
            body.put("timestamp", LocalDateTime.now());
            body.put("code", -8);
            body.put("status", HttpStatus.UNAUTHORIZED.value());
            body.put("message", e.getLocalizedMessage());
            response.getWriter().print(body);
            return;
        } catch (MalformedJwtException e) {
            // Header
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=utf-8");

            // Body
            JSONObject body = new JSONObject();
            body.put("timestamp", LocalDateTime.now());
            body.put("code", -9);
            body.put("status", HttpStatus.UNAUTHORIZED.value());
            body.put("message", e.getLocalizedMessage());
            response.getWriter().print(body);
            return;
        } catch (SignatureException e) {
            // Header
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=utf-8");

            // Body
            JSONObject body = new JSONObject();
            body.put("timestamp", LocalDateTime.now());
            body.put("code", -10);
            body.put("status", HttpStatus.UNAUTHORIZED.value());
            body.put("message", e.getLocalizedMessage());
            response.getWriter().print(body);
            return;
        } catch (ExpiredJwtException e) {
            // Header
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=utf-8");

            // Body
            JSONObject body = new JSONObject();
            body.put("timestamp", LocalDateTime.now());
            body.put("code", -11);
            body.put("status", HttpStatus.UNAUTHORIZED.value());
            body.put("message", e.getLocalizedMessage());
            response.getWriter().print(body);
            return;
        } catch (IllegalArgumentException e) {
            // Header
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=utf-8");

            // Body
            JSONObject body = new JSONObject();
            body.put("timestamp", LocalDateTime.now());
            body.put("code", -12);
            body.put("status", HttpStatus.UNAUTHORIZED.value());
            body.put("message", e.getLocalizedMessage());
            response.getWriter().print(body);
            return;
        }
    }
}
