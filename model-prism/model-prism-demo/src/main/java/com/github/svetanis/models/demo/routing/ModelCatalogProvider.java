package com.github.svetanis.models.demo.routing;

import static java.util.Arrays.asList;

import com.github.svetanis.models.spi.routing.CostPolicy;
import com.github.svetanis.models.spi.routing.LatencyPolicy;
import com.github.svetanis.models.spi.routing.ModelCatalog;
import com.github.svetanis.models.spi.routing.ModelMetadata;
import com.github.svetanis.models.spi.routing.QualityPolicy;

import jakarta.inject.Provider;

public final class ModelCatalogProvider implements Provider<ModelCatalog> {

	private static final ModelMetadata GROQ_8B = groq8B();
	private static final ModelMetadata GROQ_70B = groq70B();
	private static final ModelMetadata OPENROUTER_8B = openrouter8B();

	@Override
	public ModelCatalog get() {
		return ModelCatalog.builder()//
				.route("cheapest-7b", new CostPolicy(), asList(GROQ_8B, OPENROUTER_8B))//
				.route("fastest-7b", new LatencyPolicy(), asList(GROQ_8B, OPENROUTER_8B))//
				.route("quality-70b", new QualityPolicy(), asList(GROQ_8B, OPENROUTER_8B, GROQ_70B))//
				.build();
	}

	private static ModelMetadata groq8B() {
		String model = "groq/llama3-8b-8192";
		return new ModelMetadata(model, 0.05, 0.08, 350, 0.70);
	}

	private static ModelMetadata groq70B() {
		String model = "groq/llama3-70-8192";
		return new ModelMetadata(model, 0.59, 0.79, 700, 0.91);
	}

	private static ModelMetadata openrouter8B() {
		String model = "openrouter/meta-llama/llama-3-8b-instruct";
		return new ModelMetadata(model, 0.02, 0.04, 600, 0.68);
	}

}
