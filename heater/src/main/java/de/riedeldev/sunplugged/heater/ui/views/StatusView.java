package de.riedeldev.sunplugged.heater.ui.views;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;

import de.riedeldev.sunplugged.heater.ui.ViewNames;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringView(name = ViewNames.STATUS_VIEW)
@UIScope
public class StatusView extends VerticalLayout implements View {
	/**
	* 
	*/
	private static final long serialVersionUID = -2736446328236880529L;

}
