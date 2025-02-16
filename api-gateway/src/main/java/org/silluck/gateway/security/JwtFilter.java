package org.silluck.gateway.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.silluck.gateway.common.UserVo;
import org.silluck.gateway.config.JwtAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter implements WebFilter, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtAuthenticationProvider jwtTokenProvider;
    private ApplicationContext applicationContext;

    public JwtFilter(JwtAuthenticationProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String requestPath = request.getURI().getPath();
        logger.info("Request path: {}", requestPath);

        // 토큰 없어도 되는 URL 체크
        if (isPublicPath(requestPath)) {
            logger.info("Path {} is excluded from JWT filtering", requestPath);
            return chain.filter(exchange);  // 토큰 없이 request 가능
        }

        String token = exchange.getRequest().getHeaders().getFirst("X-AUTH-TOKEN");

        logger.info("Authorization header: {}", token);

        // 토큰이 유효한지 검증
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            logger.info("유효하지 않은 토큰");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 유저 정보를 컨텍스트에 저장 (추후 마이크로서비스에서 사용 가능)
        UserVo userVo = jwtTokenProvider.getUserVo(token);

        try {
            ServerHttpRequest mutatedRequest = request
                    .mutate()
                    .header("X-USER-ID", String.valueOf(userVo.getId()))
                    .build();
            // roles에서 권한 정보를 추출하여 GrantedAuthority로 변환
            String role = jwtTokenProvider.getRoleFromToken(token);
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

            // Authentication 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userVo.getEmail(), null, authorities);

            return chain.filter(exchange.mutate().request(mutatedRequest).build())
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (ExpiredJwtException e) {
            logger.error("Invalid or expired refresh token");
        }

        return chain.filter(exchange);
    }

    private boolean isPublicPath(String requestURI) {
        // 허용 URL 정의
        List<String> publicPaths = Arrays.asList(
                "/signup/customer", "/signin/customer",
                "/signup/seller", "/signin/seller"
        );

        return publicPaths.stream().anyMatch(requestURI::startsWith);
    }
}
