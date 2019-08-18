package com.demo.nebula.table;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.dataset.fixture.data.PricingTypeBean;
import org.eclipse.nebula.widgets.nattable.dataset.fixture.data.RowDataFixture;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.examples.fixtures.PricingTypeBeanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.DefaultGlazedListsFilterStrategy;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowHeaderComposite;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;

public class View extends ViewPart {
	public static final String ID = "com.demo.nebula.table.view";
	private String propertyNames[] = { "testCaseName", "testCaseTypes", "partTestSet", "testCaseDescription" };

	private Text outputArea;
	private ModelService modelService;

	public View() {
		modelService = new ModelService();
	}

	private class StringLabelProvider extends ColumnLabelProvider {
		@Override
		public String getText(Object element) {
			return super.getText(element);
		}

		@Override
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}

	}

	@Override
	public void createPartControl(Composite parent) {

		setModelObject();
		Map propertyToLabelMap = new HashMap<>();
		propertyToLabelMap.put("testCaseName", "Test Case Name");
		propertyToLabelMap.put("testCaseTypes", "Test case types");
		propertyToLabelMap.put("partTestSet", "Part of test set");
		propertyToLabelMap.put("testCaseDescription", "Test case description");


		MenuManager mgr = new MenuManager();
		getSite().registerContextMenu("myMenu", mgr, null);

		 // Underlying data source
		List<TestModel> data = modelService.getModel(10);
		EventList<TestModel> eventList = GlazedLists.eventList(data);
		FilterList<TestModel> filterList = new FilterList<>(eventList);

		// Body layer
		IColumnPropertyAccessor columnPropertyAccessor = new ReflectiveColumnPropertyAccessor<>(propertyNames);
		final IDataProvider bodyDataProvider = new ListDataProvider<>(filterList, columnPropertyAccessor);
		final DataLayer bodyDataLayer = new DataLayer(bodyDataProvider, 45, 20);
		GlazedListsEventLayer<TestModel> eventLayer = new GlazedListsEventLayer<>(bodyDataLayer, filterList);
		SelectionLayer selectionLayer = new SelectionLayer(eventLayer);
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		// create the column header layer stack
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(propertyNames, propertyToLabelMap);
		bodyDataLayer.setDefaultColumnWidthByPosition(0, 200);
		bodyDataLayer.setDefaultColumnWidthByPosition(1, 220);
		bodyDataLayer.setDefaultColumnWidthByPosition(2, 200);
		bodyDataLayer.setDefaultColumnWidthByPosition(3, 320);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(new DataLayer(columnHeaderDataProvider), viewportLayer,
				selectionLayer);

		// create the row header layer stack
		IDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyDataProvider);
		ILayer rowHeaderLayer = new RowHeaderLayer(
				new DefaultRowHeaderDataLayer(new DefaultRowHeaderDataProvider(bodyDataProvider)), viewportLayer,
				selectionLayer);

		// create the corner layer stack
		ConfigRegistry configRegistry = new ConfigRegistry();

		// Note: The column header layer is wrapped in a filter row composite.
		@SuppressWarnings("unchecked")
		FilterRowHeaderComposite<RowDataFixture> filterRowHeaderLayer = new FilterRowHeaderComposite<>(
				new DefaultGlazedListsFilterStrategy<>(filterList, columnPropertyAccessor, configRegistry),
				columnHeaderLayer, columnHeaderDataProvider, configRegistry);

		// Corner layer
		DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderDataProvider,
				rowHeaderDataProvider);
		DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		CornerLayer cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, filterRowHeaderLayer);
		
		// Grid Layer
		GridLayer gridLayer = new GridLayer(viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);
		gridLayer.setColumnHeaderLayer(filterRowHeaderLayer);
		
		//NatTable configuration and creation
		NatTable natTable = new NatTable(parent, gridLayer, false);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new HeaderMenuConfiguration(natTable));
		natTable.addConfiguration(new DebugMenuConfiguration(natTable, mgr));
		natTable.setData("org.eclipse.e4.ui.css.CssClassName", "modern");
		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);
		outputArea = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		outputArea.setEditable(false);
		parent.setLayout(new GridLayout());
		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);
		outputArea = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		outputArea.setEditable(false);
		GridDataFactory.fillDefaults().grab(true, false).hint(0, 0).align(SWT.FILL, SWT.BEGINNING).applyTo(outputArea);
		
		//set Filter 
		natTable.addConfiguration(new FilterRowCustomConfiguration() {
			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				super.configureRegistry(configRegistry);

				// Shade the row to be slightly darker than the blue background.
				final Style rowStyle = new Style();
				rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, DisplayMode.NORMAL,
						GridRegion.FILTER_ROW);
			}
		});
		
		//Set mouse listener
		natTable.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// get the row position for the click in the NatTable
				int rowPos = natTable.getRowPositionByY(e.y);
				int bodyRowPos = LayerUtil.convertRowPosition(natTable, rowPos,
						(IUniqueIndexLayer) gridLayer.getBodyLayer());
				Object ccd = ((ListDataProvider) bodyDataProvider).getRowObject(bodyRowPos);
				if (ccd instanceof TestSet) {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IFile ifile = modelService.getIFileFromSelection((TestSet) ccd);
					try {
						IDE.openEditor(page, ifile, true);
					} catch (PartInitException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		natTable.setConfigRegistry(configRegistry);
		natTable.configure();
		/******************************************/

	}

	private void setModelObject() {
		IProject[] iprojects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject iProject : iprojects) {

			IFolder folderFiles = iProject.getFolder("TestFile");
			try {
				IResource[] resources = folderFiles.members();
				for (IResource iResource : resources) {
					if (iResource instanceof IFile) {
						IFile file = (IFile) iResource;
						Map<Integer, String> contents = getFileContent(file);
						modelService.setTestFileModel(contents, file);
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	private Map<Integer, String> getFileContent(IFile file) {
		Map<Integer, String> content = new HashMap<>();
		String fileEnding = FileUtil.getLineSeparator(file);
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(file.getContents(true), file.getCharset()));) {
			String line = reader.readLine();
			int lastLineNumber = 1;
			while (line != null) {
				content.put(lastLineNumber, line + fileEnding);
				line = reader.readLine();
				lastLineNumber++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	@Override
	public void setFocus() {
		// viewer.getControl().setFocus();
	}

	static class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {

		final DefaultDoubleDisplayConverter doubleDisplayConverter = new DefaultDoubleDisplayConverter();

		@Override
		public void configureRegistry(IConfigRegistry configRegistry) {
			// override the default filter row configuration for painter
			configRegistry.registerConfigAttribute(CELL_PAINTER,
					new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);

			// Configure custom comparator on the rating column
			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR,
					getIngnorecaseComparator(), DisplayMode.NORMAL,
					FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);

			// If threshold comparison is used we have to convert the string
			// entered by the user to the correct underlying type (double), so
			// that it can be compared

			// Configure Bid column
			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
					this.doubleDisplayConverter, DisplayMode.NORMAL,
					FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 5);
			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
					TextMatchingMode.REGULAR_EXPRESSION, DisplayMode.NORMAL,
					FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 5);

			// Configure Ask column
			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
					this.doubleDisplayConverter, DisplayMode.NORMAL,
					FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 6);
			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
					TextMatchingMode.REGULAR_EXPRESSION, DisplayMode.NORMAL,
					FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 6);

			// Configure a combo box on the pricing type column

			// Register a combo box editor to be displayed in the filter row
			// cell when a value is selected from the combo, the object is
			// converted to a string using the converter (registered below)
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR,
					new ComboBoxCellEditor(Arrays.asList(new PricingTypeBean("MN"), new PricingTypeBean("AT"))),
					DisplayMode.NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 4);

			// The pricing bean object in column is converted to using this
			// display converter
			// A 'text' match is then performed against the value from the combo
			// box
			configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER,
					new PricingTypeBeanDisplayConverter(), DisplayMode.NORMAL,
					FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 4);

			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
					new PricingTypeBeanDisplayConverter(), DisplayMode.NORMAL,
					FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 4);

			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
					new PricingTypeBeanDisplayConverter(), DisplayMode.NORMAL, "PRICING_TYPE_PROP_NAME");
		}

		private Comparator getIngnorecaseComparator() {
			return new Comparator() {

				@Override
				public int compare(Object o1, Object o2) {
					return 0;
				}

			};
		}
	}
}