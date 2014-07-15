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

import static org.jboss.tools.vpe.preview.core.server.HttpConstants.HTTP;
import static org.jboss.tools.vpe.preview.core.server.HttpConstants.LOCALHOST;
import static org.jboss.tools.vpe.preview.core.server.HttpConstants.VIEW_ID;
import static org.jboss.tools.vpe.preview.core.server.HttpConstants.WEBROOT_PATH;
import static org.jboss.tools.vpe.preview.core.server.VpvSocketProcessor.UTF_8;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.jboss.tools.common.web.WebUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
@SuppressWarnings("restriction")
public final class EditorUtil {
	
	private EditorUtil(){
	}

	public static boolean isInCurrentEditor(IStructuredSelection selection, IEditorPart editor) {
		Node selectedNode = getNodeFromSelection(selection);
		Document selectionDocument = null;
		if (selectedNode != null) {
			selectionDocument = selectedNode.getOwnerDocument();
		}
		return selectionDocument != null && selectionDocument == getEditorDomDocument(editor);
	}

	public static Node getNodeFromSelection(IStructuredSelection selection) {
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof Node) {
			return (Node) firstElement;
		} else {
			return null;
		}
	}

	public static boolean isImportant(IEditorPart editor) {
		// TODO check DOM model support
		return editor.getAdapter(StructuredTextEditor.class) != null 
				|| SuitableFileExtensions.isCssOrJs(getFileExtensionFromEditor(editor)); 
	}

	public static IFile getFileOpenedInEditor(IEditorPart editorPart) {
		IFile file = null;
		IEditorInput fei = editorPart.getEditorInput(); 
		if (fei instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput = (IFileEditorInput) editorPart.getEditorInput();
			file = fileEditorInput.getFile();
		}
		return file;
	}

	public static String getFileExtensionFromEditor(IEditorPart editor) {
		String fileExtension = null;
		IFile file = EditorUtil.getFileOpenedInEditor(editor);
		if (file != null && file.exists()) {
			fileExtension = file.getFileExtension();
		}
		return fileExtension;
	}

	public static String formUrl(IFile file, int modelHolderId, String serverPort) throws UnsupportedEncodingException {
		String projectName = file.getProject().getName();
		String projectRelativePath = file.getProjectRelativePath().toString();
		IContainer webroot = WebUtils.getWebRootFolder(file);
		String webrootRelativePathString = projectRelativePath;
		String webrootString = null;
		if(webroot!=null) {
			IPath webrootRelativePath = file.getFullPath().makeRelativeTo(webroot.getFullPath());
			webrootRelativePathString = webrootRelativePath.toString();
			webrootString = webroot.getFullPath().toString();
		} else {
			webrootString = "/" + projectName; //$NON-NLS-1$
		}
		String parameters = WEBROOT_PATH + "=" + URLEncoder.encode(webrootString, UTF_8) + "&" + VIEW_ID + "=" + modelHolderId; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return HTTP + LOCALHOST + ":" + serverPort + "/" + webrootRelativePathString + "?" + parameters; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	private static Document getEditorDomDocument(IEditorPart editor) {
		Document editorDocument = null;
		IDOMModel editorModel = (IDOMModel) editor.getAdapter(IDOMModel.class);
		IDOMDocument editorIdomDocument = editorModel.getDocument();;

		Element editorDocumentElement = null;
		if (editorIdomDocument != null) {
			editorDocumentElement = editorIdomDocument.getDocumentElement();

			if (editorDocumentElement != null) {
				editorDocument = editorDocumentElement.getOwnerDocument();
			}
		}

		return editorDocument;
	}
}
