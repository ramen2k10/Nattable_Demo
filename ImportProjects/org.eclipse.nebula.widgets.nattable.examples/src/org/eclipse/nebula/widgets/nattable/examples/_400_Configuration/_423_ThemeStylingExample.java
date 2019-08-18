/*******************************************************************************
 * Copyright (c) 2013, 2015 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@googlemail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.nattable.examples._400_Configuration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDateDisplayConverter;
import org.eclipse.nebula.widgets.nattable.dataset.person.Person;
import org.eclipse.nebula.widgets.nattable.dataset.person.Person.Gender;
import org.eclipse.nebula.widgets.nattable.dataset.person.PersonService;
import org.eclipse.nebula.widgets.nattable.examples.AbstractNatExample;
import org.eclipse.nebula.widgets.nattable.examples.runner.StandaloneNatExampleRunner;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultBodyDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.hover.HoverLayer;
import org.eclipse.nebula.widgets.nattable.hover.config.ColumnHeaderHoverLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.hover.config.RowHeaderHoverLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.AggregateConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.NatTableBorderOverlayPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.theme.DarkNatTableThemeConfiguration;
import org.eclipse.nebula.widgets.nattable.style.theme.DefaultNatTableThemeConfiguration;
import org.eclipse.nebula.widgets.nattable.style.theme.IThemeExtension;
import org.eclipse.nebula.widgets.nattable.style.theme.ModernNatTableThemeConfiguration;
import org.eclipse.nebula.widgets.nattable.style.theme.ThemeConfiguration;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Example showing how to use {@link ThemeConfiguration}s in a NatTable. It
 * shows how to set and how to switch themes at runtime. Note that there are
 * several things that are not controllable via themes like row height, column
 * width or ILayerPainter. The later are tight connected to the ILayer
 * themselves.
 */
public class _423_ThemeStylingExample extends AbstractNatExample {

    public static final String FEMALE_LABEL = "FemaleLabel";
    public static final String MALE_LABEL = "MaleLabel";

    public static void main(String[] args) throws Exception {
        StandaloneNatExampleRunner.run(650, 400, new _423_ThemeStylingExample());
    }

    @Override
    public String getDescription() {
        return "This example shows the usage of themes for styling a NatTable and how"
                + " to switch themes at runtime.  It also shows how to deal with attributes"
                + " that are not configurable via themes like row heights, column widths and"
                + " ILayerPainter.";
    }

    @Override
    public Control createExampleControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        // property names of the Person class
        String[] propertyNames = { "firstName", "lastName", "gender", "married", "birthday" };

        // mapping from property to label, needed for column header labels
        Map<String, String> propertyToLabelMap = new HashMap<>();
        propertyToLabelMap.put("firstName", "Firstname");
        propertyToLabelMap.put("lastName", "Lastname");
        propertyToLabelMap.put("gender", "Gender");
        propertyToLabelMap.put("married", "Married");
        propertyToLabelMap.put("birthday", "Birthday");

        // build the body layer stack
        // Usually you would create a new layer stack by extending
        // AbstractIndexLayerTransform and setting the ViewportLayer
        // as underlying layer. But in this case using the ViewportLayer
        // directly as body layer is also working.
        final ListDataProvider<Person> bodyDataProvider =
                new DefaultBodyDataProvider<>(PersonService.getPersons(10), propertyNames);
        final DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);
        HoverLayer bodyHoverLayer = new HoverLayer(bodyDataLayer);
        SelectionLayer selectionLayer = new SelectionLayer(bodyHoverLayer);
        ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

        // add labels to provider conditional styling
        AggregateConfigLabelAccumulator labelAccumulator = new AggregateConfigLabelAccumulator();
        labelAccumulator.add(new ColumnLabelAccumulator());
        labelAccumulator.add(new IConfigLabelAccumulator() {
            @Override
            public void accumulateConfigLabels(
                    LabelStack configLabels, int columnPosition, int rowPosition) {
                Person p = bodyDataProvider.getRowObject(rowPosition);
                if (p != null) {
                    configLabels.addLabel(p.getGender().equals(Gender.FEMALE) ? FEMALE_LABEL : MALE_LABEL);
                }
            }
        });

        bodyDataLayer.setConfigLabelAccumulator(labelAccumulator);

        // build the column header layer
        IDataProvider columnHeaderDataProvider =
                new DefaultColumnHeaderDataProvider(propertyNames, propertyToLabelMap);
        final DataLayer columnHeaderDataLayer =
                new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
        HoverLayer columnHoverLayer =
                new HoverLayer(columnHeaderDataLayer, false);
        final ColumnHeaderLayer columnHeaderLayer =
                new ColumnHeaderLayer(columnHoverLayer, viewportLayer, selectionLayer, false);

        // add ColumnHeaderHoverLayerConfiguration to ensure that hover styling
        // and resizing is working together
        columnHeaderLayer.addConfiguration(
                new ColumnHeaderHoverLayerConfiguration(columnHoverLayer));

        // build the row header layer
        IDataProvider rowHeaderDataProvider =
                new DefaultRowHeaderDataProvider(bodyDataProvider);
        DataLayer rowHeaderDataLayer =
                new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
        HoverLayer rowHoverLayer =
                new HoverLayer(rowHeaderDataLayer, false);
        RowHeaderLayer rowHeaderLayer =
                new RowHeaderLayer(rowHoverLayer, viewportLayer, selectionLayer, false);

        // add RowHeaderHoverLayerConfiguration to ensure that hover styling and
        // resizing is working together
        rowHeaderLayer.addConfiguration(
                new RowHeaderHoverLayerConfiguration(rowHoverLayer));

        // build the corner layer
        IDataProvider cornerDataProvider =
                new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider);
        DataLayer cornerDataLayer =
                new DataLayer(cornerDataProvider);
        final CornerLayer cornerLayer =
                new CornerLayer(cornerDataLayer, rowHeaderLayer, columnHeaderLayer);

        // build the grid layer
        final GridLayer gridLayer = new GridLayer(
                viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

        final NatTable natTable = new NatTable(container, gridLayer);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

        // adding a full border
        natTable.addOverlayPainter(new NatTableBorderOverlayPainter(natTable.getConfigRegistry()));

        Composite buttonPanel = new Composite(container, SWT.NONE);
        buttonPanel.setLayout(new GridLayout(3, true));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonPanel);

        final ThemeConfiguration defaultTheme = new DefaultNatTableThemeConfiguration();
        final ThemeConfiguration modernTheme = new ModernNatTableThemeConfiguration();
        final ThemeConfiguration darkTheme = new DarkNatTableThemeConfiguration();

        final ThemeConfiguration conditionalDefaultTheme = new DefaultNatTableThemeConfiguration();
        conditionalDefaultTheme.addThemeExtension(new ConditionalStylingThemeExtension());
        final ThemeConfiguration conditionalModernTheme = new ModernNatTableThemeConfiguration();
        conditionalModernTheme.addThemeExtension(new ConditionalStylingThemeExtension());
        final ThemeConfiguration conditionalDarkTheme = new DarkNatTableThemeConfiguration();
        conditionalDarkTheme.addThemeExtension(new ConditionalStylingThemeExtension());

        final ThemeConfiguration hoverTheme = new HoverThemeConfiguration();
        final ThemeConfiguration fontTheme = new FontStylingThemeConfiguration();

        Button defaultThemeButton = new Button(buttonPanel, SWT.PUSH);
        defaultThemeButton.setText("NatTable Default Theme");
        defaultThemeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                natTable.setTheme(defaultTheme);

                // reset to default state
                cleanupNonThemeSettings(gridLayer, bodyDataLayer, columnHeaderDataLayer);
            }
        });

        Button modernThemeButton = new Button(buttonPanel, SWT.PUSH);
        modernThemeButton.setText("NatTable Modern Theme");
        modernThemeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                natTable.setTheme(modernTheme);

                // reset to default state
                cleanupNonThemeSettings(gridLayer, bodyDataLayer, columnHeaderDataLayer);
            }
        });

        Button darkThemeButton = new Button(buttonPanel, SWT.PUSH);
        darkThemeButton.setText("NatTable Dark Theme");
        darkThemeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                natTable.setTheme(darkTheme);

                // reset to default state
                cleanupNonThemeSettings(gridLayer, bodyDataLayer, columnHeaderDataLayer);
            }
        });

        Button conditionalThemeButton = new Button(buttonPanel, SWT.PUSH);
        conditionalThemeButton.setText("Conditional Default Theme");
        conditionalThemeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                natTable.setTheme(conditionalDefaultTheme);

                // reset to default state
                cleanupNonThemeSettings(gridLayer, bodyDataLayer, columnHeaderDataLayer);
            }
        });

        Button conditionalModernThemeButton = new Button(buttonPanel, SWT.PUSH);
        conditionalModernThemeButton.setText("Conditional Modern Theme");
        conditionalModernThemeButton
                .addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        natTable.setTheme(conditionalModernTheme);

                        // reset to default state
                        cleanupNonThemeSettings(gridLayer, bodyDataLayer, columnHeaderDataLayer);
                    }
                });

        Button conditionalDarkThemeButton = new Button(buttonPanel, SWT.PUSH);
        conditionalDarkThemeButton.setText("Conditional Dark Theme");
        conditionalDarkThemeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                natTable.setTheme(conditionalDarkTheme);

                // reset to default state
                cleanupNonThemeSettings(gridLayer, bodyDataLayer, columnHeaderDataLayer);
            }
        });

        Button hoverThemeButton = new Button(buttonPanel, SWT.PUSH);
        hoverThemeButton.setText("Hover Theme");
        hoverThemeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                natTable.setTheme(hoverTheme);

                // reset to default state
                cleanupNonThemeSettings(gridLayer, bodyDataLayer, columnHeaderDataLayer);
            }
        });

        Button fontThemeButton = new Button(buttonPanel, SWT.PUSH);
        fontThemeButton.setText("Increased Font Theme");
        fontThemeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                natTable.setTheme(fontTheme);

                // we are simply increasing the default width and height in this
                // example we could also register TextPainters that calculate
                // their height by content but they are not able to shrink again
                columnHeaderDataLayer.setDefaultRowHeight(30);
                columnHeaderDataLayer.setDefaultColumnWidth(130);
                bodyDataLayer.setDefaultRowHeight(30);
                bodyDataLayer.setDefaultColumnWidth(130);
            }
        });

        return container;
    }

    /**
     * Resets row heights and column widths to default settings.
     */
    private void cleanupNonThemeSettings(GridLayer gridLayer,
            DataLayer bodyDataLayer, DataLayer columnHeaderDataLayer) {

        columnHeaderDataLayer.setDefaultRowHeight(20);
        columnHeaderDataLayer.setDefaultColumnWidth(100);
        bodyDataLayer.setDefaultRowHeight(20);
        bodyDataLayer.setDefaultColumnWidth(100);
    }

    /**
     * ThemeConfiguration that adds hover styling. Note that the stylings are
     * only interpreted because the HoverLayer is involved in the layer stacks.
     */
    class HoverThemeConfiguration extends DefaultNatTableThemeConfiguration {
        {
            this.bodyHoverBgColor = GUIHelper.COLOR_YELLOW;
            this.bodyHoverSelectionBgColor = GUIHelper.COLOR_GREEN;

            this.rHeaderHoverBgColor = GUIHelper.COLOR_RED;
            this.rHeaderHoverSelectionBgColor = GUIHelper.COLOR_BLUE;

            Image bgImage = GUIHelper.getImageByURL("columnHeaderBg",
                    getClass().getResource("/org/eclipse/nebula/widgets/nattable/examples/resources/column_header_bg.png"));

            Image hoverBgImage = GUIHelper.getImageByURL("hoverColumnHeaderBg",
                    getClass().getResource("/org/eclipse/nebula/widgets/nattable/examples/resources/hovered_column_header_bg.png"));

            Image selectedBgImage = GUIHelper.getImageByURL("selectedColumnHeaderBg",
                    getClass().getResource("/org/eclipse/nebula/widgets/nattable/examples/resources/selected_column_header_bg.png"));

            Image selectedHoveredBgImage = GUIHelper.getImageByURL("selectedHoverColumnHeaderBg",
                    getClass().getResource("/org/eclipse/nebula/widgets/nattable/examples/resources/selected_hovered_column_header_bg.png"));

            TextPainter txtPainter = new TextPainter(false, false);

            ICellPainter bgImagePainter =
                    new BackgroundImagePainter(txtPainter, bgImage);

            this.cHeaderCellPainter = bgImagePainter;
            this.cornerCellPainter = bgImagePainter;

            this.cHeaderSelectionCellPainter =
                    new BackgroundImagePainter(txtPainter, selectedBgImage);
            this.cHeaderHoverCellPainter =
                    new BackgroundImagePainter(txtPainter, hoverBgImage);
            this.cHeaderHoverSelectionCellPainter =
                    new BackgroundImagePainter(txtPainter, selectedHoveredBgImage);

            this.renderCornerGridLines = true;
            this.renderColumnHeaderGridLines = true;
        }
    }

    /**
     * IThemeExtension that adds conditional styling. As it is implemented as
     * theme extension, it can be added to any ThemeConfiguration without the
     * need for inheritance.
     */
    class ConditionalStylingThemeExtension implements IThemeExtension {

        @Override
        public void registerStyles(IConfigRegistry configRegistry) {
            // add custom styling
            IStyle femaleStyle = new Style();
            femaleStyle.setAttributeValue(
                    CellStyleAttributes.BACKGROUND_COLOR,
                    GUIHelper.COLOR_YELLOW);
            femaleStyle.setAttributeValue(
                    CellStyleAttributes.FOREGROUND_COLOR,
                    GUIHelper.COLOR_BLACK);
            configRegistry.registerConfigAttribute(
                    CellConfigAttributes.CELL_STYLE,
                    femaleStyle,
                    DisplayMode.NORMAL, FEMALE_LABEL);

            // add custom painter
            configRegistry.registerConfigAttribute(
                    CellConfigAttributes.CELL_PAINTER,
                    new CheckBoxPainter(),
                    DisplayMode.NORMAL,
                    ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + "3");

            // add custom converter
            configRegistry.registerConfigAttribute(
                    CellConfigAttributes.DISPLAY_CONVERTER,
                    new DefaultBooleanDisplayConverter(),
                    DisplayMode.NORMAL,
                    ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + "3");

            DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
            String pattern = ((SimpleDateFormat) formatter).toPattern();

            configRegistry.registerConfigAttribute(
                    CellConfigAttributes.DISPLAY_CONVERTER,
                    new DefaultDateDisplayConverter(pattern),
                    DisplayMode.NORMAL,
                    ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + "4");
        }

        @Override
        public void unregisterStyles(IConfigRegistry configRegistry) {
            // unregister custom styling
            configRegistry.unregisterConfigAttribute(
                    CellConfigAttributes.CELL_STYLE,
                    DisplayMode.NORMAL,
                    FEMALE_LABEL);

            configRegistry.unregisterConfigAttribute(
                    CellConfigAttributes.CELL_PAINTER,
                    DisplayMode.NORMAL,
                    ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + "3");

            configRegistry.unregisterConfigAttribute(
                    CellConfigAttributes.DISPLAY_CONVERTER,
                    DisplayMode.NORMAL,
                    ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + "3");

            configRegistry.unregisterConfigAttribute(
                    CellConfigAttributes.DISPLAY_CONVERTER,
                    DisplayMode.NORMAL,
                    ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + "4");
        }

    }

    /**
     * ThemeConfiguration that sets different fonts which has impact on the row
     * heights and columns widths. The automatic resizing is done via specially
     * configured TextPainter instances.
     */
    class FontStylingThemeConfiguration extends ModernNatTableThemeConfiguration {
        {
            this.defaultFont = GUIHelper.getFont(new FontData("Arial", 15, SWT.NORMAL));
            this.defaultSelectionFont = GUIHelper.getFont(new FontData("Arial", 15, SWT.NORMAL));

            this.cHeaderFont = GUIHelper.getFont(new FontData("Arial", 18, SWT.NORMAL));
            this.cHeaderSelectionFont = GUIHelper.getFont(new FontData("Arial", 18, SWT.NORMAL));

            this.rHeaderFont = GUIHelper.getFont(new FontData("Arial", 18, SWT.NORMAL));
            this.rHeaderSelectionFont = GUIHelper.getFont(new FontData("Arial", 18, SWT.NORMAL));

            this.renderCornerGridLines = true;
            this.renderColumnHeaderGridLines = true;
        }
    }
}
