/*******************************************************************************
 * Copyright (c) 2012, 2018 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.examples;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.util.ObjectUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractNatExample implements INatExample {

    private Text outputArea;

    @Override
    public String getName() {
        return getClass().getSimpleName().replaceAll("^_[0-9]*_", "")
                .replace('_', ' ');
    }

    @Override
    public String getShortDescription() {
        String description = getDescription();
        return description.substring(0, description.indexOf('.') + 1);
    }

    @Override
    public String getDescription() {
        String description = getResourceAsString(getClass().getSimpleName()
                + ".txt");
        if (description != null) {
            return description;
        } else {
            return "";
        }
    }

    @Override
    public void onStart() {}

    @Override
    public void onStop() {}

    private String getResourceAsString(String resource) {
        try (InputStream inStream = getClass().getResourceAsStream(resource)) {
            if (inStream != null) {
                StringBuilder strBuf = new StringBuilder();
                int i = -1;
                while ((i = inStream.read()) != -1) {
                    strBuf.append((char) i);
                }

                return strBuf.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Text area at the bottom
     */
    public Text setupTextArea(Composite parent) {
        this.outputArea = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        this.outputArea.setEditable(false);

        GridDataFactory.fillDefaults().grab(true, false).hint(0, 100).align(SWT.FILL, SWT.BEGINNING).applyTo(this.outputArea);
        return this.outputArea;
    }

    public void log(String msg) {
        if (ObjectUtils.isNotNull(this.outputArea)) {
            this.outputArea.append(msg + "\n");
            System.out.println(msg);
        }
    }
}
