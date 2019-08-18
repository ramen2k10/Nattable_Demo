/*******************************************************************************
 * Copyright (c) 2013, 2018 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@googlemail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.nattable.examples._800_Integration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommandHandler;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.ExtendedReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.dataset.person.Address;
import org.eclipse.nebula.widgets.nattable.dataset.person.ExtendedPersonWithAddress;
import org.eclipse.nebula.widgets.nattable.dataset.person.Person;
import org.eclipse.nebula.widgets.nattable.dataset.person.Person.Gender;
import org.eclipse.nebula.widgets.nattable.dataset.person.PersonService;
import org.eclipse.nebula.widgets.nattable.examples.AbstractNatExample;
import org.eclipse.nebula.widgets.nattable.examples.runner.StandaloneNatExampleRunner;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.DefaultGlazedListsFilterStrategy;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByConfigAttributes;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByConfigLabelModifier;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByDataLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.ModernGroupByThemeExtension;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary.IGroupBySummaryProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary.SummationGroupBySummaryProvider;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowHeaderComposite;
import org.eclipse.nebula.widgets.nattable.freeze.CompositeFreezeLayer;
import org.eclipse.nebula.widgets.nattable.freeze.FreezeLayer;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultSummaryRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupExpandCollapseLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupReorderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.AbstractOverrider;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.IVisualChangeEvent;
import org.eclipse.nebula.widgets.nattable.painter.NatTableBorderOverlayPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.theme.ModernNatTableThemeConfiguration;
import org.eclipse.nebula.widgets.nattable.style.theme.ThemeConfiguration;
import org.eclipse.nebula.widgets.nattable.summaryrow.ISummaryProvider;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummaryDisplayConverter;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummaryRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummaryRowLayer;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummationSummaryProvider;
import org.eclipse.nebula.widgets.nattable.tree.TreeLayer;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeCollapseAllCommand;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeExpandAllCommand;
import org.eclipse.nebula.widgets.nattable.ui.menu.AbstractHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;

/**
 * Simple example showing how to add the group by feature to the layer
 * composition of a grid in conjunction with showing summary values of
 * groupings.
 */
public class _810_SortableGroupByFilterColumnGroupAndFreezeExample extends AbstractNatExample {

    private static final String ROW_HEADER_SUMMARY_ROW = "rowHeaderSummaryRowLabel";

    private IGroupBySummaryProvider<ExtendedPersonWithAddress> sumMoneySummaryProvider;
    private IGroupBySummaryProvider<ExtendedPersonWithAddress> avgMoneySummaryProvider;

    private boolean useMoneySum = true;

    private ColumnGroupModel columnGroupModel = new ColumnGroupModel();

    public static void main(String[] args) throws Exception {
        StandaloneNatExampleRunner.run(new _810_SortableGroupByFilterColumnGroupAndFreezeExample());
    }

    @Override
    public String getDescription() {
        return "This example shows the usage of the group by feature in conjunction with summary values of the groupings.";
    }

    @Override
    public Control createExampleControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        // create a new ConfigRegistry which will be needed for GlazedLists
        // handling
        final ConfigRegistry configRegistry = new ConfigRegistry();

        // property names of the ExtendedPersonWithAddress class
        String[] propertyNames = { "firstName", "lastName", "age", "money",
                "married", "gender", "birthday" };

        // mapping from property to label, needed for column header labels
        Map<String, String> propertyToLabelMap = new HashMap<>();
        propertyToLabelMap.put("firstName", "Firstname");
        propertyToLabelMap.put("lastName", "Lastname");
        propertyToLabelMap.put("age", "Age");
        propertyToLabelMap.put("money", "Money");
        propertyToLabelMap.put("married", "Married");
        propertyToLabelMap.put("gender", "Gender");
        propertyToLabelMap.put("birthday", "Birthday");

        final IColumnPropertyAccessor<ExtendedPersonWithAddress> columnPropertyAccessor =
                new ExtendedReflectiveColumnPropertyAccessor<>(propertyNames);

        // to enable the group by summary feature, the GroupByDataLayer needs to
        // know the ConfigRegistry
        final BodyLayerStack<ExtendedPersonWithAddress> bodyLayerStack =
                new BodyLayerStack<>(
                        PersonService.getExtendedPersonsWithAddress(10),
                        columnPropertyAccessor,
                        configRegistry);

        bodyLayerStack.getBodyDataLayer().setConfigLabelAccumulator(new ColumnLabelAccumulator());

        // build the column header layer
        IDataProvider columnHeaderDataProvider =
                new DefaultColumnHeaderDataProvider(propertyNames, propertyToLabelMap);
        DataLayer columnHeaderDataLayer =
                new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
        ColumnHeaderLayer columnHeaderLayer =
                new ColumnHeaderLayer(columnHeaderDataLayer, bodyLayerStack, bodyLayerStack.getSelectionLayer());

        // add sorting
        SortHeaderLayer<ExtendedPersonWithAddress> sortHeaderLayer = new SortHeaderLayer<>(
                columnHeaderLayer,
                new GlazedListsSortModel<>(
                        bodyLayerStack.getSortedList(),
                        columnPropertyAccessor,
                        configRegistry,
                        columnHeaderDataLayer),
                false);

        // connect sortModel to GroupByDataLayer to support sorting by group by
        // summary values
        bodyLayerStack.getBodyDataLayer().initializeTreeComparator(
                sortHeaderLayer.getSortModel(),
                bodyLayerStack.getTreeLayer(),
                true);

        ColumnGroupHeaderLayer columnGroupHeaderLayer = new ColumnGroupHeaderLayer(
                sortHeaderLayer,
                bodyLayerStack.getSelectionLayer(),
                this.columnGroupModel);
        columnGroupHeaderLayer.setCalculateHeight(true);

        // add the filter row functionality
        final FilterRowHeaderComposite<ExtendedPersonWithAddress> filterRowHeaderLayer =
                new FilterRowHeaderComposite<>(
                        new DefaultGlazedListsFilterStrategy<>(
                                bodyLayerStack.getFilterList(),
                                columnPropertyAccessor,
                                configRegistry),
                        columnGroupHeaderLayer,
                        columnHeaderDataLayer.getDataProvider(),
                        configRegistry);

        // Row header
        // Adding the specialized DefaultSummaryRowHeaderDataProvider to
        // indicate the summary row in the row header
        IDataProvider rowHeaderDataProvider =
                new DefaultSummaryRowHeaderDataProvider(bodyLayerStack.getBodyDataLayer().getDataProvider(), "\u2211");
        final DataLayer rowHeaderDataLayer =
                new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
        // add a label to the row header summary row cell aswell, so it can be
        // styled differently too
        // in this case it will simply use the same styling as the summary row
        // in the body
        rowHeaderDataLayer.setConfigLabelAccumulator(new AbstractOverrider() {
            @Override
            public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
                if ((rowPosition + 1) == rowHeaderDataLayer.getRowCount()) {
                    configLabels.addLabel(ROW_HEADER_SUMMARY_ROW);
                    configLabels.addLabel(SummaryRowLayer.DEFAULT_SUMMARY_ROW_CONFIG_LABEL);
                }
            }
        });
        ILayer rowHeaderLayer =
                new RowHeaderLayer(rowHeaderDataLayer, bodyLayerStack, bodyLayerStack.getSelectionLayer());

        // build the corner layer
        IDataProvider cornerDataProvider =
                new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider);
        DataLayer cornerDataLayer =
                new DataLayer(cornerDataProvider);
        ILayer cornerLayer =
                new CornerLayer(cornerDataLayer, rowHeaderLayer, filterRowHeaderLayer);

        // build the grid layer
        GridLayer gridLayer = new GridLayer(bodyLayerStack, filterRowHeaderLayer, rowHeaderLayer, cornerLayer);

        // set the group by header on top of the grid
        CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
        final GroupByHeaderLayer groupByHeaderLayer = new GroupByHeaderLayer(
                bodyLayerStack.getGroupByModel(),
                gridLayer,
                columnHeaderDataProvider,
                columnHeaderLayer);
        compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, groupByHeaderLayer, 0, 0);
        compositeGridLayer.setChildLayer("Grid", gridLayer, 0, 1);

        // turn the auto configuration off as we want to add our header menu
        // configuration
        final NatTable natTable = new NatTable(container, compositeGridLayer, false);

        // as the autoconfiguration of the NatTable is turned off, we have to
        // add the DefaultNatTableStyleConfiguration and the ConfigRegistry
        // manually
        natTable.setConfigRegistry(configRegistry);
        natTable.addConfiguration(new DefaultNatTableStyleConfiguration());

        // add some additional styling
        natTable.addConfiguration(new AbstractRegistryConfiguration() {

            @Override
            public void configureRegistry(IConfigRegistry configRegistry) {
                configRegistry.registerConfigAttribute(
                        CellConfigAttributes.CELL_PAINTER,
                        new CheckBoxPainter(), DisplayMode.NORMAL,
                        ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 4);

                configRegistry.registerConfigAttribute(
                        CellConfigAttributes.DISPLAY_CONVERTER,
                        new DefaultDoubleDisplayConverter(),
                        DisplayMode.NORMAL,
                        ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 3);
            }
        });

        // add sorting configuration
        natTable.addConfiguration(new SingleClickSortConfiguration());

        this.sumMoneySummaryProvider =
                new SummationGroupBySummaryProvider<>(columnPropertyAccessor);
        this.avgMoneySummaryProvider =
                new AverageMoneyGroupBySummaryProvider();

        // add group by summary configuration
        natTable.addConfiguration(new AbstractRegistryConfiguration() {

            @Override
            public void configureRegistry(IConfigRegistry configRegistry) {
                configRegistry.registerConfigAttribute(
                        GroupByConfigAttributes.GROUP_BY_SUMMARY_PROVIDER,
                        _810_SortableGroupByFilterColumnGroupAndFreezeExample.this.sumMoneySummaryProvider,
                        DisplayMode.NORMAL,
                        GroupByDataLayer.GROUP_BY_COLUMN_PREFIX + 3);

                configRegistry.registerConfigAttribute(
                        GroupByConfigAttributes.GROUP_BY_SUMMARY_PROVIDER,
                        new AverageAgeGroupBySummaryProvider(),
                        DisplayMode.NORMAL,
                        GroupByDataLayer.GROUP_BY_COLUMN_PREFIX + 2);

                configRegistry.registerConfigAttribute(
                        GroupByConfigAttributes.GROUP_BY_CHILD_COUNT_PATTERN,
                        "[{0}] - ({1})");

                configRegistry.registerConfigAttribute(
                        GroupByConfigAttributes.GROUP_BY_HINT,
                        "Drag columns here");

                Style hintStyle = new Style();
                hintStyle.setAttributeValue(
                        CellStyleAttributes.FONT,
                        GUIHelper.getFont(new FontData("Arial", 10, SWT.ITALIC)));
                configRegistry.registerConfigAttribute(
                        GroupByConfigAttributes.GROUP_BY_HINT_STYLE,
                        hintStyle);

                configRegistry.registerConfigAttribute(
                        SummaryRowConfigAttributes.SUMMARY_PROVIDER,
                        new SummationSummaryProvider(bodyLayerStack.bodyDataProvider, false),
                        DisplayMode.NORMAL,
                        SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX + 3);

                configRegistry.registerConfigAttribute(
                        SummaryRowConfigAttributes.SUMMARY_PROVIDER,
                        new AverageAgeSummaryProvider(bodyLayerStack.bodyDataProvider),
                        DisplayMode.NORMAL,
                        SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX + 2);

                configRegistry.registerConfigAttribute(
                        CellConfigAttributes.DISPLAY_CONVERTER,
                        new SummaryDisplayConverter(new DefaultDoubleDisplayConverter()),
                        DisplayMode.NORMAL,
                        SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX + 3);

                // the main styling of the summary row cell in the row header is
                // done via summary row default style, but we need to override
                // the alignment
                IStyle style = new Style();
                style.setAttributeValue(
                        CellStyleAttributes.HORIZONTAL_ALIGNMENT,
                        HorizontalAlignmentEnum.CENTER);
                configRegistry.registerConfigAttribute(
                        CellConfigAttributes.CELL_STYLE,
                        style,
                        DisplayMode.NORMAL, ROW_HEADER_SUMMARY_ROW);
                configRegistry.registerConfigAttribute(
                        CellConfigAttributes.CELL_STYLE,
                        style,
                        DisplayMode.SELECT, ROW_HEADER_SUMMARY_ROW);
            }
        });

        // add group by header configuration
        natTable.addConfiguration(new GroupByHeaderMenuConfiguration(natTable,
                groupByHeaderLayer));

        natTable.addConfiguration(new AbstractHeaderMenuConfiguration(natTable) {

            @Override
            protected PopupMenuBuilder createColumnHeaderMenu(NatTable natTable) {
                return super.createColumnHeaderMenu(natTable)
                        .withHideColumnMenuItem().withShowAllColumnsMenuItem()
                        .withColumnChooserMenuItem()
                        .withCreateColumnGroupsMenuItem()
                        .withUngroupColumnsMenuItem()
                        .withAutoResizeSelectedColumnsMenuItem()
                        .withColumnRenameDialog().withClearAllFilters()
                        .withStateManagerMenuItemProvider();
            }

            @Override
            protected PopupMenuBuilder createCornerMenu(NatTable natTable) {
                return super.createCornerMenu(natTable)
                        .withShowAllColumnsMenuItem()
                        .withStateManagerMenuItemProvider();
            }
        });

        natTable.configure();

        natTable.registerCommandHandler(
                new DisplayPersistenceDialogCommandHandler(natTable));

        DisplayColumnChooserCommandHandler columnChooserCommandHandler =
                new DisplayColumnChooserCommandHandler(
                        bodyLayerStack.getSelectionLayer(),
                        bodyLayerStack.getColumnHideShowLayer(),
                        columnHeaderLayer,
                        columnHeaderDataLayer,
                        null,
                        null);
        natTable.registerCommandHandler(columnChooserCommandHandler);

        GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

        Composite buttonPanel = new Composite(container, SWT.NONE);
        buttonPanel.setLayout(new RowLayout());
        GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);

        Button toggleHeaderButton = new Button(buttonPanel, SWT.PUSH);
        toggleHeaderButton.setText("Toggle Group By Header");
        toggleHeaderButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                groupByHeaderLayer.setVisible(!groupByHeaderLayer.isVisible());
            }
        });

        Button toggleFilterButton = new Button(buttonPanel, SWT.PUSH);
        toggleFilterButton.setText("Toggle Filter Row");
        toggleFilterButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                filterRowHeaderLayer.setFilterRowVisible(!filterRowHeaderLayer.isFilterRowVisible());
            }
        });

        Button collapseAllButton = new Button(buttonPanel, SWT.PUSH);
        collapseAllButton.setText("Collapse All");
        collapseAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                natTable.doCommand(new TreeCollapseAllCommand());
            }
        });

        Button expandAllButton = new Button(buttonPanel, SWT.PUSH);
        expandAllButton.setText("Expand All");
        expandAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                natTable.doCommand(new TreeExpandAllCommand());
            }
        });

        Button toggleMoneySummaryButton = new Button(buttonPanel, SWT.PUSH);
        toggleMoneySummaryButton.setText("Toggle Money Group Summary (SUM/AVG)");
        toggleMoneySummaryButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // clear the group by summary cache so the new summary
                // calculation gets triggered
                bodyLayerStack.getBodyDataLayer().clearCache();

                _810_SortableGroupByFilterColumnGroupAndFreezeExample.this.useMoneySum =
                        !_810_SortableGroupByFilterColumnGroupAndFreezeExample.this.useMoneySum;
                if (_810_SortableGroupByFilterColumnGroupAndFreezeExample.this.useMoneySum) {
                    configRegistry.registerConfigAttribute(
                            GroupByConfigAttributes.GROUP_BY_SUMMARY_PROVIDER,
                            _810_SortableGroupByFilterColumnGroupAndFreezeExample.this.sumMoneySummaryProvider,
                            DisplayMode.NORMAL,
                            GroupByDataLayer.GROUP_BY_COLUMN_PREFIX + 3);
                } else {
                    configRegistry.registerConfigAttribute(
                            GroupByConfigAttributes.GROUP_BY_SUMMARY_PROVIDER,
                            _810_SortableGroupByFilterColumnGroupAndFreezeExample.this.avgMoneySummaryProvider,
                            DisplayMode.NORMAL,
                            GroupByDataLayer.GROUP_BY_COLUMN_PREFIX + 3);
                }
                natTable.doCommand(new VisualRefreshCommand());
            }
        });

        // this button adds data to the grid
        // try to group by last name, sort by last name desc and then add
        // dynamic data for verification
        Button addDynamicDataButton = new Button(buttonPanel, SWT.PUSH);
        addDynamicDataButton.setText("Add Data");
        addDynamicDataButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Address address = new Address();
                address.setStreet("Some Street");
                address.setHousenumber(42);
                address.setPostalCode(12345);
                address.setCity("In the clouds");

                Person person = new Person(42, "Ralph", "Wiggum", Gender.MALE, false, new Date());
                ExtendedPersonWithAddress entry = new ExtendedPersonWithAddress(person, address,
                        "0000", "The little Ralphy", PersonService.createRandomMoneyAmount(),
                        new ArrayList<String>(), new ArrayList<String>());
                bodyLayerStack.getEventList().add(entry);

                person = new Person(42, "Clancy", "Wiggum", Gender.MALE, true, new Date());
                entry = new ExtendedPersonWithAddress(person, address,
                        "XXXL", "It is Chief Wiggum", PersonService.createRandomMoneyAmount(),
                        new ArrayList<String>(), new ArrayList<String>());
                bodyLayerStack.getEventList().add(entry);

                person = new Person(42, "Sarah", "Wiggum", Gender.FEMALE, true, new Date());
                entry = new ExtendedPersonWithAddress(person, address,
                        "mommy", "Little Ralphy's mother", PersonService.createRandomMoneyAmount(),
                        new ArrayList<String>(), new ArrayList<String>());
                bodyLayerStack.getEventList().add(entry);
            }
        });

        // add modern styling
        ThemeConfiguration theme = new ModernNatTableThemeConfiguration();
        theme.addThemeExtension(new ModernGroupByThemeExtension());
        natTable.setTheme(theme);

        // add a border on every side of the table
        natTable.addOverlayPainter(new NatTableBorderOverlayPainter());

        this.natTable = natTable;

        return container;
    }

    /**
     * Always encapsulate the body layer stack in an AbstractLayerTransform to
     * ensure that the index transformations are performed in later commands.
     *
     * @param <T>
     */
    class BodyLayerStack<T> extends AbstractLayerTransform {

        private final EventList<T> eventList;
        private final SortedList<T> sortedList;
        private final FilterList<T> filterList;

        private final IDataProvider bodyDataProvider;

        private final GroupByDataLayer<T> bodyDataLayer;

        private final ColumnHideShowLayer columnHideShowLayer;
        private final SelectionLayer selectionLayer;

        private final TreeLayer treeLayer;

        private final GroupByModel groupByModel = new GroupByModel();

        public BodyLayerStack(List<T> values,
                IColumnPropertyAccessor<T> columnPropertyAccessor,
                ConfigRegistry configRegistry) {
            // wrapping of the list to show into GlazedLists
            // see http://publicobject.com/glazedlists/ for further information
            this.eventList = GlazedLists.eventList(values);
            TransformedList<T, T> rowObjectsGlazedList = GlazedLists.threadSafeList(this.eventList);

            // use the SortedList constructor with 'null' for the Comparator
            // because the Comparator
            // will be set by configuration
            this.sortedList = new SortedList<>(rowObjectsGlazedList, null);
            // wrap the SortedList with the FilterList
            this.filterList = new FilterList<>(this.sortedList);

            // Use the GroupByDataLayer instead of the default DataLayer
            this.bodyDataLayer = new GroupByDataLayer<>(
                    getGroupByModel(),
                    this.filterList,
                    columnPropertyAccessor,
                    configRegistry);
            // get the IDataProvider that was created by the GroupByDataLayer
            this.bodyDataProvider = this.bodyDataLayer.getDataProvider();

            // layer for event handling of GlazedLists and PropertyChanges
            GlazedListsEventLayer<T> glazedListsEventLayer =
                    new GlazedListsEventLayer<>(this.bodyDataLayer, this.filterList);

            // NOTE:
            // we need to tell the GroupByDataLayer to clear its cache if
            // a IVisualChangeEvent occurs. This is necessary because the
            // GlazedListsEventLayer transforms GlazedLists change events to
            // NatTable change events and fires the event the layer stack
            // upwards. But as it sits on top of the GroupByDataLayer, the
            // GroupByDataLayer never gets informed about the change.
            glazedListsEventLayer.addLayerListener(new ILayerListener() {

                @Override
                public void handleLayerEvent(ILayerEvent event) {
                    if (event instanceof IVisualChangeEvent) {
                        BodyLayerStack.this.bodyDataLayer.clearCache();
                    }
                }
            });

            SummaryRowLayer summaryRowLayer =
                    new SummaryRowLayer(glazedListsEventLayer, configRegistry, false);

            ColumnReorderLayer columnReorderLayer =
                    new ColumnReorderLayer(summaryRowLayer);
            ColumnGroupReorderLayer columnGroupReorderLayer =
                    new ColumnGroupReorderLayer(
                            columnReorderLayer,
                            _810_SortableGroupByFilterColumnGroupAndFreezeExample.this.columnGroupModel);
            this.columnHideShowLayer =
                    new ColumnHideShowLayer(columnGroupReorderLayer);
            ColumnGroupExpandCollapseLayer columnGroupExpandCollapseLayer =
                    new ColumnGroupExpandCollapseLayer(
                            this.columnHideShowLayer,
                            _810_SortableGroupByFilterColumnGroupAndFreezeExample.this.columnGroupModel);

            this.selectionLayer = new SelectionLayer(columnGroupExpandCollapseLayer);

            // add a tree layer to visualise the grouping
            this.treeLayer = new TreeLayer(this.selectionLayer, this.bodyDataLayer.getTreeRowModel());

            ViewportLayer viewportLayer = new ViewportLayer(this.treeLayer);

            // this will avoid tree specific rendering regarding alignment and
            // indentation in case no grouping is active
            viewportLayer.setConfigLabelAccumulator(new GroupByConfigLabelModifier(getGroupByModel()));

            FreezeLayer freezeLayer = new FreezeLayer(this.treeLayer);
            CompositeFreezeLayer compositeFreezeLayer =
                    new CompositeFreezeLayer(freezeLayer, viewportLayer, this.selectionLayer);

            setUnderlyingLayer(compositeFreezeLayer);
        }

        public ColumnHideShowLayer getColumnHideShowLayer() {
            return this.columnHideShowLayer;
        }

        public TreeLayer getTreeLayer() {
            return this.treeLayer;
        }

        public SelectionLayer getSelectionLayer() {
            return this.selectionLayer;
        }

        public EventList<T> getEventList() {
            return this.eventList;
        }

        public SortedList<T> getSortedList() {
            return this.sortedList;
        }

        public FilterList<T> getFilterList() {
            return this.filterList;
        }

        public IDataProvider getBodyDataProvider() {
            return this.bodyDataProvider;
        }

        public GroupByDataLayer<T> getBodyDataLayer() {
            return this.bodyDataLayer;
        }

        public GroupByModel getGroupByModel() {
            return this.groupByModel;
        }
    }

    /**
     * Example implementation for a typed IGroupBySummaryProvider that
     * calculates the average age of ExtendedPersonWithAddress objects in a
     * grouping.
     */
    class AverageAgeGroupBySummaryProvider implements IGroupBySummaryProvider<ExtendedPersonWithAddress> {

        @Override
        public Object summarize(int columnIndex, List<ExtendedPersonWithAddress> children) {
            int summaryValue = 0;
            for (ExtendedPersonWithAddress child : children) {
                summaryValue += child.getAge();
            }
            return summaryValue / (children.size() > 0 ? children.size() : 1);
        }

    }

    /**
     * Example implementation for a typed IGroupBySummaryProvider that
     * calculates the average money of ExtendedPersonWithAddress objects in a
     * grouping.
     */
    class AverageMoneyGroupBySummaryProvider implements IGroupBySummaryProvider<ExtendedPersonWithAddress> {

        @Override
        public Object summarize(int columnIndex, List<ExtendedPersonWithAddress> children) {
            int summaryValue = 0;
            for (ExtendedPersonWithAddress child : children) {
                summaryValue += child.getMoney();
            }
            return summaryValue / (children.size() > 0 ? children.size() : 1);
        }

    }

    /**
     * Example implementation for a ISummaryProvider that calculates the average
     * age of ExtendedPersonWithAddress objects.
     */
    class AverageAgeSummaryProvider implements ISummaryProvider {

        private IDataProvider dataProvider;

        public AverageAgeSummaryProvider(IDataProvider dataProvider) {
            this.dataProvider = dataProvider;
        }

        @Override
        public Object summarize(int columnIndex) {
            double total = 0;
            int rowCount = this.dataProvider.getRowCount();
            int valueRows = 0;

            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                Object dataValue = this.dataProvider.getDataValue(columnIndex, rowIndex);
                // this check is necessary because of the GroupByObject
                if (dataValue instanceof Number) {
                    total = total + Double.parseDouble(dataValue.toString());
                    valueRows++;
                }
            }
            return "Avg: " + String.format("%.2f", total / valueRows);
        }

    }

    /**
     * Example implementation for a ISummaryProvider that calculates the average
     * money of ExtendedPersonWithAddress objects.
     */
    class AverageMoneySummaryProvider implements ISummaryProvider {

        private IDataProvider dataProvider;

        public AverageMoneySummaryProvider(IDataProvider dataProvider) {
            this.dataProvider = dataProvider;
        }

        @Override
        public Object summarize(int columnIndex) {
            double total = 0;
            int rowCount = this.dataProvider.getRowCount();
            int valueRows = 0;

            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                Object dataValue = this.dataProvider.getDataValue(columnIndex, rowIndex);
                // this check is necessary because of the GroupByObject
                if (dataValue instanceof Number) {
                    total = total + Double.parseDouble(dataValue.toString());
                    valueRows++;
                }
            }
            return "Avg: " + String.format("%.2f", total / valueRows);
        }

    }

    public static final String PROPERTIES_FILE = "myState.properties";
    public NatTable natTable;

    @Override
    public void onStart() {
        Properties properties = new Properties();

        try (InputStream in = new FileInputStream(new File(PROPERTIES_FILE))) {
            System.out.println("Loading NatTable state from " + PROPERTIES_FILE);
            properties.load(in);
            this.natTable.loadState("", properties);
        } catch (FileNotFoundException e) {
            // No file found, oh well, move along
            System.out.println(PROPERTIES_FILE + " not found, skipping load");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStop() {
        Properties properties = new Properties();

        this.natTable.saveState("", properties);

        try (OutputStream out = new FileOutputStream(new File(PROPERTIES_FILE))) {
            System.out.println("Saving NatTable state to " + PROPERTIES_FILE);
            properties.store(out, "NatTable state");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
