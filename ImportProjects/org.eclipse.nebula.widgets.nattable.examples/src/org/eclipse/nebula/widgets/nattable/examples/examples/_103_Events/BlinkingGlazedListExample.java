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
package org.eclipse.nebula.widgets.nattable.examples.examples._103_Events;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.blink.BlinkConfigAttributes;
import org.eclipse.nebula.widgets.nattable.blink.BlinkLayer;
import org.eclipse.nebula.widgets.nattable.blink.BlinkingCellResolver;
import org.eclipse.nebula.widgets.nattable.blink.IBlinkingCellResolver;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowIdAccessor;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.examples.AbstractNatExample;
import org.eclipse.nebula.widgets.nattable.examples.runner.StandaloneNatExampleRunner;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultGridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;

/*
 * This is a work in progress while seeking best solution and is therefore
 * unlikely to be a good reference example yet...
 */
public class BlinkingGlazedListExample extends AbstractNatExample {

    public static void main(String[] args) {
        StandaloneNatExampleRunner.run(800, 400,
                new BlinkingGlazedListExample());
    }

    private static final String BLINK_UP_CONFIG_LABEL = "blinkUpConfigLabel";
    private static final String BLINK_DOWN_CONFIG_LABEL = "blinkDownConfigLabel";

    private final Timer timer = new Timer();

    private final EventList<Tuple> baseTupleList;
    private final List<String> headers = new ArrayList<>();
    private final BasicEventList<Tuple> eventList;

    public BlinkingGlazedListExample() {
        this.headers.add("Name");
        this.headers.add("Value");
        this.headers.add("Price");
        this.headers.add("Quantity");

        this.eventList = new BasicEventList<>();
        this.baseTupleList = GlazedLists.threadSafeList(this.eventList);
    }

    @Override
    public String getDescription() {
        return "Cell blinking";
    }

    @Override
    public Control createExampleControl(Composite parent) {
        // Data backing the table
        this.baseTupleList.add(new Tuple(this.headers, new Object[] { "Name 1",
                "Value 1", 1.5d, -1000 }, 0));
        this.baseTupleList.add(new Tuple(this.headers, new Object[] { "Name 2",
                "Value 2", -2.5d, 2000 }, 1));
        this.baseTupleList.add(new Tuple(this.headers, new Object[] { "Name 3",
                "Value 3", 3.5d, -3000 }, 2));
        this.baseTupleList.add(new Tuple(this.headers, new Object[] { "Name 4",
                "Value 4", -4.5d, 4000 }, 3));
        this.baseTupleList.add(new Tuple(this.headers, new Object[] { "Name 5",
                "Value 5", 5.5d, -5000 }, 4));

        ConfigRegistry configRegistry = new ConfigRegistry();

        ObservableElementList<Tuple> observableTupleList = new ObservableElementList<>(
                this.baseTupleList, GlazedLists.beanConnector(Tuple.class));
        TupleColumnPropertyAccessor columnPropertyAccessor = new TupleColumnPropertyAccessor();
        ListDataProvider<Tuple> bodyDataProvider = new ListDataProvider<>(
                observableTupleList, columnPropertyAccessor);
        DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);

        // Enable capturing glazed list update events
        GlazedListsEventLayer<Tuple> glazedListEventsLayer = new GlazedListsEventLayer<>(
                bodyDataLayer, this.baseTupleList);

        // Enable blinking
        final BlinkLayer<Tuple> blinkingLayer = new BlinkLayer<>(
                glazedListEventsLayer, bodyDataProvider, getRowIdAccessor(),
                columnPropertyAccessor, configRegistry);

        registerBlinkingConfigCells(configRegistry);

        // Add Listener to existing elements
        try {
            this.baseTupleList.getReadWriteLock().readLock().lock();
            for (Tuple tuple : this.baseTupleList) {
                tuple.addPropertyChangeListener(glazedListEventsLayer);
            }
        } finally {
            this.baseTupleList.getReadWriteLock().readLock().unlock();
        }

        // Setup row/column and corner layers
        ColumnHeaderDataProvider defaultColumnHeaderDataProvider = new ColumnHeaderDataProvider();
        DefaultRowHeaderDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(
                bodyDataProvider);
        DefaultCornerDataProvider defaultCornerDataProvider = new DefaultCornerDataProvider(
                defaultColumnHeaderDataProvider, rowHeaderDataProvider);

        // Build composite for all regions
        DefaultGridLayer gridLayer = new DefaultGridLayer(blinkingLayer,
                new DefaultColumnHeaderDataLayer(
                        defaultColumnHeaderDataProvider),
                new DefaultRowHeaderDataLayer(rowHeaderDataProvider),
                new DataLayer(defaultCornerDataProvider));

        NatTable natTable = new NatTable(parent, gridLayer, false);

        natTable.setConfigRegistry(configRegistry);
        natTable.addConfiguration(new DefaultNatTableStyleConfiguration());

        natTable.configure();

        return natTable;
    }

    private IRowIdAccessor<Tuple> getRowIdAccessor() {
        return new IRowIdAccessor<Tuple>() {
            @Override
            public Serializable getRowId(Tuple rowObject) {
                return rowObject.id;
            }
        };
    }

    @Override
    public void onStart() {
        this.timer.schedule(new UpdateTupleTask(), 500L, 2000L);
    }

    @Override
    public void onStop() {
        this.timer.cancel();
    }

    public class Tuple implements Comparable<Tuple> {

        private final Object[] objects;
        private final List<String> propertyNames;
        private final long id;

        private final PropertyChangeSupport support;

        public Tuple(List<String> propertyNames, Object[] objects, long id) {
            this.propertyNames = propertyNames;
            this.objects = objects;
            this.id = id;
            this.support = new PropertyChangeSupport(this);
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            this.support.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            this.support.removePropertyChangeListener(listener);
        }

        public Object getValue(int index) {
            return this.objects[index];
        }

        public void update(int index, Object newValue) {
            Object oldValue = this.objects[index];
            this.objects[index] = newValue;
            System.out.println("Update: " + newValue);
            this.support.firePropertyChange(this.propertyNames.get(index), oldValue,
                    newValue);
        }

        @Override
        public int compareTo(Tuple o) {
            return 0;
        }
    }

    private void registerBlinkingConfigCells(ConfigRegistry configRegistry) {
        configRegistry.registerConfigAttribute(
                BlinkConfigAttributes.BLINK_RESOLVER, getBlinkResolver(),
                DisplayMode.NORMAL);

        // Styles
        Style cellStyle = new Style();
        cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
                Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE,
                cellStyle, DisplayMode.NORMAL, BLINK_UP_CONFIG_LABEL);

        cellStyle = new Style();
        cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR,
                Display.getDefault().getSystemColor(SWT.COLOR_RED));
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE,
                cellStyle, DisplayMode.NORMAL, BLINK_DOWN_CONFIG_LABEL);
    }

    private IBlinkingCellResolver getBlinkResolver() {
        return new BlinkingCellResolver() {
            private final String[] configLabels = new String[1];

            @Override
            public String[] resolve(Object oldValue, Object newValue) {
                int old = ((Integer) oldValue).intValue();
                int latest = ((Integer) newValue).intValue();
                this.configLabels[0] = (latest > old ? BLINK_UP_CONFIG_LABEL
                        : BLINK_DOWN_CONFIG_LABEL);
                return this.configLabels;
            };
        };
    }

    public class TupleColumnPropertyAccessor implements
            IColumnPropertyAccessor<Tuple> {

        @Override
        public int getColumnCount() {
            return BlinkingGlazedListExample.this.headers.size();
        }

        @Override
        public Object getDataValue(Tuple tuple, int colIndex) {
            return tuple.getValue(colIndex);
        }

        @Override
        public void setDataValue(Tuple arg0, int arg1, Object arg2) {
            // not supported
        }

        @Override
        public int getColumnIndex(String propertyName) {
            return BlinkingGlazedListExample.this.headers.indexOf(propertyName);
        }

        @Override
        public String getColumnProperty(int columnIndex) {
            return BlinkingGlazedListExample.this.headers.get(columnIndex);
        }

    }

    public class ColumnHeaderDataProvider implements IDataProvider {

        @Override
        public int getColumnCount() {
            return BlinkingGlazedListExample.this.headers.size();
        }

        @Override
        public Object getDataValue(int columnIndex, int rowIndex) {
            return BlinkingGlazedListExample.this.headers.get(columnIndex);
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
            // none
        }
    }

    public class UpdateTupleTask extends TimerTask {
        @Override
        public void run() {
            Tuple toUpdate = BlinkingGlazedListExample.this.baseTupleList.get(2);
            Integer existingValue = (Integer) toUpdate.getValue(3);
            toUpdate.update(3, existingValue * -1);
        }
    }
}
