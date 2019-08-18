/*******************************************************************************
 * Copyright (c) 2019 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dirk Fauth - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.nattable.examples._800_Integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultIntegerDisplayConverter;
import org.eclipse.nebula.widgets.nattable.dataset.NumberValues;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.examples.AbstractNatExample;
import org.eclipse.nebula.widgets.nattable.examples.runner.StandaloneNatExampleRunner;
import org.eclipse.nebula.widgets.nattable.freeze.CompositeFreezeLayer;
import org.eclipse.nebula.widgets.nattable.freeze.FreezeLayer;
import org.eclipse.nebula.widgets.nattable.freeze.IFreezeConfigAttributes;
import org.eclipse.nebula.widgets.nattable.freeze.config.DefaultFreezeGridBindings;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.FixedSummaryRowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.IOverlayPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.CompositeFreezeLayerPainter;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.summaryrow.DefaultSummaryRowConfiguration;
import org.eclipse.nebula.widgets.nattable.summaryrow.FixedSummaryRowLayer;
import org.eclipse.nebula.widgets.nattable.summaryrow.ISummaryProvider;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummaryRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummaryRowLayer;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummationSummaryProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.DebugMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Example that demonstrates the summary row at fixed locations in a layer
 * composition with additionally having the tables editable and freeze support.
 */
public class _816_EditableFixedSummaryRowWithFreezeExample extends AbstractNatExample {

    static final String SUMMARY_REGION = "SUMMARY";

    public static void main(String[] args) throws Exception {
        StandaloneNatExampleRunner.run(600, 400, new _816_EditableFixedSummaryRowWithFreezeExample());
    }

    @Override
    public String getDescription() {
        return "This example demonstrates how to add a fixed summary row at the top or the bottom of a grid.\n"
                + "Additionally it adds freeze and editing support.";
    }

    @Override
    public Control createExampleControl(Composite parent) {
        Composite panel = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 20;
        panel.setLayout(layout);

        // property names of the NumberValues class
        String[] propertyNames = { "columnOneNumber", "columnTwoNumber",
                "columnThreeNumber", "columnFourNumber", "columnFiveNumber" };

        // mapping from property to label, needed for column header labels
        Map<String, String> propertyToLabelMap = new HashMap<>();
        propertyToLabelMap.put("columnOneNumber", "Column 1");
        propertyToLabelMap.put("columnTwoNumber", "Column 2");
        propertyToLabelMap.put("columnThreeNumber", "Column 3");
        propertyToLabelMap.put("columnFourNumber", "Column 4");
        propertyToLabelMap.put("columnFiveNumber", "Column 5");

        IColumnPropertyAccessor<NumberValues> cpa =
                new ReflectiveColumnPropertyAccessor<>(propertyNames);
        IDataProvider dataProvider =
                new ListDataProvider<>(createNumberValueList(), cpa);

        ConfigRegistry configRegistry = new ConfigRegistry();
        // Summary row on top
        // The summary row is within the grid so it can be placed BETWEEN
        // column header and body
        // The body itself is a CompositeLayer with 1 column and 2 rows
        // The first row is the SummaryRowLayer configured as standalone
        // The second row is the body layer stack
        // for correct rendering of the row header you should use the
        // FixedSummaryRowHeaderLayer which is adding the correct labels and
        // shows a special configurable label for the summary row
        final SummaryRowGridLayer gridLayerWithSummary =
                new SummaryRowGridLayer(dataProvider, configRegistry,
                        propertyNames, propertyToLabelMap, true);

        // register a CompositeFreezeLayerPainter that renders also in the
        // header regions by registering it on the GridLayer itself
        CompositeFreezeLayerPainter painter = new CompositeFreezeLayerPainter(
                gridLayerWithSummary,
                ((SummaryRowBodyLayerStack) gridLayerWithSummary.getBodyLayer()).getCompositeFreezeLayer());
        // in this composition we have in the body a composite with summary row
        // on top and the composite freeze below. we therefore also need to add
        // the summary row as nested vertical layer to shift the freeze border
        // for the height of the summary row
        painter.addNestedVerticalLayer(((SummaryRowBodyLayerStack) gridLayerWithSummary.getBodyLayer()).getSummaryRowLayer());
        gridLayerWithSummary.setLayerPainter(painter);

        NatTable natTable = new NatTable(panel, gridLayerWithSummary, false);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

        natTable.setConfigRegistry(configRegistry);
        natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
        natTable.addConfiguration(new DebugMenuConfiguration(natTable));
        natTable.addConfiguration(new DefaultFreezeGridBindings());
        // add editing support
        natTable.addConfiguration(new AbstractRegistryConfiguration() {
            @Override
            public void configureRegistry(IConfigRegistry configRegistry) {
                configRegistry.registerConfigAttribute(
                        EditConfigAttributes.CELL_EDITABLE_RULE,
                        IEditableRule.ALWAYS_EDITABLE);
                configRegistry.registerConfigAttribute(
                        CellConfigAttributes.DISPLAY_CONVERTER,
                        new DefaultIntegerDisplayConverter(),
                        DisplayMode.NORMAL);

                configRegistry.registerConfigAttribute(
                        IFreezeConfigAttributes.SEPARATOR_COLOR,
                        GUIHelper.COLOR_RED);
            }
        });
        natTable.configure();

        // Summary row at bottom
        // The grid doesn't contain a summary row
        // Instead create a CompositeLayer 1 column 2 rows
        // first row = GridLayer
        // second row = FixedGridSummaryRowLayer
        final SummaryRowGridLayer gridLayer = new SummaryRowGridLayer(
                dataProvider, configRegistry, propertyNames, propertyToLabelMap, false);

        // since the summary row should stay at a fixed position we need to add
        // a new composite row
        // this is necessary as also the row header cell needs to be fixed

        // create a standalone summary row
        // for a grid this is the FixedGridSummaryRowLayer
        FixedSummaryRowLayer summaryRowLayer =
                new FixedSummaryRowLayer(gridLayer.getBodyDataLayer(), gridLayer, configRegistry, false);
        summaryRowLayer.addConfiguration(
                new ExampleSummaryRowGridConfiguration(gridLayer.getBodyDataLayer().getDataProvider()));
        summaryRowLayer.setSummaryRowLabel("\u2211");

        // create a composition that has the grid on top and the summary row on
        // the bottom
        CompositeLayer composite = new CompositeLayer(1, 2);
        composite.setChildLayer("GRID", gridLayer, 0, 0);
        composite.setChildLayer(SUMMARY_REGION, summaryRowLayer, 0, 1);

        // register a CompositeFreezeLayerPainter that renders also in the
        // header regions by registering it on the top CompositeLayer itself
        // we configure the inspectComposite flag as false to avoid that the top
        // and left positions in this CompositeLayer are inspected for the
        // freeze line position
        CompositeFreezeLayerPainter painter2 = new CompositeFreezeLayerPainter(
                composite,
                ((SummaryRowBodyLayerStack) gridLayer.getBodyLayer()).getCompositeFreezeLayer(),
                false);

        // now we configure the column header layer and the row header layer as
        // nested layers to shift the freeze lines to left and to the bottom so
        // they are correctly aligned.
        painter2.addNestedVerticalLayer(gridLayer.getColumnHeaderLayer());
        painter2.addNestedHorizontalLayer(gridLayer.getRowHeaderLayer());
        composite.setLayerPainter(painter2);

        natTable = new NatTable(panel, composite, false);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

        // configure a painter that renders a line on top of the summary row
        // this is necessary because the CompositeLayerPainter does not render
        // lines on the top of a region
        natTable.addOverlayPainter(new IOverlayPainter() {

            @Override
            public void paintOverlay(GC gc, ILayer layer) {
                // render a line on top of the summary row
                Color beforeColor = gc.getForeground();
                gc.setForeground(GUIHelper.COLOR_GRAY);
                int gridBorderY = gridLayer.getHeight() - 1;
                gc.drawLine(0, gridBorderY, layer.getWidth() - 1, gridBorderY);
                gc.setForeground(beforeColor);
            }
        });

        natTable.setConfigRegistry(configRegistry);
        natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
        natTable.addConfiguration(new DebugMenuConfiguration(natTable));
        natTable.addConfiguration(new DefaultFreezeGridBindings());
        // add editing support and custom freeze border
        natTable.addConfiguration(new AbstractRegistryConfiguration() {
            @Override
            public void configureRegistry(IConfigRegistry configRegistry) {
                configRegistry.registerConfigAttribute(
                        EditConfigAttributes.CELL_EDITABLE_RULE,
                        IEditableRule.ALWAYS_EDITABLE);
                configRegistry.registerConfigAttribute(
                        CellConfigAttributes.DISPLAY_CONVERTER,
                        new DefaultIntegerDisplayConverter(),
                        DisplayMode.NORMAL);

                configRegistry.registerConfigAttribute(
                        IFreezeConfigAttributes.SEPARATOR_COLOR,
                        GUIHelper.COLOR_RED);
            }
        });
        natTable.configure();

        return panel;
    }

    private List<NumberValues> createNumberValueList() {
        List<NumberValues> result = new ArrayList<>();

        for (int i = 0; i < 25; i++) {
            NumberValues nv = new NumberValues();
            nv.setColumnOneNumber(5);
            nv.setColumnTwoNumber(4);
            nv.setColumnThreeNumber(3);
            nv.setColumnFourNumber(1);
            nv.setColumnFiveNumber(1);
            result.add(nv);

            nv = new NumberValues();
            nv.setColumnOneNumber(1);
            nv.setColumnTwoNumber(1);
            nv.setColumnThreeNumber(2);
            nv.setColumnFourNumber(2);
            nv.setColumnFiveNumber(3);
            result.add(nv);

            nv = new NumberValues();
            nv.setColumnOneNumber(1);
            nv.setColumnTwoNumber(2);
            nv.setColumnThreeNumber(2);
            nv.setColumnFourNumber(3);
            nv.setColumnFiveNumber(3);
            result.add(nv);

            nv = new NumberValues();
            nv.setColumnOneNumber(1);
            nv.setColumnTwoNumber(2);
            nv.setColumnThreeNumber(4);
            nv.setColumnFourNumber(4);
            nv.setColumnFiveNumber(3);
            result.add(nv);

            nv = new NumberValues();
            nv.setColumnOneNumber(5);
            nv.setColumnTwoNumber(4);
            nv.setColumnThreeNumber(4);
            nv.setColumnFourNumber(4);
            nv.setColumnFiveNumber(7);
            result.add(nv);
        }

        return result;
    }

    /**
     * The body layer stack for the
     * {@link _816_EditableFixedSummaryRowWithFreezeExample}. Consists of
     * <ol>
     * <li>CompositeLayer - SummaryRowLayer included on top or bottom</li>
     * <li>CompositeFreezeLayer</li>
     * <li>ViewportLayer</li>
     * <li>SelectionLayer</li>
     * <li>ColumnHideShowLayer</li>
     * <li>ColumnReorderLayer</li>
     * <li>DataLayer</li>
     * </ol>
     */
    class SummaryRowBodyLayerStack extends AbstractLayerTransform {

        private final DataLayer bodyDataLayer;
        private FixedSummaryRowLayer summaryRowLayer;
        private final ColumnReorderLayer columnReorderLayer;
        private final ColumnHideShowLayer columnHideShowLayer;
        private final SelectionLayer selectionLayer;
        private final ViewportLayer viewportLayer;
        private final FreezeLayer freezeLayer;
        private final CompositeFreezeLayer compositeFreezeLayer;

        public SummaryRowBodyLayerStack(
                IDataProvider dataProvider, ConfigRegistry configRegistry, boolean summaryRowOnTop) {
            this.bodyDataLayer = new DataLayer(dataProvider);
            this.columnReorderLayer = new ColumnReorderLayer(this.bodyDataLayer);
            this.columnHideShowLayer = new ColumnHideShowLayer(this.columnReorderLayer);
            this.selectionLayer = new SelectionLayer(this.columnHideShowLayer);
            this.viewportLayer = new ViewportLayer(this.selectionLayer);
            this.freezeLayer = new FreezeLayer(this.selectionLayer);
            this.compositeFreezeLayer = new CompositeFreezeLayer(this.freezeLayer, this.viewportLayer, this.selectionLayer);

            if (summaryRowOnTop) {
                // create a standalone FixedSummaryRowLayer
                // since the summary row should be fixed at the top of the body
                // region the horizontal dependency of the FixedSummaryRowLayer
                // is the CompositeFreezeLayer
                this.summaryRowLayer =
                        new FixedSummaryRowLayer(this.bodyDataLayer, this.compositeFreezeLayer, configRegistry, false);
                // because the horizontal dependency is the CompositeFreezeLayer
                // we need to set the composite dependency to false
                this.summaryRowLayer.setHorizontalCompositeDependency(false);

                this.summaryRowLayer.addConfiguration(
                        new ExampleSummaryRowGridConfiguration(this.bodyDataLayer.getDataProvider()));

                CompositeLayer composite = new CompositeLayer(1, 2);
                composite.setChildLayer(SUMMARY_REGION, this.summaryRowLayer, 0, 0);
                composite.setChildLayer(GridRegion.BODY, this.compositeFreezeLayer, 0, 1);

                setUnderlyingLayer(composite);
            } else {
                setUnderlyingLayer(this.compositeFreezeLayer);
            }

            registerCommandHandler(new CopyDataCommandHandler(this.selectionLayer));
        }

        public DataLayer getDataLayer() {
            return this.bodyDataLayer;
        }

        public SelectionLayer getSelectionLayer() {
            return this.selectionLayer;
        }

        public ViewportLayer getViewportLayer() {
            return this.viewportLayer;
        }

        public CompositeFreezeLayer getCompositeFreezeLayer() {
            return this.compositeFreezeLayer;
        }

        public FixedSummaryRowLayer getSummaryRowLayer() {
            return this.summaryRowLayer;
        }
    }

    /**
     * The {@link GridLayer} used by the
     * {@link _816_EditableFixedSummaryRowWithFreezeExample}.
     */
    class SummaryRowGridLayer extends GridLayer {

        public SummaryRowGridLayer(
                IDataProvider dataProvider, ConfigRegistry configRegistry,
                final String[] propertyNames, Map<String, String> propertyToLabelMap,
                boolean summaryRowOnTop) {
            super(true);
            init(dataProvider, configRegistry, propertyNames, propertyToLabelMap, summaryRowOnTop);
        }

        private void init(
                IDataProvider dataProvider, ConfigRegistry configRegistry,
                final String[] propertyNames, Map<String, String> propertyToLabelMap,
                final boolean summaryRowOnTop) {
            // Body
            SummaryRowBodyLayerStack bodyLayer =
                    new SummaryRowBodyLayerStack(dataProvider, configRegistry, summaryRowOnTop);

            DataLayer bodyDataLayer = bodyLayer.getDataLayer();
            ColumnOverrideLabelAccumulator cellLabelAccumulator = new ColumnOverrideLabelAccumulator(bodyDataLayer);
            bodyDataLayer.setConfigLabelAccumulator(cellLabelAccumulator);
            for (int i = 0; i < propertyNames.length; i++) {
                cellLabelAccumulator.registerColumnOverrides(i, propertyNames[i]);
            }

            SelectionLayer selectionLayer = bodyLayer.getSelectionLayer();

            // Column header
            IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(
                    propertyNames, propertyToLabelMap);

            // we should be always dependent to the bodyLayer to react on
            // structural changes
            DefaultColumnHeaderDataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
            columnHeaderDataLayer.setConfigLabelAccumulator(new ColumnLabelAccumulator());
            ILayer columnHeaderLayer = new ColumnHeaderLayer(
                    columnHeaderDataLayer,
                    bodyLayer, selectionLayer);

            // Row header
            IDataProvider rowHeaderDataProvider =
                    new DefaultRowHeaderDataProvider(bodyDataLayer.getDataProvider());
            final DataLayer rowHeaderDataLayer =
                    new DefaultRowHeaderDataLayer(rowHeaderDataProvider);

            ILayer rowHeaderLayer = null;
            if (summaryRowOnTop) {
                // in case of a summary row on top in the body region we use a
                // specific FixedSummaryRowHeaderLayer
                rowHeaderLayer = new FixedSummaryRowHeaderLayer(rowHeaderDataLayer,
                        bodyLayer, selectionLayer);
                ((FixedSummaryRowHeaderLayer) rowHeaderLayer).setSummaryRowLabel("\u2211");
            } else {
                rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer,
                        bodyLayer, selectionLayer);
            }

            // Corner
            ILayer cornerLayer = new CornerLayer(new DataLayer(
                    new DefaultCornerDataProvider(columnHeaderDataProvider,
                            rowHeaderDataProvider)),
                    rowHeaderLayer,
                    columnHeaderLayer);

            setBodyLayer(bodyLayer);
            setColumnHeaderLayer(columnHeaderLayer);
            setRowHeaderLayer(rowHeaderLayer);
            setCornerLayer(cornerLayer);
        }

        public DataLayer getBodyDataLayer() {
            return ((SummaryRowBodyLayerStack) getBodyLayer()).getDataLayer();
        }
    }

    class ExampleSummaryRowGridConfiguration extends DefaultSummaryRowConfiguration {

        private final IDataProvider dataProvider;

        public ExampleSummaryRowGridConfiguration(IDataProvider dataProvider) {
            this.dataProvider = dataProvider;
            this.summaryRowBgColor = GUIHelper.COLOR_BLUE;
            this.summaryRowFgColor = GUIHelper.COLOR_WHITE;
        }

        @Override
        public void addSummaryProviderConfig(IConfigRegistry configRegistry) {
            // Labels are applied to the summary row and cells by default to
            // make configuration easier.
            // See the Javadoc for the SummaryRowLayer

            // Default summary provider
            configRegistry.registerConfigAttribute(
                    SummaryRowConfigAttributes.SUMMARY_PROVIDER,
                    new SummationSummaryProvider(this.dataProvider), DisplayMode.NORMAL,
                    SummaryRowLayer.DEFAULT_SUMMARY_ROW_CONFIG_LABEL);

            // Average summary provider for column index 2
            configRegistry.registerConfigAttribute(
                    SummaryRowConfigAttributes.SUMMARY_PROVIDER,
                    new AverageSummaryProvider(), DisplayMode.NORMAL,
                    SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX + 4);
        }

        /**
         * Custom summary provider which averages out the contents of the column
         */
        class AverageSummaryProvider implements ISummaryProvider {
            @Override
            public Object summarize(int columnIndex) {
                double total = 0;
                int rowCount = ExampleSummaryRowGridConfiguration.this.dataProvider.getRowCount();

                for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                    Object dataValue = ExampleSummaryRowGridConfiguration.this.dataProvider.getDataValue(columnIndex,
                            rowIndex);
                    total = total + Double.parseDouble(dataValue.toString());
                }
                return "Avg: " + total / rowCount;
            }
        }
    }

}
