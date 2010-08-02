/*******************************************************************************
  * Copyright (c) 2007-2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.vpe.spring.test;

import org.jboss.tools.vpe.ui.test.ComponentContentTest;

/**
 * Tests for the context that was generated by Spring templates
 * 
 * @author dmaliarevich
 */
public class SpringComponentContentTest extends ComponentContentTest {

	/**
	 * The Constructor
	 * 
	 * @param name a test case name
	 */
	public SpringComponentContentTest(String name) {
		super(name);
		setCheckWarning(false);
	}

	public void testCheckbox() throws Throwable {
		performContentTestByFullPath("src/main/webapp/WEB-INF/jsp/checkbox.jsp"); //$NON-NLS-1$
	}
	
	@Override
	protected String getTestProjectName() {
		return SpringAllTests.IMPORT_PROJECT_NAME;
	}

}