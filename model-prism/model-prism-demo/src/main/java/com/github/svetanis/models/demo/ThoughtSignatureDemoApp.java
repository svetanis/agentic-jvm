package com.github.svetanis.models.demo;

import static com.github.svetanis.models.demo.DemoRunner.showAgent;
import static com.github.svetanis.models.demo.DemoRunner.showProviders;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.github.svetanis.models.demo.tools.DemoTools;
import com.github.svetanis.models.spi.ModelProvider;
import com.github.svetanis.models.spi.ModelProviderRegistry;
import com.google.adk.agents.LlmAgent;
import com.google.adk.tools.BaseTool;
import com.google.adk.tools.FunctionTool;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

/**
 * Demo application for ADK thought signature functionality with a model-prism
 * provider.
 *
 * <p>
 * Equips an agent with a tool and sends a prompt that encourages reasoning
 * before tool execution. This validates that thought signatures returned by the
 * model (e.g., gemini-2.5-flash) are correctly intercepted, serialized, and
 * round-tripped through the ChatCompletionsRequest integration layer.
 */
public final class ThoughtSignatureDemoApp {

	private static final String PROMPT = "What time is right now? Please think carefully step by step before answering.";

	private static final String INSTRUCTION = """
			You are a helpful assistant.
			Use the available tools. You should use your reasoning capability
			to output thought signatures before making a tool call.
			""";

	public static void main(String[] args) {
		// Discover and register ALL providers on the classpath
		List<ModelProvider> registered = ModelProviderRegistry.registerAll();
		showProviders(registered);
		LlmAgent agent = demoAgent();
		showAgent(agent, PROMPT);

		// Use a custom event handler to intercept and print thought signatures
		DemoRunner.run(agent, PROMPT, event -> {
			DemoRunner.printEvent(event);
			event.content().flatMap(Content::parts).ifPresent(parts -> {
				for (Part part : parts) {
					part.thoughtSignature().ifPresent(sig -> {
						System.out.println("\n>>> THOUGHT SIGNATURE DETECTED <<<");
						System.out.println(Base64.getEncoder().encodeToString(sig));
						System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
					});
				}
			});
		});
	}

	private static LlmAgent demoAgent() {
		return LlmAgent.builder().name("thought-signature-demo-agent")
				.description("Agent testing thought signature capabilities")
				// Using gemini-2.5-flash or whatever supports thought_signature
				.model("gemini-2.5-flash").instruction(INSTRUCTION).tools(tools()).build();
	}

	private static List<BaseTool> tools() {
		List<BaseTool> tools = new ArrayList<>();
		tools.add(FunctionTool.create(DemoTools.class, "getCurrentTime"));
		return tools;
	}
}
