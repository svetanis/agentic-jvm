package com.github.svetanis.models.spi.routing;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class LatencyPolicy implements RoutingPolicy {

	@Override
	public Optional<ModelMetadata> pick(List<ModelMetadata> candidates) {
		return candidates.stream().min(Comparator.comparingLong(ModelMetadata::typicalLatencyMs));
	}

	@Override
	public String name() {
		return "latency";
	}
}
