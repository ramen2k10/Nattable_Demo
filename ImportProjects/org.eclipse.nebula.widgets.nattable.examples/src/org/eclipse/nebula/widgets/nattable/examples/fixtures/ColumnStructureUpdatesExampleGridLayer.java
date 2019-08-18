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
package org.eclipse.nebula.widgets.nattable.examples.fixtures;

import java.util.Collection;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.event.ColumnStructuralChangeEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.StructuralDiff;
import org.eclipse.nebula.widgets.nattable.layer.stack.DefaultBodyLayerStack;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.util.IClientAreaProvider;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

/**
 * Factory for assembling GridLayer and the child layers - with support for
 * GlazedLists
 *
 * @see <a href="http://publicobject.com/glazedlists/"> http://publicobject.com/
 *      glazedlists/</a>
 */
public class ColumnStructureUpdatesExampleGridLayer<T> extends GridLayer {

    private ColumnOverrideLabelAccumulator columnLabelAccumulator;
    private DataLayer bodyDataLayer;
    private DataLayer columnHeaderDataLayer;

    public ColumnStructureUpdatesExampleGridLayer(EventList<T> eventList,
            String[] propertyNames, Map<String, String> propertyToLabelMap,
            IConfigRegistry configRegistry) {
        this(eventList, propertyNames, propertyToLabelMap, configRegistry, true);
    }

    public ColumnStructureUpdatesExampleGridLayer(EventList<T> eventList,
            String[] propertyNames, Map<String, String> propertyToLabelMap,
            IConfigRegistry configRegistry, boolean useDefaultConfiguration) {
        super(useDefaultConfiguration);

        // Body - with list event listener
        // NOTE: Remember to use the SortedList constructor with 'null' for the
        // Comparator
        SortedList<T> sortedList = new SortedList<>(eventList, null);
        IColumnPropertyAccessor<T> columnPropertyAccessor = new ReflectiveColumnPropertyAccessor<>(
                propertyNames);
        this.bodyDataProvider = new ListDataProviderExample<>(sortedList,
                columnPropertyAccessor);

        this.bodyDataLayer = new DataLayer(this.bodyDataProvider);
        GlazedListsEventLayer<T> glazedListsEventLayer = new GlazedListsEventLayer<>(
                this.bodyDataLayer, eventList);
        DefaultBodyLayerStack bodyLayer = new DefaultBodyLayerStack(
                glazedListsEventLayer);

        // Sort Column header
        IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(
                propertyNames, propertyToLabelMap);
        this.columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(
                columnHeaderDataProvider);
        ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(
                this.columnHeaderDataLayer, bodyLayer, bodyLayer.getSelectionLayer());

        // Auto configure off. Configurations have to applied manually.
        SortHeaderLayer<T> columnHeaderSortableLayer = new SortHeaderLayer<>(
                columnHeaderLayer, new GlazedListsSortModel<>(sortedList,
                        columnPropertyAccessor, configRegistry,
                        this.columnHeaderDataLayer),
                false);

        // Row header
        DefaultRowHeaderDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(
                this.bodyDataProvider);
        DefaultRowHeaderDataLayer rowHeaderDataLayer = new DefaultRowHeaderDataLayer(
                rowHeaderDataProvider);
        RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer,
                bodyLayer, bodyLayer.getSelectionLayer());

        // Corner
        DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(
                columnHeaderDataProvider, rowHeaderDataProvider);
        DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
        CornerLayer cornerLayer = new CornerLayer(cornerDataLayer,
                rowHeaderLayer, columnHeaderLayer);

        // Grid
        setBodyLayer(bodyLayer);
        setColumnHeaderLayer(columnHeaderSortableLayer);
        setRowHeaderLayer(rowHeaderLayer);
        setCornerLayer(cornerLayer);
    }

    public ColumnOverrideLabelAccumulator getColumnLabelAccumulator() {
        return this.columnLabelAccumulator;
    }

    @Override
    public void setClientAreaProvider(IClientAreaProvider clientAreaProvider) {
        super.setClientAreaProvider(clientAreaProvider);
    }

    public DataLayer getBodyDataLayer() {
        return this.bodyDataLayer;
    }

    public AbstractLayer getColumnHeaderDataLayer() {
        return this.columnHeaderDataLayer;
    }

    public ListDataProviderExample<T> bodyDataProvider;

    public class ListDataProviderExample<E> extends ListDataProvider<E> {
        private int mColumnCount = 0;

        public ListDataProviderExample(SortedList<E> sortedList,
                IColumnPropertyAccessor<E> columnPropertyAccessor) {
            super(sortedList, columnPropertyAccessor);
        }

        @Override
        public int getColumnCount() {
            return this.mColumnCount == 0 ? 2 : this.mColumnCount;
        }

        public void setColumnCount(int pColumnCount) {
            this.mColumnCount = pColumnCount;
            fireColumnCountChangeEvent(ColumnStructureUpdatesExampleGridLayer.this.bodyDataLayer);
        }

        private void fireColumnCountChangeEvent(ILayer layer) {
            if (layer instanceof AbstractLayer) {
                AbstractLayer alay = (AbstractLayer) layer;
                alay.fireLayerEvent(new MultiColumnStructuralChangeEventExtension(
                        layer));
            }
        }

        private final class MultiColumnStructuralChangeEventExtension extends
                ColumnStructuralChangeEvent {
            private MultiColumnStructuralChangeEventExtension(ILayer layer) {
                super(layer);
            }

            @Override
            public Collection<StructuralDiff> getColumnDiffs() {
                return null;
            }

            @Override
            public boolean isHorizontalStructureChanged() {
                return true;
            }

            @Override
            public boolean convertToLocal(ILayer localLayer) {
                return true;
            }

            @Override
            public ILayerEvent cloneEvent() {
                return new MultiColumnStructuralChangeEventExtension(getLayer());
            }
        }
    }
}
