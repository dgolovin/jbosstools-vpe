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
package org.jboss.tools.vpe.editor.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.expression.VpeExpressionBuilder;

public class VpeJspRootCreator extends VpeAbstractCreator {
	private static final String ATTR_PREFIX = "xmlns:";

	private List attrs;

	VpeJspRootCreator(Element taglibElement, VpeDependencyMap dependencyMap) {
		build(taglibElement, dependencyMap);
	}

	private void build(Element sourceElement, VpeDependencyMap dependencyMap) {
//		dependencyMap.setCreator(this, VpeExpressionBuilder.SIGNATURE_ANY_ATTR);
	}

	public VpeCreatorInfo create(VpePageContext pageContext, Node sourceNode, Document visualDocument, Element visualElement, Map visualNodeMap) {
//		setTaglib(pageContext, (Element)sourceNode);
		return null;
	}

	public void setAttribute(VpePageContext pageContext, Element sourceElement, Map visualNodeMap, String name, String value) {
//		setTaglib(pageContext, sourceElement);
	}

	public void removeAttribute(VpePageContext pageContext, Element sourceElement, Map visualNodeMap, String name) {
	    int address = getAddress(name);
	    if (address > 0) {
		    pageContext.setTaglib(address, null, null, true);
	    }
//		setTaglib(pageContext, sourceElement);
	}
	
	private void setTaglib(VpePageContext pageContext, Element sourceElement) {
	    attrs = new ArrayList();
	    NamedNodeMap attributes = sourceElement.getAttributes();
	    for (int i = 0; i < attributes.getLength(); i++) {
	        Attr attr = (Attr)attributes.item(i);
			String name = attr.getNodeName();
			if (name.indexOf(ATTR_PREFIX) == 0) {
			    String prefix = name.substring(6);
			    String uri = attr.getNodeValue();
				pageContext.setTaglib(attr.hashCode(), uri, prefix, true);
				attrs.add(new AttrElement(attr.hashCode(), name));
			}
	    }
	}

	private int getAddress(String name) {
	    for (int i = 0; i < attrs.size(); i++) {
	        AttrElement item = (AttrElement)attrs.get(i);
	        if (item.attrName.equals(name)) {
	            return item.address;
	        }
	    }
	    return -1;
	}
	
	
	private class AttrElement {
		private int address;
		private String attrName;

		private AttrElement(int address, String attrName) {
			this.address = address;
			this.attrName = attrName;
		}
	}
}
