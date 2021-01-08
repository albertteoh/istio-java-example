package com.example.serviceb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
public class ServiceB {

	static final String serviceName = "service-b";

	public static void main(String[] args) {
		SpringApplication.run(ServiceB.class, args);
	}

	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
		CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
		loggingFilter.setIncludeClientInfo(true);
		loggingFilter.setIncludeQueryString(true);
		loggingFilter.setIncludePayload(true);
		loggingFilter.setIncludeHeaders(true);
		loggingFilter.setMaxPayloadLength(64000);
		return loggingFilter;
	}
}

@RestController
class ServiceBController {
	@GetMapping("/ping")
	public PingResponse ping() {
		return new PingResponse(ServiceB.serviceName);
	}
}

