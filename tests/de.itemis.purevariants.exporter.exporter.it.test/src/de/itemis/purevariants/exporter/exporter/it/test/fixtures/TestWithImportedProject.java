// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.exporter.it.test.fixtures;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class TestWithImportedProject {

	protected IProject project;
	protected Path sourceProjectPath;
	
	@BeforeEach
	public void importProject() throws Exception {
		project = ProjectBuilder.givenImportedProject(sourceProjectPath());
	}
	
	@AfterEach
	public void removeProject() throws Exception {
		project.delete(false, false, new NullProgressMonitor());
		
		Path projectLocation = projectPath().toFile().toPath();
		Files.walk(projectLocation)
	      .sorted(Comparator.reverseOrder())
	      .map(java.nio.file.Path::toFile)
	      .forEach(File::delete);
	}

	protected abstract Path sourceProjectPath();

	protected IPath givenOutputDirectory() {
		return projectPath().append("exporter_output");
	}
	
	protected IProject getProject(String projectName) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}
	
	protected IPath projectPath() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().append(project.getFullPath());
	}
	
	protected Path expectedFileUnderOutputPath(String relativeFilePath) {
		return givenOutputDirectory().append(relativeFilePath).toFile().toPath();
	}
}
