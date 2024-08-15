package org.silluck.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SilluckDomainApplication {

    public static void main(String[] args) {
        SpringApplication.run(SilluckDomainApplication.class, args);
    }
}
