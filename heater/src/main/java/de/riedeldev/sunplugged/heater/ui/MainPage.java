package de.riedeldev.sunplugged.heater.ui;

import com.vaadin.annotations.Push;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringUI
@Push
@SpringViewDisplay
public class MainPage extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = -970312717678890849L;
	private Label header;
	private VerticalLayout headerLayout;
	private VerticalLayout mainLayout;
	private VerticalLayout footerLayout;
	private VerticalLayout contentLayout;
	private MenuBar mainMenu;

	@Override
	protected void init(VaadinRequest request) {

		getPage().setTitle("Heater");
		SpringNavigator navigator = (SpringNavigator) getNavigator();

		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(false);
		mainLayout.setMargin(false);

		headerLayout = new VerticalLayout();
		mainLayout.addComponent(headerLayout);

		mainLayout.addComponent(Utils.createHorizontalSeperator());

		header = new Label("<h2>Heater Control</h2>", ContentMode.HTML);
		headerLayout.addComponent(header);

		mainMenu = new MenuBar();
		mainMenu.addStyleName(ValoTheme.MENUBAR_BORDERLESS);

		headerLayout.addComponent(mainMenu);

		mainMenu.addItem("Status View", VaadinIcons.LINE_CHART, item -> {
			navigator.navigateTo(ViewNames.STATUS_VIEW);
		});

		mainMenu.addItem("PID", VaadinIcons.DATABASE,
				item -> navigator.navigateTo(ViewNames.PID_VIEW));

		contentLayout = new VerticalLayout();
		contentLayout.setMargin(false);

		mainLayout.addComponent(Utils.createHorizontalSeperator());
		mainLayout.addComponent(contentLayout);

		mainLayout.addComponent(Utils.createHorizontalSeperator());

		footerLayout = new VerticalLayout();
		mainLayout.addComponent(footerLayout);

		// footerLayout.addComponent(logStateComponent);
		setContent(mainLayout);
		navigator.init(this, contentLayout);

	}

}
