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
import de.itemis.purevariants.exporter.model.Variant;
import de.itemis.purevariants.exporter.model.VariantModel;

public class WriteVariantModelToXmlTest {

	private static final String OPEN_VARIANT_MODEL = "<variantModel>";
	private static final String CLOSE_VARIANT_MODEL = "</variantModel>";
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
		ByteArrayOutputStream outContent = marshall(givenVariantModel("variantModelName"));

		assertThat(outContent.toString()).isXmlEqualTo(HEADER + expectedEmptyVariantModel());
	}
	
	@Test
	public void writeAttribtute() throws Exception {
		ByteArrayOutputStream outContent = marshall(givenAttribute());

		assertThat(outContent.toString()).isXmlEqualTo(HEADER + expectedAttribute());
	}
	
	@Test
	public void writeVariantWithEmptyFields() throws Exception {
		ByteArrayOutputStream outContent = marshall(givenVariant("feature01"));

		assertThat(outContent.toString()).isXmlEqualTo(HEADER + expectedVariant());
	}
	
	@Test
	public void writeFeatureWithAllFieldsFulfilled() throws Exception {
		ByteArrayOutputStream outContent = marshall(givenVariantWithFieldsFulfilled("feature01"));

		assertThat(outContent.toString()).isXmlEqualTo(HEADER + expectedVariantWithFieldsFulfilled());
	}
	
	private VariantModel givenVariantModel(String name) {
		return new VariantModel(name);
	}
	
	private Attribute givenAttribute() {
		Attribute attribute = new Attribute();
		attribute.setDefaultValue("someDefaultValue");
		attribute.setName("attributeName");
		attribute.setType("someType");
		attribute.setValue("someValue");
		return attribute;
	}
	
	private Variant givenVariant(String id) {
		Variant variant = new Variant();
		variant.setId(id);
		return variant;
	}
	
	private Variant givenVariantWithFieldsFulfilled(String id) {
		Variant feature = new Variant(id);
		feature.addAttribute(givenAttribute());
		feature.setSelector("some selector");
		feature.setState("some state");
		feature.setType("some type");
		return feature;
	}
	
	private String expectedEmptyVariantModel() {
		StringBuilder builder = new StringBuilder();
		builder.append(OPEN_VARIANT_MODEL);
		builder.append("<name>variantModelName</name>");
		builder.append("<variants/>");
		builder.append(CLOSE_VARIANT_MODEL);
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
	
	private String expectedVariant() {
		StringBuilder builder = new StringBuilder();
		builder.append("<variant>");
		builder.append("<attributes/>");
		builder.append("<id>feature01</id>");
		builder.append("</variant>");
		return builder.toString();
	}
	
	private String expectedVariantWithFieldsFulfilled() {
		StringBuilder builder = new StringBuilder();
		builder.append("<variant>");
		builder.append("<attributes>");
		builder.append(expectedAttribute());
		builder.append("</attributes>");
		builder.append("<id>feature01</id>");
		builder.append("<selector>some selector</selector>");
		builder.append("<state>some state</state>");
		builder.append("<type>some type</type>");
		builder.append("</variant>");
		return builder.toString();
	}
	
}
