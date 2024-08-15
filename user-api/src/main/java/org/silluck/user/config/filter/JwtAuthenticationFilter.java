//package org.silluck.user.config.filter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.silluck.domain.config.JwtAuthenticationProvider;
//import org.silluck.domain.domain.common.UserVo;
//import org.silluck.user.service.customer.CustomerService;
//import org.silluck.user.service.seller.SellerService;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtAuthenticationProvider tokenProvider;
//    private final CustomerService customerService;
//    private final SellerService sellerService;
//
//    // 실제 필터링 로직은 doFilterInternal 에 들어감
//    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String requestURI = request.getRequestURI();
//
//        // 토큰 없어도 되는 URL 체크
//        if (isPublicPath(requestURI)) {
//            filterChain.doFilter(request, response);  // 토큰 없이 request 가능
//            return;
//        }
//
//        // Request Header 에서 토큰을 꺼냄
//        String token = request.getHeader("X-AUTH-TOKEN");
//
//        // 토큰이 유효한지 확인
//        if (token != null && tokenProvider.validateToken(token)) {
//            UserVo userVo = tokenProvider.getUserVo(token);
//            String role = tokenProvider.getRoleFromToken(token);
//
//            // roles에서 권한 정보를 추출하여 GrantedAuthority로 변환
//            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
//
//            // Authentication 객체 생성
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(userVo.getEmail(), null, authorities);
//
//            // SecurityContext에 Authentication 객체 저장
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            // 고객 정보가 있는지 확인
//            if (role.equals("CUSTOMER")) {
//                customerService.findByIdAndEmail(userVo.getId(), userVo.getEmail()).orElseThrow(
//                        () -> new ServletException("Invalid Access"));  // 토큰이 유효한지 확인 위해 DB 조회
//            } else if (role.equals("SELLER")) {
//                sellerService.findByIdAndEmail(userVo.getId(), userVo.getEmail()).orElseThrow(
//                        () -> new ServletException("Invalid Access"));  // 토큰이 유효한지 확인 위해 DB 조회
//            }
//        } else {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("text/plain; charset=UTF-8");
//            response.getWriter().write("Invalid Access or Expired Token");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private boolean isPublicPath(String requestURI) {
//        // 허용 URL 정의
//        List<String> publicPaths = Arrays.asList("/signup", "/signin",
//                //swagger 허용 URL
//                "/v2/api-docs", "/v3/api-docs", "/swagger-resources",
//                "/configuration", "/swagger-ui", "/webjars/**", "/swagger-ui.html"
//        );
//
//        return publicPaths.stream().anyMatch(requestURI::startsWith);
//    }
//}