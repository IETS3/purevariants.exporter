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

public class ExportSubmodelsTest extends TestWithImportedProject { 

	private static final String PROJECT_NAME = "ProjectWithSubmodels";
	private static final String PATH_TO_IMPORT_PROJECT = "resources/" + PROJECT_NAME;
	private static final String MAINMODEL_KEY = "/" + PROJECT_NAME + "/MainConfigSpace/MainVariant.vdm";
	
	private static final String SOMEVDM = "/SubmodelConfigSpace/SomeVariant.vdm";
	private static final String SOMEMODEL_KEY = "/" + PROJECT_NAME + SOMEVDM;
	private static final String EXPECTED_SOMEVDM_PATH = "$(PROJECT:" + PROJECT_NAME+ ")" + SOMEVDM;
	
	private static final String ANOTHERVDM = "/SubmodelConfigSpace/AnotherVariant.vdm";
	private static final String ANOTHERMODEL_KEY = "/" + PROJECT_NAME + ANOTHERVDM;
	private static final String EXPECTED_ANOTHERVDM_PATH = "$(PROJECT:" + PROJECT_NAME+ ")" + SOMEVDM;
	
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
	public void exportAllVariant() throws Exception {
		exporter.export();
			
		assertThat(exporter.getVariantModels()).size().isEqualTo(3);
		assertThat(exporter.getVariantModel(MAINMODEL_KEY)).isNotNull();
		assertThat(exporter.getVariantModel(SOMEMODEL_KEY)).isNotNull();
		assertThat(exporter.getVariantModel(ANOTHERMODEL_KEY)).isNotNull();
	}
	
	@Test
	public void variantReferenceContainDefiningModelAttribute() throws Exception {
		exporter.export();
		VariantModel model = exporter.getVariantModel(MAINMODEL_KEY);
		Variant inflexibleReference = model.getVariant("InflexibleVariantReference");
		Variant flexibleReference = model.getVariant("FlexibleVariantReference");
		
		assertThat(inflexibleReference.getAttribute("definingModel")).usingRecursiveComparison().isEqualTo(expectedDefinigModelAttribute());
		assertThat(inflexibleReference.getAttribute("ps:vdmpath")).usingRecursiveComparison().isEqualTo(expectedVdmPathModelAttribute(EXPECTED_SOMEVDM_PATH,true));
		
		assertThat(flexibleReference.getAttribute("definingModel")).usingRecursiveComparison().isEqualTo(expectedDefinigModelAttribute());
		assertThat(flexibleReference.getAttribute("ps:vdmpath")).usingRecursiveComparison().isEqualTo(expectedVdmPathModelAttribute(EXPECTED_ANOTHERVDM_PATH,false));
	}
	
	private Attribute expectedDefinigModelAttribute() {
		Attribute expecedAttribute = new Attribute();
		expecedAttribute.setDefaultValue("Submodel");
		expecedAttribute.setName("definingModel");
		expecedAttribute.setRange("");
		expecedAttribute.setType("ps:string");
		expecedAttribute.setValue("Submodel");
		return expecedAttribute;
	}
	
	private Attribute expectedVdmPathModelAttribute(String vdm, boolean fixed) {
		Attribute expecedAttribute = new Attribute();
		expecedAttribute.setDefaultValue(vdm);
		expecedAttribute.setFixed(fixed);
		expecedAttribute.setName("ps:vdmpath");
		expecedAttribute.setRange("");
		expecedAttribute.setType("ps:path");
		expecedAttribute.setValue(vdm);
		return expecedAttribute;
	}
}