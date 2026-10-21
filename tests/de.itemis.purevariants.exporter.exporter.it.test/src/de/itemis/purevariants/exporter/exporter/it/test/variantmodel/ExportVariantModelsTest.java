// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.exporter.it.test.variantmodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.itemis.purevariants.exporter.exporter.PureVariantsFacade;
import de.itemis.purevariants.exporter.exporter.VariantModelExporter;
import de.itemis.purevariants.exporter.exporter.it.test.fixtures.TestWithImportedProject;
import de.itemis.purevariants.exporter.model.VariantModel;

public class ExportVariantModelsTest extends TestWithImportedProject {

	private static final String PROJECT_NAME = "ProjectWithTwoModels";
	private static final String PATH_TO_IMPORT_PROJECT = "resources/ProjectWithTwoModels";
	private static final String FIRST_MODEL_KEY = "/ProjectWithTwoModels/FirstConfigurationSpace/FirstVariantModel.vdm";
	private static final String SECOND_MODEL_KEY = "/ProjectWithTwoModels/SomeFolder/SecondConfigurationSpace/SecondVariantModel.vdm";

	
	VariantModelExporter exporter;
	
	@Override
	public Path sourceProjectPath(){
		return Paths.get(PATH_TO_IMPORT_PROJECT);
	}
	
	@BeforeEach
	public void givenVariantModelExporter() throws Exception {
		exporter = new VariantModelExporter(getProject(PROJECT_NAME), new PureVariantsFacade());
	}
	
	@Test
	void exportVariantModel() throws Exception {
		exporter.export();
		VariantModel model = exporter.getVariantModel(FIRST_MODEL_KEY); 
		
		assertThat(model.getName()).isEqualTo("FirstVariantModel");
		assertThat(model.getPath()).isEqualTo(FIRST_MODEL_KEY);
		assertThat(model.getTargetModel()).isEqualTo("FirstFeatureModel");
	}
	
	@Test
	void exportVariantModelInNestedFolder() throws Exception {
		exporter.export();
		VariantModel model = exporter.getVariantModel(SECOND_MODEL_KEY); 
		
		assertThat(model.getName()).isEqualTo("SecondVariantModel");
		assertThat(model.getPath()).isEqualTo(SECOND_MODEL_KEY);
		assertThat(model.getTargetModel()).isEqualTo("SecondFeatureModel");
	}
}