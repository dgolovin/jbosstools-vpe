/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.ui.test.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jst.web.ui.internal.editor.editor.IVisualEditor;
import org.jboss.tools.jst.web.ui.internal.editor.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeEditorPart;
import org.jboss.tools.vpe.ui.test.VpeUiTests;
import org.junit.Test;

public class CustomSashFormTest extends VpeTest {

    private final String FILE_NAME_ONE = "hello.jsp"; //$NON-NLS-1$
    private final String FILE_NAME_TWO = "inputUserName.jsp"; //$NON-NLS-1$
    
    public CustomSashFormTest() {
    }
	@Test
    public void testSashChangesInJBIDE3140() throws Throwable {
        IFile file1 = (IFile) TestUtil.getComponentPath(FILE_NAME_ONE,
        	VpeUiTests.IMPORT_PROJECT_NAME);
        IFile file2 = (IFile) TestUtil.getComponentPath(FILE_NAME_TWO,
        	VpeUiTests.IMPORT_PROJECT_NAME);
        
        /*
         * Open file in the VPE
         */
        IEditorInput input = new FileEditorInput(file1);
        JSPMultiPageEditor part = openEditor(input);
        VpeEditorPart visualEditor = (VpeEditorPart) part.getVisualEditor();
        /*
         * Maximize visual part, switch to source part, 
         * close the file, open file again.
         * Sash should be restored without any exception. 
         */
        visualEditor.maximizeVisual();
        visualEditor.setVisualMode(IVisualEditor.SOURCE_MODE);
        TestUtil.waitForJobs();
        /*
         * Close editor part
         */
        closeEditor(part);
        TestUtil.waitForJobs();
        TestUtil.delay();
        input = new FileEditorInput(file1);
        part = openEditor(input);
        visualEditor = (VpeEditorPart) part.getVisualEditor();
        TestUtil.waitForJobs();
        closeEditor(part);
        
        /*
         * Test it on another file
         */
        
        /*
         * Open file in the VPE
         */
        input = new FileEditorInput(file2);
        part = openEditor(input);
        visualEditor = (VpeEditorPart) part.getVisualEditor();
        
        /*
         * Maximize visual part, switch to source part, 
         * close the file, open file again.
         * Sash should be restored without any exception. 
         */
        visualEditor.maximizeVisual();
        visualEditor.setVisualMode(IVisualEditor.SOURCE_MODE);
        TestUtil.waitForJobs();
        /*
         * Close part without saving
         */
        closeEditor(part);
        TestUtil.waitForJobs();
        part = openEditor(input);
        
        /*
         * If there are any exceptions it would be thrown.
         */
        TestUtil.waitForJobs();
		if(getException()!=null) {
			throw new Exception(getException());
		}
    }
}
