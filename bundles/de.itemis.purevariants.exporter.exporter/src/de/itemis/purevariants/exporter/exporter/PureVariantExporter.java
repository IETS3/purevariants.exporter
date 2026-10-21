// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import com.sun.xml.bind.v2.ContextFactory;

import de.itemis.purevariants.exporter.model.FeatureModelSet;
import de.itemis.purevariants.exporter.model.VariantModelSet;

public class PureVariantExporter {
	private final String EXPORTER_VERSION = "3.0.1";
	
	private PureVariantsFacade facade = new PureVariantsFacade();

	public void export(IPath outputPath, IProject project) {
		FeatureModelSet fmModels = exportFeatureModels(project);
		tryMarshall(fmModels, outputPath.append(project.getName()+"_FeatureModels_iets3.xml"));	
		
		VariantModelSet vmModels = exportVariantModels(project);
		tryMarshall(vmModels, outputPath.append(project.getName()+"_VariantModels_iets3.xml"));	
	}
	
	public FeatureModelSet exportFeatureModels(IProject project) {
		FeatureModelExporter exporter = new FeatureModelExporter(project, facade);
		FeatureModelSet models = new FeatureModelSet();
		models.setFeatureModels(exporter.export().values());
		models.setVersion(EXPORTER_VERSION);
		return models;
	}
	
	public VariantModelSet exportVariantModels(IProject project) {
		VariantModelExporter exporter = new VariantModelExporter(project, facade);
		VariantModelSet models = new VariantModelSet();
		models.setVariantModels(exporter.export().values());
		models.setVersion(EXPORTER_VERSION);
		return models;		
	}

	private void tryMarshall(Object element, IPath outputPath) {
		try {
			marshall(element, outputPath);
		} catch (FileNotFoundException | JAXBException e) {
			e.printStackTrace();
		}
	}

	private void marshall(Object element, IPath outputpath) throws FileNotFoundException, JAXBException {
		JAXBContext contextObj = ContextFactory.createContext(new Class[] { element.getClass() }, Collections.<String, Object>emptyMap());
		//JAXBContext contextObj = JAXBContext.newInstance(element.getClass());

		Marshaller marshallerObj = contextObj.createMarshaller();
		marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		File file = outputpath.toFile();
		file.getParentFile().mkdirs();
		marshallerObj.marshal(element, file);
	}
}

