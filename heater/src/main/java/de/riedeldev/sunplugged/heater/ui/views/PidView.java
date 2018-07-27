package de.riedeldev.sunplugged.heater.ui.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.VerticalLayout;

import de.riedeldev.sunplugged.heater.pid.Heater;
import de.riedeldev.sunplugged.heater.ui.PIDComponent;
import de.riedeldev.sunplugged.heater.ui.ViewNames;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringView(name = ViewNames.PID_VIEW)
@UIScope
public class PidView extends VerticalLayout implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4423719416277671758L;

	@Autowired
	@Qualifier("preHeaterOne")
	private Heater heater;

	private PIDComponent pidComponent;

	@Autowired
	public PidView(PIDComponent pidComponent) {
		addComponent(pidComponent);

		this.pidComponent = pidComponent;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		pidComponent.setHeater(heater);
		View.super.enter(event);
	}

}
