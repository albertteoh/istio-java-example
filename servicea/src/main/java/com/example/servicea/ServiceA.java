package com.example.servicea;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
class ServiceAController {
	OkHttpClient client = new OkHttpClient();

	private final Tracer tracer;

	public ServiceAController(Tracer tracer) {
		this.tracer = tracer;
	}

	@GetMapping("/ping")
	public ServiceAResponse ping() {
		Span span = tracer.buildSpan("ping").start();
		ServiceAResponse r = new ServiceAResponse(ServiceA.serviceName);
		span.finish();
		return r;
	}

	private String makeRequest(String url) throws IOException {
		Request.Builder requestBuilder = new Request.Builder()
				.url(url);

		tracer.inject(
				tracer.activeSpan().context(),
				Format.Builtin.HTTP_HEADERS,
				new RequestBuilderCarrier(requestBuilder)

		);

		Request request = requestBuilder
				.build();

		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}
}

class ServiceAResponse {
	private final String response;

	public ServiceAResponse(String response) {
		this.response = response;
	}

	public String getResponse() {
		return this.response;
	}
}
