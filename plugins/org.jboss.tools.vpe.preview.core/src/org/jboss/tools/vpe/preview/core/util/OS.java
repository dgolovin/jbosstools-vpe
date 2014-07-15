/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.preview.core.util;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public enum OS {
	WINDOWS("win"), 
	MACOS("mac"), 
	DARWIN("darwin"), 
	LINUX("nux"), 
	OTHER("unknown");
	
	private String name; 
	
	OS(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	private static final OS DETECTED_OS;
	static {
		String currentOs = System.getProperty("os.name", "generic").toLowerCase(); //$NON-NLS-1$ //$NON-NLS-2$
		if (currentOs.indexOf(MACOS.toString()) >= 0) {
			DETECTED_OS = MACOS;
		} else if(currentOs.indexOf(DARWIN.toString()) >= 0) {
			DETECTED_OS = DARWIN;
		} else if (currentOs.indexOf(WINDOWS.toString()) >= 0) {
			DETECTED_OS = WINDOWS;
		} else if (currentOs.indexOf(LINUX.toString()) >= 0) {
			DETECTED_OS = LINUX;
		} else {
			DETECTED_OS = OTHER;
		}
	}

	public static OS getOs() {
		return DETECTED_OS;
	}
}
