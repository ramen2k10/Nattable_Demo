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
package org.eclipse.nebula.widgets.nattable.dataset.valuegenerator;

import java.util.Random;

import org.eclipse.nebula.widgets.nattable.dataset.generator.IValueGenerator;

public class IntegerValueGenerator implements IValueGenerator {

    private int floor;

    private int range;

    /**
     * Generates random integer values such that: (floor) &lt;= value &lt;
     * (floor + range)
     *
     * @param floor
     *            Minimum value to be returned by this value generator. May be
     *            positive or negative.
     * @param range
     *            Indicates size of the range of values to be returned by this
     *            generator. Must be &gt; 0.
     */
    public IntegerValueGenerator(int floor, int range) {
        if (range <= 0) {
            throw new IllegalArgumentException("Range must be > 0");
        }

        this.floor = floor;
        this.range = range;
    }

    @Override
    public Object newValue(Random random) {
        return new Integer(this.floor + random.nextInt(this.range));
    }

}
