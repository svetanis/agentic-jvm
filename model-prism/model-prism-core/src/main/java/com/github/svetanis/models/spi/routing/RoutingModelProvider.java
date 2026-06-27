package com.github.svetanis.models.spi.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.svetanis.models.spi.ModelProvider;
import com.google.adk.models.BaseLlm;
import com.google.adk.models.LlmRegistry;
import com.google.api.client.util.Preconditions;

public class RoutingModelProvider implements ModelProvider {

	private static final Logger LOG = LoggerFactory.getLogger(RoutingModelProvider.class);
	private static final String PREFIX = "route";

	private final ModelCatalog catalog;

	public RoutingModelProvider(ModelCatalog catalog) {
		this.catalog = Preconditions.checkNotNull(catalog, "catalog");
	}

	@Override
	public String prefix() {
		return PREFIX;
	}

	@Override
	public BaseLlm createFromBareModelName(String bareModelName) {
		String msg1 = "No route named '%s' in catalog. Known routes: %s";
		String formatted = String.format(msg1, bareModelName, catalog.logicalNames());
		RoutingDecision decision = catalog.resolve(bareModelName)
				.orElseThrow(() -> new IllegalArgumentException(formatted));
		String msg2 = "Routing decision logical={} policy={} chosen={} candidates={}";
		int size = decision.consideredCandidates().size();
		LOG.info(msg2, decision.logicalName(), decision.policyName(), decision.chosen().fullModelName(), size);
		return LlmRegistry.getLlm(decision.chosen().fullModelName());
	}

	public RoutingDecision decide(String bareLogicalName) {
		String msg = "No route named '%s' in catalog";
		String formatted = String.format(msg, bareLogicalName);
		return catalog.resolve(bareLogicalName).orElseThrow(() -> new IllegalArgumentException(formatted));
	}

	public ModelCatalog catalog() {
		return catalog;
	}
}
