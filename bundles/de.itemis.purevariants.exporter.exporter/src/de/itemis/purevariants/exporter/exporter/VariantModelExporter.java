// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.exporter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import com.ps.consul.eclipse.core.model.IPVVariantElement;
import com.ps.consul.eclipse.core.model.IPVVariantModel;
import com.ps.consul.eclipse.core.model.ModelConstants;
import com.ps.consul.eclipse.core.project.ConfigSpace;
import com.ps.xml.ID;

import de.itemis.purevariants.exporter.model.Attribute;
import de.itemis.purevariants.exporter.model.Variant;
import de.itemis.purevariants.exporter.model.VariantModel;

public class VariantModelExporter {
	private static final String VARIANTMODEL_FILE_EXT = "vdm";

	private static final String EMPTY_STRING = "";
	
	private IContainer project;
	private PureVariantsFacade purevariants;


	private Map<String, VariantModel> variantModels = new HashMap<>();

	public VariantModelExporter(IProject project, PureVariantsFacade purevariants) {
		this.project = project;
		this.purevariants = purevariants;
	}
	
	public Map<String, VariantModel> export() {
		try {
			processContainer(project);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return variantModels;
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
		if (VARIANTMODEL_FILE_EXT.equals(member.getFileExtension())) {
			IPVVariantModel pvModel = tryOpenVariantModel(member);
			if(null != pvModel) {
				exportModel(pvModel, member);
				purevariants.closeModel(pvModel);
			}
		}
	}
	
	private void exportModel(IPVVariantModel pvModel, IFile member) {
		IPVFeatureModel pvFeatureModel = purevariants.getFeatureModelFor(pvModel);
		
		VariantModel exportVariantModel = new VariantModel();
		exportVariantModel.setName(pvModel.getName());
		exportVariantModel.setPath(member.getFullPath().toString());
		exportVariantModel.setTargetMode(pvFeatureModel.getName());
		variantModels.put(member.getFullPath().toString(), exportVariantModel);
				
		IPVElement root = pvFeatureModel.getRoot();
		exportVariantModel.addVariant(exportVariant(EMPTY_STRING, root, pvModel));
		
		exportVariants(root, pvModel, exportVariantModel);
	}

	private void exportVariants(IPVElement variant, IPVVariantModel pvModel, VariantModel exModel) {
		Iterator<IPVElement> children = variant.getChildren().iterator();
		while (children.hasNext()) {
			IPVElement child = children.next();
			exModel.addVariant(exportVariant(defineParentId(child), child, pvModel));
			exportVariants(child, pvModel, exModel);
		}
	}
	
	private Variant exportVariant(String parentId, IPVElement variant, IPVVariantModel pvModel) {
		Variant exportVariant = new Variant(variant.getID().toString());
		exportVariant.setAttributes(collectVariantAttributes(variant, pvModel));
		exportVariant.setId(variant.getID().toString());
		exportVariant.setName(variant.getName());
		exportVariant.setParentId(parentId);
		
		exportVariant.setSelector(collectSelector(variant, pvModel));
		exportVariant.setState(purevariants.getSelectionState(variant));
		
		exportVariant.setType(variant.getType());
		
		if (ModelConstants.VARIANT_LINK_TYPE.equals(variant.getType())) {
			Attribute attribute = exportDefiningModelAttribute(variant);
			exportVariant.addAttribute(attribute);
		}
	
						
		return exportVariant;
	}
	
		
	private List<Attribute> collectVariantAttributes(IPVElement element, IPVVariantModel pvModel) {
		IPVVariantElement selection = pvModel.getSelectionOfReference(element);
		
		List<Attribute> attributes = new LinkedList<>();
		for (IPVProperty property : element.getPropertyList()) {
				Attribute attribute = new Attribute();
				attribute.setDefaultValue(purevariants.getPropertyValue(property, element));
				attribute.setFixed(property.isFixed());
				attribute.setName(property.getName());
				attribute.setRange(property.getRange());
				attribute.setType(property.getType());
				attribute.setValue(purevariants.getPropertyValue(property, element));

				if (property.isInheritable()) {
					if (null != selection) {
						IPVProperty inheritedProperty = pvModel.getValueForProperty(property);
						try {
							attribute.setValue(purevariants.getPropertyValue(inheritedProperty, element));
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						attribute.setValue(EMPTY_STRING);
					}
				} else {
					attribute.setValue(purevariants.getPropertyValue(property, element));
				}
				attributes.add(attribute);
		}
		return attributes;
	}

	/*
	 * When the feature is not selected, the selection is null; return the default value then.
	 */
	private String collectSelector(IPVElement element, IPVVariantModel pvModel) {
		IPVVariantElement selection = pvModel.getSelectionOfReference(element);
		if (null == selection) {
			return "ps:user";
		} else {
			return selection.getSelector();
		}
	}

	public VariantModel	getVariantModel(String key) {
		return variantModels.get(key);
	}
	
	public Collection<VariantModel>	getVariantModels() {
		return variantModels.values();
	}
	
	private IPVVariantModel tryOpenVariantModel(IFile member) {
		try {
			return purevariants.openVariantModel(member.getLocationURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Attribute exportDefiningModelAttribute(IPVElement variantReference) {
		
		if(isInstanceReference(variantReference)) {
			return createDefiningAttribute(extractDefiningModelFromInstanceReference(variantReference));	
		}else {
			return createDefiningAttribute(extractDefiningModelFromVariantReference(variantReference));
		}
	}

	private boolean isInstanceReference(IPVElement variantReference) {
		return "ps:variant".equals(variantReference.getType()) && getVDMPath(variantReference).isBlank();
	}

	private Attribute createDefiningAttribute(String value) {
		Attribute attribute = new Attribute();
		attribute.setDefaultValue(value);
		attribute.setName("definingModel");
		attribute.setRange("");
		attribute.setType("ps:string");
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
	
	private String extractDefiningModelFromVariantReference(IPVElement variantReference) {
		IPVVariantModel vdm = purevariants.openVariantModel(tryFindMemberUrl(createVariantPathRelativeToExportedProject(variantReference)));
		IPVFeatureModel featureModel = (IPVFeatureModel)purevariants.getFeatureModels().get(0);
		String definingModelName = featureModel.getName();
		purevariants.closeModel(vdm);
		return definingModelName;
	}
	
	private Path getConfigSpacePath(IPVElement variantReference) {
		String pathToConfigSpace = purevariants.getPropertyValue(variantReference.getPropertyWithName(ModelConstants.ATTRIBUTES_CS_PATH_NAME), variantReference);
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

	private Path createVariantPathRelativeToExportedProject(IPVElement variantReference) {
		// Only sub-models have a VDM path. For instances, the path has to be extracted from the ConfigSpace reference.
		String pathAttribute = null;
		try{
			pathAttribute  = getVDMPath(variantReference);
		}catch(Exception e) {
			pathAttribute = EMPTY_STRING;
		}
		return  new Path(pathAttribute.substring(pathAttribute.indexOf(")")+1));
	}

	private String getVDMPath(IPVElement variantReference) {
		return purevariants.getPropertyValue(variantReference.getPropertyWithName(ModelConstants.ATTRIBUTES_VDM_PATH_NAME), variantReference);
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
		ID parentId = pvFeature.getParent() != null ? pvFeature.getParent().getID(): new ID("");
		return parentId.toString();
	}

}

