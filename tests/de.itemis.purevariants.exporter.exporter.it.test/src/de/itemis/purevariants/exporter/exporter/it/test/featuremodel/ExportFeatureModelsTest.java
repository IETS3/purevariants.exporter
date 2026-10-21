// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.exporter.it.test.featuremodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.itemis.purevariants.exporter.exporter.FeatureModelExporter;
import de.itemis.purevariants.exporter.exporter.PureVariantsFacade;
import de.itemis.purevariants.exporter.exporter.it.test.fixtures.TestWithImportedProject;
import de.itemis.purevariants.exporter.model.FeatureModel;

public class ExportFeatureModelsTest extends TestWithImportedProject{
	private static final String PROJECT_NAME = "ProjectWithTwoModels";
	private static final String PATH_TO_IMPORT_PROJECT = "resources/ProjectWithTwoModels";
	private static final String FIRST_MODEL_KEY = "/ProjectWithTwoModels/FirstFeatureModel.xfm";
	private static final String SECOND_MODEL_KEY = "/ProjectWithTwoModels/SomeFolder/SecondFeatureModel.xfm";
	
	FeatureModelExporter exporter;
	
	@Override
	public Path sourceProjectPath(){
		return Paths.get(PATH_TO_IMPORT_PROJECT);
	}
	
	@BeforeEach
	public void givenFeatureModelExporter() throws Exception {
		exporter = new FeatureModelExporter(getProject(PROJECT_NAME), new PureVariantsFacade());
	}

	
	@Test 
	public void exportAllFeatureModels() throws Exception {
		Map<String, FeatureModel> featureModels = exporter.export();

		assertThat(featureModels.values()).hasSize(2);
	}
	
	@Test
	public void exportFeatureModel() throws Exception {
		exporter.export();
		FeatureModel model = exporter.getFeatureModel(FIRST_MODEL_KEY); 
		
		assertThat(model.getId()).isEqualTo("iZvZha2DI1q5iELZF");
		assertThat(model.getName()).isEqualTo("FirstFeatureModel");
		assertThat(model.getPath()).isEqualTo("/ProjectWithTwoModels/FirstFeatureModel.xfm");
		assertThat(model.getProject()).isEqualTo(PROJECT_NAME);
	}
	
	@Test
	public void exportFeatureModelInNestedFolder() throws Exception {
		exporter.export();
		FeatureModel model = exporter.getFeatureModel(SECOND_MODEL_KEY); 
		
		assertThat(model.getName()).isEqualTo("SecondFeatureModel");
		assertThat(model.getPath()).isEqualTo("/ProjectWithTwoModels/SomeFolder/SecondFeatureModel.xfm");
		assertThat(model.getProject()).isEqualTo(PROJECT_NAME);
	}
	
}

