// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.exporter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.xml.sax.SAXException;

import com.ps.consul.eclipse.core.model.IPVElement;
import com.ps.consul.eclipse.core.model.IPVFeatureModel;
import com.ps.consul.eclipse.core.model.IPVProperty;
import com.ps.consul.eclipse.core.model.IPVRelation;
import com.ps.consul.eclipse.core.model.IPVRestSet;
import com.ps.consul.eclipse.core.model.IPVRestriction;
import com.ps.consul.eclipse.core.model.IPVVariantModel;
import com.ps.consul.eclipse.core.model.ModelConstants;
import com.ps.consul.eclipse.core.project.ConfigSpace;
import com.ps.xml.ID;

import de.itemis.purevariants.exporter.model.Attribute;
import de.itemis.purevariants.exporter.model.Feature;
import de.itemis.purevariants.exporter.model.FeatureModel;
import de.itemis.purevariants.exporter.model.Relation;

public class FeatureModelExporter {
	private static final String FEATUREMODEL_FILE_EXT = "xfm";
	private static final String DEFINING_MODEL = "definingModel";
	private static final String EMPTY_STRING = "";
		
	private IContainer project;
	private PureVariantsFacade purevariants;

	private Map<String, FeatureModel> featureModels = new HashMap<>();

	public FeatureModelExporter(IProject project, PureVariantsFacade purevariants) {
		this.project = project;
		this.purevariants = purevariants;
	}

	public Map<String, FeatureModel> export() {
		try {
			processContainer(project);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return featureModels;
	}

	private void processContainer(IContainer container) throws CoreException {
		IResource[] members = container.members();
		for (IResource member : members) {
			if (member instanceof IContainer)
				processContainer((IContainer) member);
			else if (member instanceof IFile)
				processFile((IFile) member);
		}
	}

	private void processFile(IFile member) {
		if (FEATUREMODEL_FILE_EXT.equals(member.getFileExtension())) {
			IPVFeatureModel pvModel = tryOpenFeatureModel(member);
			if(null != pvModel) {
				exportModel(pvModel, member);
				purevariants.closeModel(pvModel);
			}
		}

	}

	private void exportModel(IPVFeatureModel pvModel, IFile member) {
		FeatureModel fm = new FeatureModel();
		fm.setId(pvModel.getID().toString());
		fm.setName(pvModel.getName());
		fm.setPath(member.getFullPath().toString());
		fm.setProject(member.getProject().getName());
		
		featureModels.put(member.getFullPath().toString(), fm);
		
		for (IPVElement pvFeature: pvModel.getFeatureList()) {
			fm.addFeature(exportFeature(pvFeature));
		}
	}

	private Feature exportFeature(IPVElement pvFeature) {
		Feature feature = new Feature();
		feature.setAttributes(collectFeatureAttributes(pvFeature));
		feature.setConstraints(collectRestrictions(pvFeature.getConstraintSet()));
		feature.setId(pvFeature.getID().toString());
		feature.setName(pvFeature.getName());
		feature.setParentId(defineParentId(pvFeature));
		feature.setRelations(collectRelations(pvFeature));
		feature.setRestrictions(collectRestrictions(pvFeature.getRestrictionSet()));
		feature.setType(pvFeature.getType());
		feature.setVariantionType(pvFeature.getVariationType());
		
		if (isConfigurationSpaceReference(pvFeature)) {
			Attribute attribute = createDefiningModelAttribute(pvFeature);
			feature.addAttribute(attribute);
		}
		return feature;
	}

	private boolean isConfigurationSpaceReference(IPVElement pvFeature) {
		return isVariantReference(pvFeature) || isInstanceReference(pvFeature);
	}

	private boolean isInstanceReference(IPVElement pvFeature) {
		return ModelConstants.INSTANCE_LINK_TYPE.equals(pvFeature.getType());
	}

	private boolean isVariantReference(IPVElement pvFeature) {
		return ModelConstants.VARIANT_LINK_TYPE.equals(pvFeature.getType());
	}
	
	private Attribute createDefiningModelAttribute(IPVElement configSpaceReference) {
		String value = EMPTY_STRING;
		if(isVariantReference(configSpaceReference)) {
			value = extractDefiningModelFromVariantReference(configSpaceReference);
		}else {
			value = extractDefiningModelFromInstanceReference(configSpaceReference);
		}
		
		Attribute attribute = new Attribute();
		attribute.setDefaultValue(value);
		attribute.setName(DEFINING_MODEL);
		attribute.setRange(EMPTY_STRING);
		attribute.setType(ModelConstants.ATTRIBUTES_STRING_TYPE);
		attribute.setValue(value);
		return attribute;
	}

	private String extractDefiningModelFromInstanceReference(IPVElement configSpaceReference) {
		String definingModelName = EMPTY_STRING;
		Path configSpacePath =  getConfigSpacePath(configSpaceReference);
		URL csUrl = tryFindMemberUrl(configSpacePath);
		ConfigSpace cs = tryCreateConfigSpace(csUrl.getFile());
		if(null != cs) {
			URL[] modelUrls = cs.getUsedModelUrls();
			IPVFeatureModel pvModel = purevariants.openFeatureModel(modelUrls[0]);
			if(null != pvModel) {
				definingModelName = pvModel.getName();
				purevariants.closeModel(pvModel);
			}	
			
		}
		return definingModelName;
	}
	
	private String extractDefiningModelFromVariantReference(IPVElement configSpaceReference) {
		if (configSpaceReference.getChildren().isEmpty()) {
			IPVVariantModel vdm = purevariants.openVariantModel(tryFindMemberUrl(createVariantPathRelativeToExportedProject(configSpaceReference)));
			IPVFeatureModel featureModel = (IPVFeatureModel)purevariants.getFeatureModels().get(0);
			String definingModelName = featureModel.getName();
			purevariants.closeModel(vdm);
			return definingModelName;
		}else {
			IPVElement modelRoot = configSpaceReference.getChildren().get(0);
			if (!modelRoot.getChildren().isEmpty()) {
				String rootDefiningName = modelRoot.getName().split(":")[modelRoot.getName().split(":").length - 1];
				return rootDefiningName;
			}
		}
		return EMPTY_STRING;
	}
	
	private Path getConfigSpacePath(IPVElement configSpaceReference) {
		String pathToConfigSpace = purevariants.getPropertyValue(configSpaceReference.getPropertyWithName(ModelConstants.ATTRIBUTES_CS_PATH_NAME), configSpaceReference);
		return new Path(pathToConfigSpace.substring(pathToConfigSpace.indexOf(")")+1));
	}

	private ConfigSpace tryCreateConfigSpace(String csURLFile) {
		try {
			return new ConfigSpace(new Path(csURLFile).toFile(), false);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Path createVariantPathRelativeToExportedProject(IPVElement configSpaceReference) {
		// Only sub-models have a VDM path. For instances, the path has to be extracted from the ConfigSpace reference.
		String pathAttribute = null;
		try{
			pathAttribute  = getVDMPath(configSpaceReference);
		}catch(Exception e) {
			pathAttribute = EMPTY_STRING;
		}
		return  new Path(pathAttribute.substring(pathAttribute.indexOf(")")+1));
	}

	private String getVDMPath(IPVElement configSpaceReference) {
		return purevariants.getPropertyValue(configSpaceReference.getPropertyWithName(ModelConstants.ATTRIBUTES_VDM_PATH_NAME), configSpaceReference);
	}
	
	private URL tryFindMemberUrl(Path path){
		try {
			return project.findMember(path).getLocationURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}


	private String defineParentId(IPVElement pvFeature) {
		ID parentId = pvFeature.getParent() != null ? pvFeature.getParent().getID(): new ID(EMPTY_STRING);
		return parentId.toString();
	}

	private IPVFeatureModel tryOpenFeatureModel(IFile member) {
		try {
			return purevariants.openFeatureModel(member.getLocationURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public FeatureModel getFeatureModel(String key) {
		return featureModels.get(key);
	}
	
	private List<Attribute> collectFeatureAttributes(IPVElement element) {
		List<Attribute> properties = new LinkedList<>();
		for (IPVProperty property : element.getPropertyList()) {
			Attribute attribute = new Attribute();
			attribute.setDefaultValue(purevariants.getPropertyValue(property, element));
			attribute.setFixed(property.isFixed());
			attribute.setInvisible(property.isInvisible());
			attribute.setName(property.getName());
			attribute.setRange(property.getRange());
			attribute.setType(property.getType());
			attribute.setValue(purevariants.getPropertyValue(property, element));
			properties.add(attribute);
		}
		return properties;
	}
	
	private List<String> collectRestrictions(IPVRestSet restrictions) {
		List<String> result = new LinkedList<>();
		if (null != restrictions && restrictions.hasRestrictionList()) {
			for (IPVRestriction restriction : restrictions.getRestrictionList()) {
				result.add(new String(restriction.getScriptList().get(0).getContent()));
			}
		}
		return result;
	}
	
	private List<Relation> collectRelations(IPVElement feature) {
		List<Relation> result = new LinkedList<>();
		for (IPVRelation relation : feature.getRelationListOfKlass(ModelConstants.DEPENDENCIES_CLASS)) {
			Relation exportRelation = new Relation();
			exportRelation.setName(relation.getType());
			exportRelation.setParameters(collectTargetList(relation));
			result.add(exportRelation);
		}
		return result;
	}
	
	private List<String> collectTargetList(IPVRelation relation) {
		if (relation.hasTargetList()) {
			return relation.getTargetList().stream().map(t -> t.getTarget()).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
}
