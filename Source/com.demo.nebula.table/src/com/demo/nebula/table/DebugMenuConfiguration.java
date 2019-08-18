package com.demo.nebula.table;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;

public class DebugMenuConfiguration extends AbstractUiBindingConfiguration {

	private final Menu debugMenu;

	public DebugMenuConfiguration(NatTable natTable, MenuManager mgr) {
		// extend the declarative menu provided by the MenuManager
		this.debugMenu = new PopupMenuBuilder(natTable, mgr).withInspectLabelsMenuItem().build();
	}

	@Override
	public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
		uiBindingRegistry.registerMouseDownBinding(
				new MouseEventMatcher(SWT.NONE, null, MouseEventMatcher.RIGHT_BUTTON),
				new PopupMenuAction(this.debugMenu));
	}

}
