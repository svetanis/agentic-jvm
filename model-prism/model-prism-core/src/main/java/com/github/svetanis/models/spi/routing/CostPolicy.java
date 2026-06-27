package com.github.svetanis.models.spi.routing;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class CostPolicy implements RoutingPolicy {

	@Override
	public Optional<ModelMetadata> pick(List<ModelMetadata> candidates) {
		return candidates.stream().min(Comparator.comparingDouble(ModelMetadata::combinedCostPer1kTokens));
	}

	@Override
	public String name() {
		return "cost";
	}

}
