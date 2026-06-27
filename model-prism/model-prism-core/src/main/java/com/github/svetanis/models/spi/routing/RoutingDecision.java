package com.github.svetanis.models.spi.routing;

import java.util.List;

import com.google.api.client.util.Preconditions;

public record RoutingDecision(String logicalName, ModelMetadata chosen, String policyName,
		List<ModelMetadata> consideredCandidates) {

	public RoutingDecision {
		Preconditions.checkNotNull(logicalName, "logicalName");
		Preconditions.checkNotNull(chosen, "chosen");
		Preconditions.checkNotNull(policyName, "policyName");
		consideredCandidates = List.copyOf(consideredCandidates);
	}

	@Override
	public String toString() {
		String msg = "RoutingDecision[logicalName=%s, chosen=%s, policy=%s, candidates=%s]";
		return String.format(msg, logicalName, chosen.fullModelName(), policyName, consideredCandidates.size());
	}
}
