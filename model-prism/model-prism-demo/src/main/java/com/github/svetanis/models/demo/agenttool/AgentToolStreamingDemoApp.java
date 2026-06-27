package com.github.svetanis.models.demo.agenttool;

import static com.github.svetanis.models.demo.DemoRunner.printStreamingEvent;
import static com.github.svetanis.models.demo.DemoRunner.runStreaming;
import static com.github.svetanis.models.demo.DemoRunner.showAgent;
import static com.github.svetanis.models.demo.DemoRunner.showProviders;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.svetanis.models.demo.DemoRunner;
import com.github.svetanis.models.spi.ModelProvider;
import com.github.svetanis.models.spi.ModelProviderRegistry;
import com.google.adk.agents.LlmAgent;
import com.google.adk.tools.GoogleSearchAgentTool;

/**
 * Streaming Demo for {@link GoogleSearchAgentTool}.
 *
 * <p>
 * Validates the SSE streaming path with tool-dispatch to sub-agents.
 */
public class AgentToolStreamingDemoApp {

	private static final String PROMPT = """
			  What are the most significant large language model
			  releases from major AI labs so far in 2026? Give me
			  a brief summary of each with their key capabilities?
			""";

	public static void main(String[] args) {
		List<ModelProvider> registered = ModelProviderRegistry.registerAll();
		showProviders(registered);
		LlmAgent agent = new AgentProvider(DemoRunner.MODEL).get();
		showAgent(agent, PROMPT);
		
		System.out.println("Streaming response (each token printed as it arrives):");
		var count = new AtomicInteger(0);
		runStreaming(agent, PROMPT, event -> printStreamingEvent(event, count));
	}
}
