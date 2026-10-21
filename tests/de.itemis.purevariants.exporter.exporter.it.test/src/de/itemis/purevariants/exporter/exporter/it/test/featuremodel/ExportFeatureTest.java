// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.exporter.it.test.featuremodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.itemis.purevariants.exporter.exporter.FeatureModelExporter;
import de.itemis.purevariants.exporter.exporter.PureVariantsFacade;
import de.itemis.purevariants.exporter.exporter.it.test.fixtures.TestWithImportedProject;
import de.itemis.purevariants.exporter.model.Attribute;
import de.itemis.purevariants.exporter.model.Feature;
import de.itemis.purevariants.exporter.model.FeatureModel;
import de.itemis.purevariants.exporter.model.Relation;

public class ExportFeatureTest extends TestWithImportedProject{
	private static final String PROJECT_NAME="ProjectWithFeatureFields";
	private static final String PATH_TO_IMPORT_PROJECT = "resources/ProjectWithFeatureFields";
	private static final String MODEL_KEY = "/ProjectWithFeatureFields/FeatureModel.xfm";
	
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
	public void exportFeaturePrimitiveFields() throws Exception {
		exporter.export();
		
		FeatureModel model = exporter.getFeatureModel(MODEL_KEY);
		Feature feature = model.getFeature("MandatoryFeature");
		
		assertThat(feature.getId()).isNotBlank();
		assertThat(feature.getName()).isEqualTo("MandatoryFeature");
		assertThat(feature.getParentId()).isEqualTo(model.getFeature("FeatureModel").getId());
		assertThat(feature.getType()).isEqualTo("ps:feature");
		assertThat(feature.getVariantionType()).isEqualTo("ps:mandatory");
	}
	
	@Test
	public void rootFeatureHasNoParent() throws Exception {
		exporter.export();
		
		FeatureModel model = exporter.getFeatureModel(MODEL_KEY);
		Feature feature = model.getFeature("FeatureModel");
		
		assertThat(feature.getName()).isEqualTo("FeatureModel");
		assertThat(feature.getParentId()).isBlank();				
	}
	
	@Test
	public void exportFeatureAttributes() throws Exception {
		exporter.export();
		
		FeatureModel model = exporter.getFeatureModel(MODEL_KEY);
		Feature feature = model.getFeature("FeatureWithAttributes");
		
		assertThat(feature.getAttributes()).contains(expectedIntegerAttribute());
	}
	
	@Test
	public void exportFeatureConstraints() throws Exception {
		exporter.export();
		
		FeatureModel model = exporter.getFeatureModel(MODEL_KEY);
		Feature feature = model.getFeature("AutoSelectedFeatureWithConstraint");
		
		assertThat(feature.getConstraints()).contains(":SelectedOptionalFeature implies SELF");
	}
	
		
	@Test
	public void exportFeatureRelations() throws Exception {
		exporter.export();
		
		FeatureModel model = exporter.getFeatureModel(MODEL_KEY);
		Feature feature = model.getFeature("AutoExcludedFeatureWithRelation");
		
		assertThat(feature.getRelations()).contains(expectedRelation());
	}
	
	@Test
	public void exportFeatureRestrictions() throws Exception {
		exporter.export();
		
		FeatureModel model = exporter.getFeatureModel(MODEL_KEY);
		Feature feature = model.getFeature("FeatureWithRestriction");
		
		assertThat(feature.getRestrictions()).contains("SELF implies :SelectedOptionalFeature");
	}

	private Relation expectedRelation() {
		Relation relation = new Relation();
		relation.setName("ps:requires");
		List<String> parameters = new LinkedList<String>();
		parameters.add("iHvvVt224NepEaiPb/iUWH5rPsBEhViQ5t5");
		relation.setParameters(parameters);
		return relation;
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
