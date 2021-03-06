/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.vpe.editor.toolbar.format;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.document.TextImpl;
import org.w3c.dom.Node;
import org.jboss.tools.jst.web.ui.internal.editor.selection.SourceSelectionBuilder;
import org.jboss.tools.jst.web.ui.internal.editor.selection.SourceSelection;
import org.jboss.tools.jst.web.ui.internal.editor.selection.SelectedNodeInfo;
import org.jboss.tools.vpe.VpePlugin;
import org.jboss.tools.vpe.editor.VisualController;
import org.jboss.tools.vpe.editor.template.textformating.FormatData;
import org.jboss.tools.vpe.editor.template.textformating.TextFormatingData;
import org.jboss.tools.vpe.editor.toolbar.format.handler.FormatHandler;
import org.jboss.tools.vpe.editor.toolbar.format.handler.HandlerFactory;
import org.jboss.tools.vpe.editor.toolbar.format.handler.IFormatHandler;

/**
 * This manager set format toolbar items to enabled/disabled status after selection changing.
 * @author Igels
 */
@SuppressWarnings("restriction")
public class FormatControllerManager {

	private VisualController vpeController = null;
	private List<IFormatController> formatControllers = new ArrayList<IFormatController>();
	private HandlerFactory handlerFactory;

	private FormatData currentFormatData;
	private Node currentSelectedNode;
	private Node currentSelectedElement; // Parent node If currentSelectedNode is Text.
	private SelectedNodeInfo currentSelectedNodeInfo;
	private boolean controllerNotifedSelectionChange = false;
	private String currentSelectedTagValue;
	private boolean ignoreSelectionChanges = false;

	/**
	 * Constructor
	 */
	public FormatControllerManager() {
		handlerFactory = new HandlerFactory(this);
	}

	/**
	 * @param vpeController
	 */
	public void setVpeController(VisualController vpeController) {
		this.vpeController = vpeController;
	}

	/**
	 * Notify selection changed.
	 * Set format toolbar items to enabled/disabled status.
	 */
	public void selectionChanged() {
		List<SelectedNodeInfo> cleanNodesList = getCleanSelectedNodesList();
		if(cleanNodesList.size()==1) {
			SelectedNodeInfo newNodeInfo = (SelectedNodeInfo)cleanNodesList.get(0);
			if(newNodeInfo!=null) {
				 Node newNode = newNodeInfo.getNode();
				 if(currentSelectedNode == newNode && newNode.getNodeType() == Node.ELEMENT_NODE) {
					 ElementImpl elementImpl = (ElementImpl)newNode;
					 int startOffset = elementImpl.getStartOffset();
					 int startEndOffset = elementImpl.getStartEndOffset();
					 StructuredTextViewer viewer = getVpeController().getSourceEditor().getTextViewer();//getPageContext().getSourceBuilder().getStructuredTextViewer();
					 try {
						String newSelectedTagValue = viewer.getDocument().get(startOffset, startEndOffset-startOffset);
						if(currentSelectedTagValue!=null && currentSelectedTagValue.equals(newSelectedTagValue)) {
							return;
						}
						currentSelectedTagValue = newSelectedTagValue;
					 } catch (BadLocationException e) {
						 VpePlugin.getPluginLog().logError(e);
					 }
				 } else {
					 currentSelectedTagValue = null;
				 }
			this.currentSelectedNode = newNodeInfo.getNode();
			}
			currentSelectedNodeInfo = newNodeInfo;

			currentSelectedElement = currentSelectedNode;
			TextFormatingData textFormatingData = getFormatTemplateForSelectedNode();
			if(textFormatingData!=null) {
				selectionChanged(textFormatingData);
			} else {
				disableAllFormatControllers();
			}
		} else {
			disableAllFormatControllers();
			currentSelectedTagValue = null;
			currentSelectedNodeInfo = null;
			currentSelectedNode = null;
			currentSelectedElement = currentSelectedNode; 
		}
	}

	private void selectionChanged(TextFormatingData textFormatingData) {
		if(ignoreSelectionChanges) {
			return;
		}
		for(int i=0; i<formatControllers.size(); i++) {
			IFormatController controller = (IFormatController)formatControllers.get(i);
			FormatData[] formatDatas = textFormatingData.getFormatDatas(controller.getType());
			boolean enabled = false;
			for(int j=0; j<formatDatas.length; j++) {
				FormatData formatData = formatDatas[j];
				currentFormatData = formatData;
				IFormatHandler handler = handlerFactory.createHandler(formatData);
				if(handler!=null) {
					if(handler instanceof FormatHandler) {
						FormatHandler formatHandler = (FormatHandler)handler;
						formatHandler.setFormatController(controller);
					}
					if(handler instanceof IFormatItemSelector) {
						IFormatItemSelector itemHandler = (IFormatItemSelector)handler;
						if(handler.formatIsAllowable()) {
							itemHandler.setToolbarItemEnabled(true);
							enabled = true;
							break;
						}
					} else {
						if(handler.formatIsAllowable()) {
							controller.setToolbarItemEnabled(true);
							enabled = true;
							break;
						}
					}
				} else {
					boolean allowable = formatData.getFormatAttributes().length > 0;
					if(allowable) {
						controller.setToolbarItemEnabled(true);
						enabled = true;
						break;
					}
				}
			}
			if(!enabled) {
				controller.setToolbarItemEnabled(false);
			}
		}
	}

	private TextFormatingData getParentFormatingDataForTextNode(SelectedNodeInfo selectedNodeInfo) {
		Node selectedNode = selectedNodeInfo.getNode();
		if(selectedNode instanceof TextImpl) {
//				int startOffset = selectedNodeInfo.getStartOffset()>selectedNodeInfo.getEndOffset()?selectedNodeInfo.getEndOffset():selectedNodeInfo.getStartOffset();
//				int endOffset = selectedNodeInfo.getStartOffset()>selectedNodeInfo.getEndOffset()?selectedNodeInfo.getStartOffset():selectedNodeInfo.getEndOffset();
//
//				IndexedRegion region = (IndexedRegion)selectedNode;
//				String wholeValue = vpeController.getSourceEditor().getTextViewer().getDocument().get(region.getStartOffset(), region.getEndOffset()-region.getStartOffset());
//
//				String selectedValue = wholeValue.substring(startOffset, endOffset);
//				// if selected text is not whole text node return null.
//				if((startOffset==endOffset) || (wholeValue.trim().equals(selectedValue.trim()))) {
//					Node parentNode = selectedNode.getParentNode();
//					TextFormatingData data = getFormatTemplateForTag(parentNode);
//					if(data!=null) {
//						currentSelectedNode = parentNode;
//					}
//					return data;
//				}
				currentSelectedElement = selectedNode.getParentNode();
				return getFormatTemplateForTag(selectedNode.getParentNode());
		}

		return null;
	}

	/**
	 * @return
	 */
	public TextFormatingData getFormatTemplateForSelectedNode() {
		if(currentSelectedNodeInfo==null) {
			return null;
		}
		TextFormatingData textFormatingData = getFormatTemplateForTag(currentSelectedNodeInfo.getNode());
		if(textFormatingData!=null) {
			currentSelectedNode = currentSelectedNodeInfo.getNode();
			currentSelectedElement = currentSelectedNode;
			return textFormatingData;
		} else {
			// Try to take text formating data if selected node is text
			textFormatingData = getParentFormatingDataForTextNode(currentSelectedNodeInfo);
			if(textFormatingData!=null) {
				return textFormatingData;
			}
		}
		return null;
	}

	/**
	 * @param node
	 * @return
	 */
	public TextFormatingData getFormatTemplateForTag(Node node) {
//		VpeDomMapping domMapping = vpeController.getDomMapping();
//		if(node==null) {
//			return null;
//		}
//
//		VpeNodeMapping nodeMapping = domMapping.getNodeMapping(node);
//		if(nodeMapping instanceof VpeElementMapping) {
//			VpeElementMapping elementMapping = (VpeElementMapping)nodeMapping;
//
//			VpeTemplate template = elementMapping.getTemplate();
//			if(template!=null) {
//				return template.getTextFormattingData();
//			}
//		} else {
//			// Selected node is text.
//		}
		return null;
	}

	/**
	 * @return
	 */
	public SelectedNodeInfo computeSelectedNode() {
		List<SelectedNodeInfo> nodes = getCleanSelectedNodesList();
		if(nodes.size()==0) {
			return null;
		}
		return (SelectedNodeInfo)nodes.get(0);
	}

	private List<SelectedNodeInfo> getCleanSelectedNodesList() {
		SourceSelection selection = getSelection();
		if(selection==null) {
			return new ArrayList<SelectedNodeInfo>();
		}
		List dirtyNodesList = selection.getSelectedNodes();
		List<SelectedNodeInfo> cleanNodesList = getCleanSelectedNodesList(dirtyNodesList);
		return cleanNodesList;
	}

	private List<SelectedNodeInfo> getCleanSelectedNodesList(List dirtyNodesList) {
		ArrayList<SelectedNodeInfo> nodes = new ArrayList<SelectedNodeInfo>(dirtyNodesList.size());
		HashSet<Node> parentNodes = new HashSet<Node>();
		for (int i = 0; i < dirtyNodesList.size(); i++) {
			SelectedNodeInfo nodeInfo = (SelectedNodeInfo)dirtyNodesList.get(i);
			Node node = nodeInfo.getNode();
			if(parentNodes.contains(node.getParentNode())) {
				// Ignore child node.
				parentNodes.add(node);
				continue;
			}
			parentNodes.add(node);
			if(node instanceof TextImpl) {
				TextImpl textNode = (TextImpl)node;
				String value = textNode.getNodeValue().trim();
				if(value==null || value.length()==0) {
					continue;
				}
			}
			nodes.add(nodeInfo);
		}
		return nodes;
	}

	private void disableAllFormatControllers() {
		for(int i=0; i<formatControllers.size(); i++) {
			((IFormatController)formatControllers.get(i)).setToolbarItemEnabled(false);
		}
	}

	/**
	 * @return current selection
	 */
	public SourceSelection getSelection() {
		SourceSelectionBuilder sourceSelectionBuilder = new SourceSelectionBuilder(vpeController.getSourceEditor());
		return sourceSelectionBuilder.getSelection();
	}

	/**
	 * @param controller
	 */
	public void addFormatController(IFormatController controller) {
		formatControllers.add(controller);
	}

	/**
	 * @return
	 */
	public VisualController getVpeController() {
		return vpeController;
	}

	/**
	 * @return Returns the handlerFactory.
	 */
	public HandlerFactory getHandlerFactory() {
		return handlerFactory;
	}

	/**
	 * @return Returns selected node or parent of selected node if selected node is text.
	 */
	public Node getCurrentSelectedNode() {
		return currentSelectedElement;
	}

	/**
	 * @return Returns the currentFormatData.
	 */
	public FormatData getCurrentFormatData() {
		return currentFormatData;
	}

	/**
	 * @return Returns the currentSelectedNodeInfo.
	 */
	public SelectedNodeInfo getCurrentSelectedNodeInfo() {
		return currentSelectedNodeInfo;
	}

	/**
	 * @return Returns the controllerNotifedSelectionChange.
	 */
	public boolean didControllerNotifySelectionChange() {
		return controllerNotifedSelectionChange;
	}

	/**
	 * @param controllerNotifedSelectionChange The controllerNotifedSelectionChange to set.
	 */
	public void setControllerNotifedSelectionChange(boolean controllerNotifedSelectionChange) {
		this.controllerNotifedSelectionChange = controllerNotifedSelectionChange;
	}

	public boolean isIgnoreSelectionChanges() {
		return ignoreSelectionChanges;
	}

	public void setIgnoreSelectionChanges(boolean ignoreSelectionChanges) {
		this.ignoreSelectionChanges = ignoreSelectionChanges;
	}
}