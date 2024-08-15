package org.silluck.gateway.security;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Configuration
public class GatewayConfiguration {

    private final JwtFilter jwtFilter;

    public GatewayConfiguration(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-api", predicateSpec -> predicateSpec
                        .path("/signup", "/signin")
                        .and().method(HttpMethod.POST)
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE))
                        .uri("lb://USER-API")
                )
                .route("user-api", predicateSpec -> predicateSpec
                        .path("/customer/**", "/seller/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .removeRequestHeader(HttpHeaders.COOKIE))
                        .uri("lb://USER-API")
                )
                .build();
    }
}