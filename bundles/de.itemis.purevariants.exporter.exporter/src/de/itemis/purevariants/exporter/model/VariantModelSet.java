// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.model;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@XmlAccessorType(XmlAccessType.FIELD)
public class VariantModelSet {
	
	@XmlElementWrapper(name = "variantModels") 
	@XmlElement(name = "variantModel")
	private Collection<VariantModel> variantModels;

	@XmlAttribute(name="exporterVersion")
	private String exporterVersion;
	
	public Collection<VariantModel> getVariantModels() {
		return variantModels;
	}

	public void setVariantModels(Collection<VariantModel> models) {
		this.variantModels = models;
	}

	public String getVersion() {
		return exporterVersion;
	}

	public void setVersion(String version) {
		this.exporterVersion = version;
	}
	
}