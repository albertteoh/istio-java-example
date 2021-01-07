package com.example.serviceb;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ServiceB {

	static final String serviceName = "service-b";

	public static void main(String[] args) {
		SpringApplication.run(ServiceB.class, args);
	}

	@Bean
	public Tracer tracer() {
		SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv()
				.withType(ConstSampler.TYPE)
				.withParam(1);

		ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv()
				.withLogSpans(true);

		Configuration config = new Configuration(serviceName)
				.withSampler(samplerConfig)
				.withReporter(reporterConfig);

		return config.getTracer();
	}
}

@RestController
class ServiceBController {
	@Autowired
	private Tracer tracer;

	@GetMapping("/ping")
	public ServiceBResponse ping() {
		Span span = tracer.buildSpan("ping").start();
		ServiceBResponse r = new ServiceBResponse(ServiceB.serviceName);
		span.finish();
		return r;
	}
}

class ServiceBResponse {
	private final String response;

	public ServiceBResponse(String response) {
		this.response = response;
	}

	public String getResponse() {
		return this.response;
	}
}
