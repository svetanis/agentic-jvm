package com.github.svetanis.models.spi.routing;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface RoutingPolicy {

	Optional<ModelMetadata> pick(List<ModelMetadata> candidates);

	/**
	 * Human-readable name used in {@link RoutingDecision} logs. Defaults to the
	 * simple class name.
	 */
	default String name() {
		return getClass().getSimpleName();
	}
}
