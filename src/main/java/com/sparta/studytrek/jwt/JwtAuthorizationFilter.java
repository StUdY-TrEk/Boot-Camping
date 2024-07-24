package com.sparta.studytrek.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.studytrek.common.exception.CustomException;
import com.sparta.studytrek.common.exception.ErrorCode;
import com.sparta.studytrek.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = jwtUtil.getTokenFromRequest(req);

        if (StringUtils.hasText(tokenValue)) {
            tokenValue = jwtUtil.substringToken(tokenValue);

            try {
                if (!jwtUtil.validateToken(tokenValue)) {
                    throw new CustomException(ErrorCode.TOKEN_VALIDATE);
                }

                Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
                String username = info.getSubject();

                setAuthentication(username);
            } catch (CustomException e) {
                handleException(res, "커스텀 필터에러", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (Exception e) {
                log.error("Authentication Error: {}", e.getMessage());
                handleException(res, "Authentication Error: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }
        filterChain.doFilter(req, res);
    }

    // 인증 설정 : 보안 컨텍스트에 인증 객체 설정
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성 : UserDetails를 기반으로 인증 객체 설정
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private void handleException(HttpServletResponse res, String message, int statusCode) throws IOException {
        res.setStatus(statusCode);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(new ObjectMapper().writeValueAsString(message));
    }
}
