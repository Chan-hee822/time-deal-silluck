package org.silluck.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig2 {

    private final JwtFilter jwtFilter;

    public SecurityConfig2(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }


    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity httpSecurity) {

        return httpSecurity
                .cors(withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .headers(headers -> headers.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable))
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/signup/**", "/signin/**",
                                "/v2/api-docs/**", "/swagger-resources/**",
                                "/configuration/ui", "/configuration/security",
                                "/swagger-ui/**", "/webjars/**", "/swagger-ui.html")
                        .permitAll()
                        .anyExchange().hasAnyAuthority("SELLER", "USER")
                )
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint((exchange, denied) -> Mono.fromRunnable(() -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        }))
                        .accessDeniedHandler((exchange, denied) -> Mono.fromRunnable(() -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        })))
                .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}

