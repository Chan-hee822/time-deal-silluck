package org.silluck.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("user-service", r -> r.path("/signin/**")
						.uri("lb://user-api"))
				.route("user-service", r -> r.path("/signup/**")
						.uri("lb://user-api"))
				.route("user-service", r -> r.path("/customer/**")
						.uri("lb://user-api"))
				.route("user-service", r -> r.path("/seller/**")
						.uri("lb://user-api"))
				.route("order-service", r -> r.path("/wishlist/**")
						.uri("lb://order-api"))
				.route("order-service", r -> r.path("/order/**")
						.uri("lb://order-api"))
				.route("order-service", r -> r.path("/product/**")
						.uri("lb://order-api"))
				.build();
	}
}
