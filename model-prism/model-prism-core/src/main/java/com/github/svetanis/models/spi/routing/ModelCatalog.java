package com.github.svetanis.models.spi.routing;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.api.client.util.Preconditions;
import com.google.common.collect.ImmutableMap;

public final class ModelCatalog {

	private record Entry(RoutingPolicy policy, List<ModelMetadata> candidates) {
	}

	private final ImmutableMap<String, Entry> routes;

	private ModelCatalog(Map<String, Entry> routes) {
		this.routes = ImmutableMap.copyOf(new LinkedHashMap<>(routes));
	}

	public Optional<RoutingDecision> resolve(String logicalName) {
		Entry entry = routes.get(logicalName);
		if (entry == null) {
			return Optional.empty();
		}
		return entry.policy().pick(entry.candidates()).map(chosen -> rd(logicalName, chosen, entry));
	}

	private RoutingDecision rd(String logicalName, ModelMetadata chosen, Entry entry) {
		String name = entry.policy().name();
		return new RoutingDecision(logicalName, chosen, name, entry.candidates());
	}

	public List<String> logicalNames() {
		return List.copyOf(routes.keySet());
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private Builder() {
		}

		private Map<String, Entry> routes = new LinkedHashMap<>();

		public Builder route(String logicalName, RoutingPolicy policy, ModelMetadata... candidates) {
			return route(logicalName, policy, List.of(candidates));
		}

		public Builder route(String logicalName, RoutingPolicy policy, List<ModelMetadata> candidates) {
			Preconditions.checkNotNull(logicalName, "logicalName");
			Preconditions.checkNotNull(policy, "policy");
			Preconditions.checkNotNull(candidates, "candidates");
			if (StringUtils.isBlank(logicalName)) {
				throw new IllegalArgumentException("logicalName must not be blank");
			}
			if (candidates.isEmpty()) {
				throw new IllegalArgumentException("at least one candidate is required for route " + logicalName);
			}
			if (routes.containsKey(logicalName)) {
				throw new IllegalArgumentException("duplicate route: " + logicalName);
			}
			routes.put(logicalName, new Entry(policy, List.copyOf(candidates)));
			return this;
		}

		public ModelCatalog build() {
			return new ModelCatalog(routes);
		}
	}
}
