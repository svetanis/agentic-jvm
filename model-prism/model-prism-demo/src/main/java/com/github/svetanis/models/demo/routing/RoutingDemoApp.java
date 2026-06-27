package com.github.svetanis.models.demo.routing;

import com.github.svetanis.models.demo.DemoRunner;
import com.github.svetanis.models.spi.ModelProviderRegistry;
import com.github.svetanis.models.spi.routing.ModelCatalog;
import com.github.svetanis.models.spi.routing.RoutingDecision;
import com.github.svetanis.models.spi.routing.RoutingModelProvider;
import com.google.adk.agents.LlmAgent;
import com.google.adk.models.LlmRegistry;
import com.google.adk.plugins.agentanalytics.BigQueryAgentAnalyticsPlugin;

/**
 * Demo application for ADK Observability and BigQuery Analytics integration.
 *
 * <p>
 * Validates that the model-prism proxy correctly emits standardized ADK
 * observability spans and metrics, allowing the
 * {@link BigQueryAgentAnalyticsPlugin} to log inference events to BigQuery.
 */
public final class RoutingDemoApp {

	private static final String PROMPT = "In one sentence, what is OpenTelemetry?";

	public static void main(String[] args) throws Exception {
		// 1. Register the physical providers via the existing SPI.
		ModelProviderRegistry.registerAll();

		// 2. Build the routing catalog and register the routing provider.
		ModelCatalog catalog = new ModelCatalogProvider().get();
		RoutingModelProvider routing = new RoutingModelProvider(catalog);
		LlmRegistry.registerLlm(routing.modelPattern(), routing::create);

		// 3. Print the decision for every logical route.
		showDecision(catalog, routing);

		// 4. Optional live run
		LlmAgent agent = new RoutingAgentProvider("route/cheapest-7b").get();
		DemoRunner.showAgent(agent, PROMPT);
		DemoRunner.run(agent, PROMPT);
	}

	private static void showDecision(ModelCatalog catalog, RoutingModelProvider routing) {
		System.out.println("Routing decisions:");
		System.out.println("-".repeat(72));
		String msg = " route/%-12s -> %-50s policy=%-8s candidates=%d%n";
		for (String logical : catalog.logicalNames()) {
			RoutingDecision rd = routing.decide(logical);
			String name = rd.chosen().fullModelName();
			int size = rd.consideredCandidates().size();
			System.out.printf(msg, logical, name, rd.policyName(), size);
		}
		System.out.println();
	}
}