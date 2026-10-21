// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.exporter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.junit.jupiter.api.Test;

import de.itemis.purevariants.exporter.model.Attribute;
import de.itemis.purevariants.exporter.model.Feature;
import de.itemis.purevariants.exporter.model.FeatureModel;
import de.itemis.purevariants.exporter.model.Relation;

public class WriteFeatureModelToXmlTest {

	private static final String OPEN_FEATURE_MODEL = "<featureModel>";
	private static final String CLOSE_FEATURE_MODEL = "</featureModel>";
	private final static String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	private ByteArrayOutputStream marshall(Object element) throws JAXBException, PropertyException {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();

		JAXBContext contextObj = JAXBContext.newInstance(element.getClass());

		Marshaller marshallerObj = contextObj.createMarshaller();
		marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshallerObj.marshal(element, outContent);
		return outContent;
	}

	@Test
	public void writeFeatureModel() throws Exception {
		ByteArrayOutputStream outContent = marshall(givenFeatureModel("featureModelName"));

		assertThat(outContent.toString()).isXmlEqualTo(HEADER + expectedEmptyFeatureModel());
	}

	@Test
	public void writeAttribtute() throws Exception {
		ByteArrayOutputStream outContent = marshall(givenAttribute());

		assertThat(outContent.toString()).isXmlEqualTo(HEADER + expectedAttribute());
	}

	@Test
	public void writeAttribtuteWithRange() throws Exception {
		ByteArrayOutputStream outContent = marshall(givenAttributeWithRange());

		assertThat(outContent.toString()).isXmlEqualTo(HEADER + expectedAttributeWithRange());
	}

	@Test
	public void writeRelation() throws Exception {
		ByteArrayOutputStream outContent = marshall(givenRelation());

		assertThat(outContent.toString()).isXmlEqualTo(HEADER + expectedRelation());
	}

	@Test
	public void writeFeatureWithEmptyFields() throws Exception {
		ByteArrayOutputStream outContent = marshall(givenFeature("feature01", ""));

		assertThat(outContent.toString()).isXmlEqualTo(HEADER + expectedFeature());
	}

	@Test
	public void writeFeatureWithAllFieldsFulfilled() throws Exception {
		ByteArrayOutputStream outContent = marshall(givenFeatureWithFieldsFulfilled("feature01", "aParentId"));

		assertThat(outContent.toString()).isXmlEqualTo(HEADER + expectedFeatureWithFieldsFulfilled());
	}

	private FeatureModel givenFeatureModel(String name) {
		return new FeatureModel(name);
	}

	private Attribute givenAttribute() {
		Attribute attribute = new Attribute();
		attribute.setDefaultValue("someDefaultValue");
		attribute.setName("attributeName");
		attribute.setType("someType");
		attribute.setValue("someValue");
		return attribute;
	}

	private Attribute givenAttributeWithRange() {
		Attribute attribute = new Attribute();
		attribute.setDefaultValue("someDefaultValue");
		attribute.setName("attributeName");
		attribute.setType("someType");
		attribute.setValue("someValue");
		attribute.setRange(("[lowerBound, upperBound]"));
		return attribute;
	}

	private Relation givenRelation() {
		Relation relation = new Relation();
		relation.setName("relationName");
		relation.addParameter("someParameter");
		return relation;
	}

	private Feature givenFeature(String id, String parentId) {
		Feature feature = new Feature(id, id + "_name");
		feature.setParentId(parentId);
		return feature;
	}

	private Feature givenFeatureWithFieldsFulfilled(String id, String parentId) {
		Feature feature = new Feature(id, id + "_name");
		feature.addAttribute(givenAttribute());
		feature.addConstraint("some constraint");
		feature.addConstraint("another constraint");
		feature.setParentId(parentId);
		feature.addRelation(givenRelation());
		feature.addRelation(givenRelation());
		feature.addRestriction("some restriction");
		feature.addRestriction("another restriction");
		feature.setType("some type");
		feature.setVariantionType("some variation type");
		return feature;
	}
	
	private String expectedEmptyFeatureModel() {
		StringBuilder builder = new StringBuilder();
		builder.append(OPEN_FEATURE_MODEL);
		builder.append("<features/>");
		builder.append("<name>featureModelName</name>");
		builder.append(CLOSE_FEATURE_MODEL);
		return builder.toString();
	}

	private String expectedAttribute() {
		StringBuilder builder = new StringBuilder();
		builder.append("<attribute>");
		builder.append("<defaultValue>someDefaultValue</defaultValue>");
		builder.append("<fixed>false</fixed>");
		builder.append("<inherited>false</inherited>");
		builder.append("<invisible>false</invisible>");
		builder.append("<name>attributeName</name>");
		builder.append("<type>someType</type>");
		builder.append("<value>someValue</value>");
		builder.append("</attribute>");
		return builder.toString();
	}

	private String expectedAttributeWithRange() {
		StringBuilder builder = new StringBuilder();
		builder.append("<attribute>");
		builder.append("<defaultValue>someDefaultValue</defaultValue>");
		builder.append("<fixed>false</fixed>");
		builder.append("<inherited>false</inherited>");
		builder.append("<invisible>false</invisible>");
		builder.append("<name>attributeName</name>");
		builder.append("<range>[lowerBound, upperBound]</range>");
		builder.append("<type>someType</type>");
		builder.append("<value>someValue</value>");
		builder.append("</attribute>");
		return builder.toString();
	}

	private String expectedRelation() {
		StringBuilder builder = new StringBuilder();
		builder.append("<relation>");
		builder.append("<name>relationName</name>");
		builder.append("<parameters>");
		builder.append("<parameter>someParameter</parameter>");
		builder.append("</parameters>");
		builder.append("</relation>");
		return builder.toString();
	}

	private String expectedFeature() {
		StringBuilder builder = new StringBuilder();
		builder.append("<feature>");
		builder.append("<attributes/>");
		builder.append("<constraints/>");
		builder.append("<id>feature01</id>");
		builder.append("<name>feature01_name</name>");
		builder.append("<parentId/>");
		builder.append("<relations/>");
		builder.append("<restrictions/>");
		builder.append("</feature>");
		return builder.toString();
	}

	private String expectedFeatureWithFieldsFulfilled() {
		StringBuilder builder = new StringBuilder();
		builder.append("<feature>");
		builder.append("<attributes>");
		builder.append(expectedAttribute());
		builder.append("</attributes>");
		builder.append("<constraints>");
		builder.append("<constraint>some constraint</constraint>");
		builder.append("<constraint>another constraint</constraint>");
		builder.append("</constraints>");
		builder.append("<id>feature01</id>");
		builder.append("<name>feature01_name</name>");
		builder.append("<parentId>aParentId</parentId>");
		builder.append("<relations>");
		builder.append(expectedRelation());
		builder.append(expectedRelation());
		builder.append("</relations>");
		builder.append("<restrictions>");
		builder.append("<restriction>some restriction</restriction>");
		builder.append("<restriction>another restriction</restriction>");
		builder.append("</restrictions>");
		builder.append("<type>some type</type>");
		builder.append("<variationType>some variation type</variationType>");
		builder.append("</feature>");
		return builder.toString();
	}
}
