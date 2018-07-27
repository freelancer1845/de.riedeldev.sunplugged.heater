package de.riedeldev.sunplugged.heater.ui;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.riedeldev.sunplugged.heater.io.IOServiceException;
import de.riedeldev.sunplugged.heater.pid.Heater;
import de.riedeldev.sunplugged.heater.pid.HeaterStatusEvent;

@SpringComponent
@UIScope
public class PIDComponent extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4930442724532003457L;

	private TextField targetTemperature = new TextField("Target Temperature");

	private TextField currentTemperature = new TextField("Current Temperature");

	private TextField power = new TextField("Current Power");

	private CheckBox controlling = new CheckBox("Controlling", false);

	private CheckBox onOff = new CheckBox("On/Off", false);

	private Heater heater;

	private ChartJs chart;

	@Autowired
	private UI ui;

	public PIDComponent() {

		currentTemperature.setReadOnly(true);
		targetTemperature.setValueChangeMode(ValueChangeMode.BLUR);
		targetTemperature.addValueChangeListener(e -> {
			try {
				heater.setTargetTemperature(Double.parseDouble(e.getValue()));
				targetTemperature.setComponentError(null);
			} catch (IllegalArgumentException e1) {
				UserError error = new UserError(e1.getMessage());
				targetTemperature.setComponentError(error);
			}

		});
		currentTemperature.setEnabled(false);
		targetTemperature.setEnabled(false);
		power.setEnabled(false);

		FormLayout form = new FormLayout(onOff, controlling, targetTemperature,
				currentTemperature, power);
		addComponent(form);

		onOff.addValueChangeListener(e -> {
			if (e.getValue() == true) {
				heater.on();
			} else {
				try {
					heater.off();

				} catch (IOServiceException e1) {
					notifyError("Failed to turn heater off.", e1);
				}
			}
		});

		controlling.addValueChangeListener(e -> {
			if (e.getValue() == true) {
				heater.activateControlling();
			} else {
				heater.deactivateControlling();
				currentTemperature.setEnabled(false);
				targetTemperature.setEnabled(false);
				power.setEnabled(false);

			}
		});
		setVisible(false);
	}

	public void setHeater(Heater heater) {
		if (heater == null) {
			this.heater = null;
			setVisible(false);
		} else {
			this.heater = heater;
			setVisible(true);
			updateValues();
		}
	}

	private void notifyError(String message, Throwable e) {
		Notification error = new Notification("Error",
				message + " Reason: " + e.getMessage(), Type.ERROR_MESSAGE);
		error.show(getUI().getPage());
	}

	@EventBusListenerMethod(scope = EventScope.APPLICATION)
	public void newHeaterStatus(HeaterStatusEvent event) {
		if (event.getSource() == heater) {
			ui.access(() -> {
				onOff.setValue(heater.isOn());
				controlling.setValue(heater.isControlling());

				if (heater.isControlling()) {
					currentTemperature.setEnabled(true);
					targetTemperature.setEnabled(true);
					power.setEnabled(true);
					currentTemperature.markAsDirty();
				} else {

				}
				updateValues();
				this.markAsDirtyRecursive();
			});
		}
	}

	private void updateValues() {
		try {
			currentTemperature
					.setValue(Double.toString(heater.getCurrentTemperature()));
		} catch (IOServiceException e3) {
			currentTemperature.setValue(null);
		}

		if (targetTemperature.getValue().equals(
				Double.toString(heater.getTargetTemperature())) == false) {
			targetTemperature
					.setValue(Double.toString(heater.getTargetTemperature()));

		}

		power.setValue(Double.toString(heater.getPower()));
	}

	@Autowired
	private EventBus.ApplicationEventBus eventBus;

	@PostConstruct
	protected void postConstrcut() {
		eventBus.subscribe(this);
	}
	@PreDestroy
	protected void preDestory() {
		eventBus.unsubscribe(this);
	}
}
