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

public class ExportVariantFieldsTest extends TestWithImportedProject{
	private static final String PROJECT_NAME="ProjectWithFeatureFields";
	private static final String PATH_TO_IMPORT_PROJECT = "resources/ProjectWithFeatureFields";
	private static final String MODEL_KEY = "/ProjectWithFeatureFields/ConfigSpace/VariantModel.vdm";
	
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
	public void exportVariantPrimitiveFields() throws Exception {
		exporter.export();
		
		VariantModel model = exporter.getVariantModel(MODEL_KEY);
		Variant variant = model.getVariant("MandatoryFeature");
		
		assertThat(variant.getId()).isNotBlank();
		assertThat(variant.getName()).isEqualTo("MandatoryFeature");
		assertThat(variant.getParentId()).isEqualTo(model.getVariant("FeatureModel").getId());
		assertThat(variant.getSelector()).isEqualTo("ps:user");
		assertThat(variant.getState()).isEqualTo("ps:unselected");
		assertThat(variant.getType()).isEqualTo("ps:feature");
	}
	
	@Test
	public void rootFeatureHasNoParent() throws Exception {
		exporter.export();
		
		VariantModel model = exporter.getVariantModel(MODEL_KEY);
		Variant variant = model.getVariant("FeatureModel");
		
		assertThat(variant.getName()).isEqualTo("FeatureModel");
		assertThat(variant.getParentId()).isBlank();				
	}
	
	@Test
	public void exportFeatureAttributes() throws Exception {
		exporter.export();
		
		VariantModel model = exporter.getVariantModel(MODEL_KEY);
		Variant variant = model.getVariant("FeatureWithAttributes");
		
		assertThat(variant.getAttribute("anIntegerAtt")).usingRecursiveComparison().isEqualTo(expectedIntegerAttribute());
	}
	
	private Attribute expectedIntegerAttribute() {
		Attribute att = new Attribute();
		att.setDefaultValue("111");
		att.setFixed(true);
		att.setInherited(false);
		att.setInvisible(false);
		att.setName("anIntegerAtt");
		att.setRange("");
		att.setType("ps:integer");
		att.setValue("111");
		return att;
	}
	
	
	
}
