package org.silluck.user.config;

import lombok.RequiredArgsConstructor;
import org.silluck.user.config.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter authenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        http     // rest api 설정
                .csrf(AbstractHttpConfigurer::disable)
                // csrf 비활성화 -> cookie를 사용하지 않으면 꺼도 된다.
                // (cookie를 사용할 경우 httpOnly(XSS 방어), sameSite(CSRF 방어)로 방어해야 한다.)
                .sessionManagement(
                        session -> session.sessionCreationPolicy
                                (SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    try {
                        authorize
                                .requestMatchers("/signup/**","signin/**",
                                        //swagger 허용 URL
                                        "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources",
                                        "/swagger-resources/**", "/configuration/ui", "/configuration/security", "/swagger-ui/**",
                                        "/webjars/**", "/swagger-ui.html")
                                .permitAll()// 해당 경로는 인증 없이 접근 가능
                                .requestMatchers("/customer/**") // 해당 경로는 인증이 필요
                                .hasAnyAuthority("CUSTOMER")
                        ;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                // jwt 관련 설정
                .addFilterBefore(this.authenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}