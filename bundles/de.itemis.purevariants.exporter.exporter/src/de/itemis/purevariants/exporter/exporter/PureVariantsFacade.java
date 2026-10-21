// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.exporter;

import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import com.ps.consul.eclipse.core.ConsulCorePlugin;
import com.ps.consul.eclipse.core.IModelManager;
import com.ps.consul.eclipse.core.IVDMManager;
import com.ps.consul.eclipse.core.IVariantModelEnvironment;
import com.ps.consul.eclipse.core.evaluate.EvaluateSettings;
import com.ps.consul.eclipse.core.evaluate.IVariantResult;
import com.ps.consul.eclipse.core.evaluate.VariantResult;
import com.ps.consul.eclipse.core.model.IPVElement;
import com.ps.consul.eclipse.core.model.IPVFeatureModel;
import com.ps.consul.eclipse.core.model.IPVModel;
import com.ps.consul.eclipse.core.model.IPVProperty;
import com.ps.consul.eclipse.core.model.IPVVariantModel;
import com.ps.consul.eclipse.core.model.VariantElementState;

/**
 * Thin wrapper around the pure::variants core API used by the exporters.
 * Keeps track of the variant model opened last and its environment.
 * API errors are logged and reported as {@code null} (or an empty result).
 */
public class PureVariantsFacade {

	private static final String EMPTY_PATH = "file://";

	private final IModelManager models;
	private final IVDMManager vdms;

	private IPVVariantModel currentVariantModel;
	private IVariantModelEnvironment currentEnvironment;

	public PureVariantsFacade() {
		ConsulCorePlugin core = ConsulCorePlugin.getDefault();
		models = core.getModelManager();
		vdms = core.getVDMManager();
	}

	public IPVFeatureModel openFeatureModel(URL modelUrl) {
		return callOrNull(() -> (IPVFeatureModel) models.openModel(modelUrl));
	}

	public void closeModel(IPVModel model) {
		callOrNull(() -> {
			models.closeModel(model);
			return null;
		});
	}

	/**
	 * Opens the variant model and makes it the current one for
	 * {@link #getSelectionState}, {@link #evaluateVariantModel} and {@link #getFeatureModels}.
	 */
	public IPVVariantModel openVariantModel(URL modelUrl) {
		return callOrNull(() -> {
			IPVVariantModel opened = (IPVVariantModel) models.openModel(modelUrl);
			currentVariantModel = opened;
			currentEnvironment = callOrNull(() -> vdms.getVariantEnvironment(opened));
			return opened;
		});
	}

	public IPVFeatureModel getFeatureModelFor(IPVVariantModel model) {
		return callOrNull(() -> (IPVFeatureModel) vdms.getVariantEnvironment(model).getFeatureModels().get(0));
	}

	/**
	 * Returns the first constant value of the property, or an empty string if the
	 * value is an empty path or cannot be read.
	 */
	public String getPropertyValue(IPVProperty property, IPVElement element) {
		try {
			String value = property.getConstantList().iterator().next().getValue();
			// a null value deliberately ends up in the catch block below
			return value.equals(EMPTY_PATH) ? "" : value;
		} catch (Exception e) {
			System.out.println("Container: " + property.getContainer());
			System.out.println("Name: " + element.getName());
			System.out.println("Property:" + property.getName());
			System.out.println("type:" + property.getType());
			return "";
		}
	}

	/**
	 * Maps the selection state of the element in the current variant model to
	 * the pure::variants state name.
	 */
	public String getSelectionState(IPVElement element) {
		switch (currentVariantModel.getSelection(element)) {
		case VariantElementState.SELECTION:
			return "ps:selected";
		case VariantElementState.EXCLUSION:
			return "ps:excluded";
		case VariantElementState.NONSELECTION:
			return "ps:undefined";
		default: // UNSELECTION, NOT_AVAILABLE
			return "ps:unselected";
		}
	}

	public IVariantResult evaluateVariantModel() {
		try {
			return ConsulCorePlugin.getDefault().getEvaluationManager().evaluate(currentVariantModel,
					vdms.getEnvironment(currentVariantModel), new EvaluateSettings(), null);
		} catch (CoreException e) {
			e.printStackTrace();
			return new VariantResult();
		}
	}

	public List<IPVModel> getFeatureModels() {
		return currentEnvironment.getFeatureModels();
	}

	@FunctionalInterface
	private interface CoreCall<T> {
		T call() throws CoreException;
	}

	private static <T> T callOrNull(CoreCall<T> call) {
		try {
			return call.call();
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
	}
}
