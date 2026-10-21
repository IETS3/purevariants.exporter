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
public class Variant {
	
	@XmlElementWrapper(name = "attributes") 
	@XmlElement(name = "attribute")
	private List<Attribute> attributes = new LinkedList<>();
	
	@XmlElement
	private String id;
	
	@XmlElement
	private String name;
	
	@XmlElement
	private String parentId;
	
	@XmlElement
	private String selector;
	
	@XmlElement
	private String state;
	
	@XmlElement
	private String type;
	
		
	public Variant() {}
	
	public Variant(String id) {
		this.id = id;
	}
		
	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getParentId() {
		return parentId;
	}
	
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public String getSelector() {
		return selector;
	}
	
	public void setSelector(String selector) {
		this.selector = selector;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public boolean addAttribute(Attribute attribute) {
		return attributes.add(attribute);
	}
	
	public Attribute getAttribute(String name) {
		return attributes.stream().filter(it -> it.getName().equals(name)).findFirst().orElse(null);
	}
}
