package com.github.svetanis.models.demo.routing;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.adk.agents.LlmAgent;

import jakarta.inject.Provider;

/**
 * Provider class for creating the observability-demo agent.
 */
public final class RoutingAgentProvider implements Provider<LlmAgent> {

	private static final String INSTRUCTION = """
			You are a helpful assistant.
			Please answer concisely.
			""";

	private final String model;

	public RoutingAgentProvider(String model) {
		this.model = checkNotNull(model, "model");
	}

	@Override
	public LlmAgent get() {
		return LlmAgent.builder()//
				.name("routing-demo-agent")//
				.description("Helpful Assistant")//
				.model(model)//
				.instruction(INSTRUCTION)//
				.build();
	}
}
