// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
public class FeatureModel {

	@XmlElement
	private String id;

	@XmlElement
	private String name;
	
	@XmlElement
	private String path;
	
	@XmlElement
	private String project;
	
	@XmlElementWrapper(name = "features") 
	@XmlElement(name = "feature")
	private List<Feature> features = new LinkedList<Feature>();
	
	public FeatureModel() {}
	
	public FeatureModel(String name) {
		this.name = name;
	}

	public boolean addFeature(Feature feature) {
		return features.add(feature);
	}
	
	public Feature getFeature(String name) {
		return features.stream().filter(it -> name.equals(it.getName())).findFirst().orElse(null);
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	@Override
	public int hashCode() {
		return Objects.hash(features, name, path, project);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeatureModel other = (FeatureModel) obj;
		return Objects.equals(features, other.features) && Objects.equals(name, other.name)
				&& Objects.equals(path, other.path) && Objects.equals(project, other.project);
	}

	
	
}
