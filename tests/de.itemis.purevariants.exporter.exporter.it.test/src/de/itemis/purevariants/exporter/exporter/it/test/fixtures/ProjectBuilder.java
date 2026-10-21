// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.exporter.it.test.fixtures;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;

public class ProjectBuilder {
	
	public static IProject givenImportedProject(Path source) throws Exception {
		Path destination = Paths.get(getWorkspacePath(),source.getFileName().toString());
		copyDirectory(source.toString(), destination.toString());
				
		IProjectDescription description = ResourcesPlugin.getWorkspace()
				.loadProjectDescription(new FileInputStream(destination+"/.project"));
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
		project.create(description, new NullProgressMonitor());
		project.open(new NullProgressMonitor());
		
		return project;
	}
		
	private static String getWorkspacePath() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
	}

	private static void copyDirectory(String sourceLocation, String destinationLocation) throws IOException {
		Files.walk(Paths.get(sourceLocation)).forEach(source -> {
			Path destination = Paths.get(destinationLocation,
					source.toString().substring(sourceLocation.length()));
			try {
				Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
