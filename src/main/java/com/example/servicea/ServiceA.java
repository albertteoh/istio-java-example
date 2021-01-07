package com.example.servicea;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Scope;
import io.opentracing.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import io.opentracing.Tracer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ServiceA {

	static final String serviceName = "service-a";

	public static void main(String[] args) {
		SpringApplication.run(ServiceA.class, args);
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
class Controller {
	@Autowired
	private Tracer tracer;

	@GetMapping("/ping")
	public Response ping() {
		Span span = tracer.buildSpan("ping").start();
		Response r = new Response(ServiceA.serviceName);
		span.finish();
		return r;
	}
}

class Response {
	private final String response;

	public Response(String response) {
		this.response = response;
	}

	public String getResponse() {
		return this.response;
	}
}
