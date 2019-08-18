/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.examples;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface INatExample {

    public static final String BASE_PACKAGE = "org.eclipse.nebula.widgets.nattable.examples";

    public static final String CLASSIC_BASE_PACKAGE = BASE_PACKAGE
            + ".examples";

    public static final String BASE_PATH = "/org/eclipse/nebula/widgets/nattable/examples";

    public static final String CLASSIC_BASE_PATH = BASE_PATH + "/examples";

    public static final String TUTORIAL_EXAMPLES_PREFIX = "Tutorial Examples/";

    public static final String CLASSIC_EXAMPLES_PREFIX = "Classic Examples/";

    public String getName();

    public String getShortDescription();

    public String getDescription();

    public Control createExampleControl(Composite parent);

    public void onStart();

    public void onStop();

}
