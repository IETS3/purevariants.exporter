// Copyright (c) 2022-2026 itemis AG and others. SPDX-License-Identifier: EPL-2.0
package de.itemis.purevariants.exporter.exporter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Path;
import org.junit.jupiter.api.Test;

public class ExtractProjectNameFromPathTest {
	@Test
	public void extractProject() throws Exception {
		Pattern projectNamePattern = Pattern.compile("\\$\\(PROJECT:(.*?)\\)");
		String data = "$(PROJECT:ProjectSubmodel)/SubmodelConfigSpace/Subvariant.vdm";
		Matcher matcher =  projectNamePattern.matcher(data);
		
		assertThat(matcher.find()).isTrue();
		assertThat(matcher.group(1)).isEqualTo("ProjectSubmodel");
	}
	
	@Test
	public void buildVdmpath() throws Exception {
		String pathAttribute = "$(PROJECT:ProjectSubmodel)/SubmodelConfigSpace/Subvariant.vdm";
		String projectName = pathAttribute.substring(pathAttribute.indexOf(":")+1, pathAttribute.indexOf(")"));
		Path vdmPath = new Path("/" + projectName + pathAttribute.substring(pathAttribute.indexOf(")")+1));

		
		assertThat(vdmPath).hasToString("/ProjectSubmodel/SubmodelConfigSpace/Subvariant.vdm");
	}
}
