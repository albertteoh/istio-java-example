package com.example.servicea;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.io.IOException;
import java.util.Map;

@SpringBootApplication
public class ServiceA {
	// HTTP headers to propagate for distributed tracing are documented at
	// https://istio.io/docs/tasks/telemetry/distributed-tracing/overview/#trace-context-propagation
	final static String[] headersToPropagate = {
			// All applications should propagate x-request-id. This header is
			// included in access log statements and is used for consistent trace
			// sampling and log sampling decisions in Istio.
			"x-request-id",

			// b3 trace headers. Compatible with Zipkin, OpenCensusAgent, and
			// Stackdriver Istio configurations.
			"x-b3-traceid",
			"x-b3-spanid",
			"x-b3-parentspanid",
			"x-b3-sampled",
			"x-b3-flags",
	};

	static final String serviceName = "service-a";

	public static void main(String[] args) {
		SpringApplication.run(ServiceA.class, args);
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
class ServiceAController {
	OkHttpClient client = new OkHttpClient();

	@Value("${OUTBOUND_HOST:localhost}")
	private String outboundHost;

	@Value("${OUTBOUND_PORT:8082}")
	private String outboundPort;

	@GetMapping("/ping")
	public PingResponse ping(@RequestHeader Map<String, String> headers) throws IOException {
		String url = String.format("http://%s:%s/ping", outboundHost, outboundPort);
		String response = makeRequest(url, headers);
		PingResponse r = new PingResponse(ServiceA.serviceName + " -> " + response);
		return r;
	}

	private String makeRequest(String url, Map<String, String> headers) throws IOException {
		Request.Builder requestBuilder = new Request.Builder().url(url);

		for (String header : ServiceA.headersToPropagate) {
			String value = headers.get(header);
			if (value != null) {
				requestBuilder.header(header,value);
			}
		}
		Request request = requestBuilder.build();

		try (Response response = client.newCall(request).execute()) {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(response.body().string(), PingResponse.class).getResponse();
		}
	}
}

