// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.model;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@XmlAccessorType(XmlAccessType.NONE)
public class VariantModel {
	
	@XmlElement
	private String name;
	
	@XmlElement
	private String path;
	
	@XmlElement
	private String targetModel;
	
	@XmlElementWrapper(name = "variants") 
	@XmlElement(name = "variant")
	private List<Variant> variants = new LinkedList<Variant>();
	
	public VariantModel() {}
	
	public VariantModel(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	public String getTargetModel() {
		return targetModel;
	}
	
	public void setTargetMode(String target) {
		this.targetModel = target;
	}
	
	public List<Variant> getVariants() {
		return variants;
	}
	
	public void setVariants(List<Variant> variants) {
		this.variants = variants;
	}
	
	public boolean addVariant(Variant feature) {
		return variants.add(feature);
	}
	
	public Variant getVariant(String name) {
		return variants.stream().filter(it -> name.equals(it.getName())).findFirst().orElse(null);
	}
}

