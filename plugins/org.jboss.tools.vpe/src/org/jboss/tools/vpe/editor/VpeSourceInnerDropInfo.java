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
package org.jboss.tools.vpe.editor;

import org.w3c.dom.Node;

public class VpeSourceInnerDropInfo {
	private Node container;
	private int offset;
	private boolean canDrop;
	private int top;
	private int left;
	
	public VpeSourceInnerDropInfo(Node container, int offset, boolean canDrop) {
		this.container = container;
		this.offset = offset;
		this.canDrop = canDrop;
	}

	public Node getContainer() {
		return container;
	}

	public int getOffset() {
		return offset;
	}

	public boolean canDrop() {
		return canDrop;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public void setContainer(Node container) {
		this.container = container;
	}
	
	
}
