package com.github.svetanis.models.spi.routing;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class QualityPolicy implements RoutingPolicy {

	@Override
	public Optional<ModelMetadata> pick(List<ModelMetadata> candidates) {
		return candidates.stream().max(Comparator.comparingDouble(ModelMetadata::qualityScore));
	}

	@Override
	public String name() {
		return "quality";
	}
}
