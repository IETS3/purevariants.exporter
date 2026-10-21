// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.exporter.it.test.featuremodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.itemis.purevariants.exporter.exporter.FeatureModelExporter;
import de.itemis.purevariants.exporter.exporter.PureVariantsFacade;
import de.itemis.purevariants.exporter.exporter.it.test.fixtures.TestWithImportedProject;
import de.itemis.purevariants.exporter.model.Attribute;
import de.itemis.purevariants.exporter.model.Feature;
import de.itemis.purevariants.exporter.model.FeatureModel;

public class ExportInstances extends TestWithImportedProject{
	private static final String PROJECT_NAME = "ProjectWithInstances";
	private static final String PATH_TO_IMPORT_PROJECT = "resources/" + PROJECT_NAME;
	private static final String MODEL_KEY = "/" + PROJECT_NAME + "/MainModel.xfm";
	
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
	public void exportDefiningModelAttributeFromInstanceReferenes() throws Exception {
		exporter.export();
		
		FeatureModel model = exporter.getFeatureModel(MODEL_KEY);
		Feature feature = model.getFeature("ReferenceToSomeDefiningModel");
		
		assertThat(feature.getAttributes()).contains(expectedDefinigModelAttribute());				
	}
	
	private Attribute expectedDefinigModelAttribute() {
		Attribute expecedAttribute = new Attribute();
		expecedAttribute.setDefaultValue("SomeDefiningModel");
		expecedAttribute.setName("definingModel");
		expecedAttribute.setRange("");
		expecedAttribute.setType("ps:string");
		expecedAttribute.setValue("SomeDefiningModel");
		return expecedAttribute;
	}
	
}
