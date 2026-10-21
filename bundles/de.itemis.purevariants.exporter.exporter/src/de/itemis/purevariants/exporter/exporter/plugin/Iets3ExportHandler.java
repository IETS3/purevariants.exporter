// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.exporter.plugin;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import de.itemis.purevariants.exporter.exporter.PureVariantExporter;

public class Iets3ExportHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		DirectoryDialog projectDialog = new DirectoryDialog(window.getShell(), SWT.OPEN);
		projectDialog.setText("Select Project");
		projectDialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
		String selectedProject = projectDialog.open();
		
		IPath projectPath = Path.fromOSString(selectedProject);
		PureVariantExporter exporter = new PureVariantExporter();
		exporter.export(projectPath.append("exporter_output"),getProject(projectPath));
		
		MessageDialog.openInformation(window.getShell(), "IETS3 Exporter", "Export completed.");
		return null;
	}

	private IProject getProject(IPath projectPath) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectPath.lastSegment());
	}
}


