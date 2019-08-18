/*******************************************************************************
 * Copyright (c) 2013, 2015 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@gogglemail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.nattable.examples._500_Layers._504_Viewport;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.ExtendedReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.dataset.person.PersonService;
import org.eclipse.nebula.widgets.nattable.dataset.person.PersonWithAddress;
import org.eclipse.nebula.widgets.nattable.examples.AbstractNatExample;
import org.eclipse.nebula.widgets.nattable.examples.runner.StandaloneNatExampleRunner;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.painter.IOverlayPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.GridLineCellLayerPainter;
import org.eclipse.nebula.widgets.nattable.print.command.MultiTurnViewportOffCommandHandler;
import org.eclipse.nebula.widgets.nattable.print.command.MultiTurnViewportOnCommandHandler;
import org.eclipse.nebula.widgets.nattable.util.ClientAreaAdapter;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.SliderScroller;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Slider;

/**
 * Example showing how to implement NatTable that contains two horizontal split
 * viewports.
 */
public class _5042_HorizontalSplitViewportExample extends AbstractNatExample {

    public static void main(String[] args) throws Exception {
        StandaloneNatExampleRunner.run(600, 400, new _5042_HorizontalSplitViewportExample());
    }

    @Override
    public String getDescription() {
        return "This example shows a NatTable that contains two separately scrollable "
                + "horzizontal split viewports.";
    }

    @Override
    public Control createExampleControl(Composite parent) {
        // property names of the Person class
        String[] propertyNames = { "firstName", "lastName", "gender", "married", "birthday",
                "address.street", "address.housenumber", "address.postalCode", "address.city" };

        IColumnPropertyAccessor<PersonWithAddress> columnPropertyAccessor =
                new ExtendedReflectiveColumnPropertyAccessor<>(propertyNames);

        IDataProvider bodyDataProvider = new ListDataProvider<>(
                PersonService.getPersonsWithAddress(50), columnPropertyAccessor);
        DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);

        // use a cell layer painter that is configured for left clipping
        // this ensures that the rendering works correctly for split viewports
        bodyDataLayer.setLayerPainter(new GridLineCellLayerPainter(true, false));

        // create a ViewportLayer for the left part of the table and configure
        // it to only contain the first 5 columns
        final ViewportLayer viewportLayerLeft = new ViewportLayer(bodyDataLayer);
        viewportLayerLeft.setMaxColumnPosition(5);

        // create a ViewportLayer for the right part of the table and configure
        // it to only contain the last 4 columns
        ViewportLayer viewportLayerRight = new ViewportLayer(bodyDataLayer);
        viewportLayerRight.setMinColumnPosition(5);

        // create a CompositeLayer that contains both ViewportLayers
        CompositeLayer compositeLayer = new CompositeLayer(2, 1);
        compositeLayer.setChildLayer("REGION_A", viewportLayerLeft, 0, 0);
        compositeLayer.setChildLayer("REGION_B", viewportLayerRight, 1, 0);

        // in order to make printing and exporting work correctly you need to
        // register the following command handlers
        // although in this example printing and exporting is not enabled, we
        // show the registering
        compositeLayer.registerCommandHandler(
                new MultiTurnViewportOnCommandHandler(viewportLayerLeft, viewportLayerRight));
        compositeLayer.registerCommandHandler(
                new MultiTurnViewportOffCommandHandler(viewportLayerLeft, viewportLayerRight));

        // set the width of the left viewport to only showing 2 columns at the
        // same time
        int leftWidth = bodyDataLayer.getStartXOfColumnPosition(2);

        // as the CompositeLayer is setting a IClientAreaProvider for the
        // composition we need to set a special ClientAreaAdapter after the
        // creation of the CompositeLayer to support split viewports
        ClientAreaAdapter leftClientAreaAdapter =
                new ClientAreaAdapter(viewportLayerLeft.getClientAreaProvider());
        leftClientAreaAdapter.setWidth(leftWidth);
        viewportLayerLeft.setClientAreaProvider(leftClientAreaAdapter);

        // Wrap NatTable in composite so we can slap on the external horizontal
        // sliders
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        composite.setLayout(gridLayout);

        NatTable natTable = new NatTable(composite, compositeLayer);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        natTable.setLayoutData(gridData);

        createSplitSliders(composite, viewportLayerLeft, viewportLayerRight);

        // add an IOverlayPainter to ensure the right border of the left
        // viewport always this is necessary because the left border of layer
        // stacks is not rendered by default
        natTable.addOverlayPainter(new IOverlayPainter() {

            @Override
            public void paintOverlay(GC gc, ILayer layer) {
                Color beforeColor = gc.getForeground();
                gc.setForeground(GUIHelper.COLOR_GRAY);
                int viewportBorderX = viewportLayerLeft.getWidth() - 1;
                gc.drawLine(viewportBorderX, 0, viewportBorderX, layer.getHeight() - 1);
                gc.setForeground(beforeColor);
            }
        });

        return composite;
    }

    private void createSplitSliders(
            Composite natTableParent, final ViewportLayer left, final ViewportLayer right) {
        Composite sliderComposite = new Composite(natTableParent, SWT.NONE);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = false;
        gridData.heightHint = 17;
        sliderComposite.setLayoutData(gridData);

        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        sliderComposite.setLayout(gridLayout);

        // Slider Left
        // Need a composite here to set preferred size because Slider can't be
        // subclassed.
        Composite sliderLeftComposite = new Composite(sliderComposite, SWT.NONE) {
            @Override
            public Point computeSize(int wHint, int hHint, boolean changed) {
                int width = ((ClientAreaAdapter) left.getClientAreaProvider()).getWidth();
                return new Point(width, 17);
            }
        };
        sliderLeftComposite.setLayout(new FillLayout());
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.verticalAlignment = GridData.BEGINNING;
        sliderLeftComposite.setLayoutData(gridData);

        Slider sliderLeft = new Slider(sliderLeftComposite, SWT.HORIZONTAL);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        sliderLeft.setLayoutData(gridData);

        left.setHorizontalScroller(new SliderScroller(sliderLeft));

        // Slider Right
        Slider sliderRight = new Slider(sliderComposite, SWT.HORIZONTAL);
        gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.BEGINNING;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = false;
        sliderRight.setLayoutData(gridData);

        right.setHorizontalScroller(new SliderScroller(sliderRight));
    }
}
