package com.example.servicea;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PingResponse {
	private final String response;

	public PingResponse(@JsonProperty("response") String response) {
		this.response = response;
	}

	public String getResponse() {
		return this.response;
	}
}
