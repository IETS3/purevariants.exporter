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
import de.itemis.purevariants.exporter.model.Attribute;
import de.itemis.purevariants.exporter.model.Variant;
import de.itemis.purevariants.exporter.model.VariantModel;

public class ExportInstancesTest extends TestWithImportedProject{
	private static final String PROJECT_NAME = "ProjectWithInstances";
	private static final String PATH_TO_IMPORT_PROJECT = "resources/" + PROJECT_NAME;
	private static final String MODEL_KEY = "/" + PROJECT_NAME + "/MainModelConfigSpace/MainVariantModel.vdm";
	
	VariantModelExporter exporter;
	
	@Override
	public Path sourceProjectPath(){
		return Paths.get(PATH_TO_IMPORT_PROJECT);
	}
	
	@BeforeEach
	public void givenFeatureModelExporter() throws Exception {
		exporter = new VariantModelExporter(getProject(PROJECT_NAME), new PureVariantsFacade());
	}
	
	@Test
	public void exportInstantiatedFeatures() throws Exception {
		exporter.export();
		VariantModel model = exporter.getVariantModel(MODEL_KEY); 
				
		assertThat(model.getVariants()).size().isEqualTo(6);	
	}
	
	@Test
	public void instanceReferenceContainDefiningModelAttribute() throws Exception {
		exporter.export();
		VariantModel model = exporter.getVariantModel(MODEL_KEY);
		Variant variant = model.getVariant("SomeInstanceOfSomeDefiningModel");
		
		assertThat(variant.getAttribute("definingModel")).usingRecursiveComparison().isEqualTo(expectedDefinigModelAttribute());	
		assertThat(variant.getAttribute("ps:vdmpath")).usingRecursiveComparison().isEqualTo(expectedVdmPathModelAttribute());	
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
	
	private Attribute expectedVdmPathModelAttribute() {
		Attribute expecedAttribute = new Attribute();
		expecedAttribute.setDefaultValue("");
		expecedAttribute.setFixed(true);
		expecedAttribute.setName("ps:vdmpath");
		expecedAttribute.setRange("");
		expecedAttribute.setType("ps:path");
		expecedAttribute.setValue("");
		return expecedAttribute;
	}
}
