package com.github.svetanis.models.demo.observability;

import static com.github.svetanis.models.demo.DemoRunner.showAgent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.svetanis.models.demo.DemoRunner;
import com.github.svetanis.models.spi.ModelProviderRegistry;
import com.github.svetanis.models.spi.UsageDigest;
import com.google.adk.agents.LlmAgent;
import com.google.adk.plugins.agentanalytics.BigQueryAgentAnalyticsPlugin;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;

/**
 * Demo application for ADK Observability and BigQuery Analytics integration.
 *
 * <p>
 * Validates that the model-prism proxy correctly emits standardized ADK
 * observability spans and metrics, allowing the
 * {@link BigQueryAgentAnalyticsPlugin} to log inference events to BigQuery.
 */
public final class ObservabilityDemoApp {

	private static final String PROMPT = "In one sentence, what is OpenTelemetry?";

	public static void main(String[] args) throws Exception {
		// Register model-prism providers
		ModelProviderRegistry.registerAll();

		// 1. Boot OpenTelemetry BEFORE touching any ADK class.
		InMemorySpanExporter spanExporter = new InMemorySpanExporter();
		SdkTracerProvider tracerProvider = tracer(spanExporter);
		OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build();
		GlobalOpenTelemetry.set(sdk);

		// 2. Build the agent
		LlmAgent agent = new ObservabilityAgentProvider(DemoRunner.MODEL).get();

		// 3. Run the agent.
		try {
			runAgent(agent);
		} finally {
			tracerProvider.forceFlush().join(5, TimeUnit.SECONDS);
			printSpanSummary(spanExporter.captured());
			tracerProvider.shutdown().join(5, TimeUnit.SECONDS);
		}
	}

	private static SdkTracerProvider tracer(InMemorySpanExporter spanExporter) {
		SpanProcessor ssp = SimpleSpanProcessor.create(spanExporter);
		return SdkTracerProvider.builder()//
				.addSpanProcessor(ssp)//
				.build();//
	}

	private static void runAgent(LlmAgent agent) {
		showAgent(agent, PROMPT);
		UsageDigest digest = new UsageDigest();
		DemoRunner.run(agent, PROMPT, event -> {
			digest.record(event);
			DemoRunner.printEvent(event);
		});
		System.out.println();
		System.out.println("=".repeat(70));
		System.out.println(digest);
	}

	private static void printSpanSummary(List<SpanData> spans) {
		System.out.println("=".repeat(70));
		System.out.printf("OpenTelemetry spans captured: %d%n", spans.size());
		for (SpanData s : spans) {
			long durationMs = (s.getEndEpochNanos() - s.getStartEpochNanos()) / 1_000_000L;
			String msg = "  . %-40s %5d ms attrs=%d%n";
			System.out.printf(msg, s.getName(), durationMs, s.getAttributes().size());
		}
	}
}
