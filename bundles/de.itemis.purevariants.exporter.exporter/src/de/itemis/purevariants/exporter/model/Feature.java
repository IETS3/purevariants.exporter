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
@XmlAccessorType(XmlAccessType.FIELD)
public class Feature {
	@XmlElementWrapper(name = "attributes") 
	@XmlElement(name = "attribute")
	private List<Attribute> attributes = new LinkedList<>();;
	
	@XmlElementWrapper(name = "constraints") 
	@XmlElement(name = "constraint")
	private List<String> constraints = new LinkedList<>();;
	
	@XmlElement
	private String id;
	
	@XmlElement
	private String name;
	
	@XmlElement
	private String parentId;
	
	@XmlElementWrapper(name = "relations") 
	@XmlElement(name = "relation")
	private List<Relation> relations = new LinkedList<>();;
	
	@XmlElementWrapper(name = "restrictions") 
	@XmlElement(name = "restriction")
	private List<String> restrictions = new LinkedList<>();;
	
	@XmlElement
	private String type;
	
	@XmlElement
	private String variationType;
	
	public Feature() {}
	
	public Feature(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public List<String> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<String> constraints) {
		this.constraints = constraints;
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

	public List<Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}

	public List<String> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(List<String> restrictions) {
		this.restrictions = restrictions;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVariantionType() {
		return variationType;
	}

	public void setVariantionType(String variationType) {
		this.variationType = variationType;
	}
	
	public boolean addAttribute(Attribute attribute) {
		return attributes.add(attribute);
	}
	
	public boolean addConstraint(String constraint) {
		return constraints.add(constraint);
	}
	
	public boolean addRestriction(String restrinction) {
		return restrictions.add(restrinction);
	}
	
	public boolean addRelation(Relation relation) {
		return relations.add(relation);
	}
}
