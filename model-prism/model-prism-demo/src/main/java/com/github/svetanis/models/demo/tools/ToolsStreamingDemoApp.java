package com.github.svetanis.models.demo.tools;

import static com.github.svetanis.models.demo.DemoRunner.printStreamingEvent;
import static com.github.svetanis.models.demo.DemoRunner.runStreaming;
import static com.github.svetanis.models.demo.DemoRunner.showAgent;
import static com.github.svetanis.models.demo.DemoRunner.showProviders;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.svetanis.models.demo.DemoRunner;
import com.github.svetanis.models.spi.ModelProvider;
import com.github.svetanis.models.spi.ModelProviderRegistry;
import com.google.adk.agents.LlmAgent;
import com.google.adk.tools.BaseTool;
import com.google.adk.tools.FunctionTool;

/**
 * Streaming Demo application for ADK function-calling (tool use).
 *
 * <p>
 * Validates the full tool-calling round-trip in SSE streaming mode.
 */
public final class ToolsStreamingDemoApp {

	private static final String PROMPT = """
			What time is right now?
			Also, what's the weather like in Phoenix?
			And finally, what is 42 multiplied by 137?
			""";

	private static final String INSTRUCTION = """
			You are a helpful assistant.
			Use the available tools to answer questions accurately.
			""";

	public static void main(String[] args) {
		List<ModelProvider> registered = ModelProviderRegistry.registerAll();
		showProviders(registered);
		LlmAgent agent = demoAgent();
		showAgent(agent, PROMPT);
		
		System.out.println("Streaming response (each token printed as it arrives):");
		var count = new AtomicInteger(0);
		runStreaming(agent, PROMPT, event -> printStreamingEvent(event, count));
	}

	private static LlmAgent demoAgent() {
		return LlmAgent.builder().name("tools-streaming-demo-agent") //
				.description("Helpful Assistant agent with tools (Streaming)") //
				.model(DemoRunner.MODEL) //
				.instruction(INSTRUCTION) //
				.tools(tools()) //
				.build();
	}

	private static List<BaseTool> tools() {
		List<BaseTool> tools = new ArrayList<>();
		tools.add(FunctionTool.create(DemoTools.class, "getCurrentTime"));
		tools.add(FunctionTool.create(DemoTools.class, "getWeather"));
		tools.add(FunctionTool.create(DemoTools.class, "calculate"));
		return tools;
	}
}
