package de.riedeldev.sunplugged.heater.ui.views;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.riedeldev.sunplugged.heater.io.IOServiceException;
import de.riedeldev.sunplugged.heater.status.MachineStatus;
import de.riedeldev.sunplugged.heater.status.MachineStatusBiConsumer;
import de.riedeldev.sunplugged.heater.status.MachineStatusConsumer;
import de.riedeldev.sunplugged.heater.status.MachineStatusEvent;
import de.riedeldev.sunplugged.heater.status.MachineStatusSnapshot;
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

	@Autowired
	EventBus.ApplicationEventBus bus;

	@Autowired
	private UI ui;

	private List<Consumer<MachineStatusSnapshot>> labels = new LinkedList<>();

	@Autowired
	public StatusView(MachineStatus machineStatus) {

		HorizontalLayout digitalGroup = new HorizontalLayout();
		digitalGroup.setCaption("<h2>Digital I/O</h2>");
		digitalGroup.setCaptionAsHtml(true);

		VerticalLayout digitalInputs = new VerticalLayout();
		digitalGroup.addComponent(digitalInputs);
		digitalInputs.setCaption("<h3>Digital Inputs</h3>");
		digitalInputs.setCaptionAsHtml(true);
		digitalInputs.setMargin(false);

		digitalInputs.addComponent(new BooleanStatusLabel("PLC All fine",
				s -> s.plcSaysAllFine()));
		digitalInputs.addComponent(new BooleanStatusLabel("Over Temperature",
				s -> s.isOvertemperature() == false));
		digitalInputs.addComponent(new BooleanStatusLabel("Panel Interlock Two",
				s -> s.panelInterlockTwo() == false));
		digitalInputs.addComponent(new BooleanStatusLabel("Panel Interlock Two",
				s -> s.panelInterlockTwo() == false));
		digitalInputs
				.addComponent(new BooleanStatusLabel("Panel Interlock Three",
						s -> s.panelInterlockThree() == false));
		digitalInputs.addComponent(new BooleanStatusLabel("Cover Alarm Top",
				s -> s.coverAlarmTop() == false));
		digitalInputs.addComponent(new BooleanStatusLabel("Cover Alarm Bottom",
				s -> s.coverAlarmBottom() == false));
		digitalInputs
				.addComponent(new BooleanStatusLabel("Panel Interlock Alarm",
						s -> s.panelInterlockAlarm() == false));

		VerticalLayout digitalCoils = new VerticalLayout();
		digitalCoils.setCaption("<h3>Digital Outputs</h3>");
		digitalCoils.setCaptionAsHtml(true);
		digitalCoils.setMargin(false);
		digitalGroup.addComponent(digitalCoils);

		digitalCoils.addComponent(new ToggableBooleanStatusLabel("PLC Start",
				status -> status.isPlcStart(), machineStatus,
				service -> service.setPlcStart(true),
				service -> service.setPlcStart(false)));
		digitalCoils.addComponent(new ToggableBooleanStatusLabel("PLC Run",
				status -> status.isPlcRun(), machineStatus,
				s -> s.setPlcRun(true), s -> s.setPlcRun(false)));
		digitalCoils.addComponent(new ToggableBooleanStatusLabel("Horn",
				s -> s.isHorn(), machineStatus, s -> s.setHorn(true),
				s -> s.setHorn(false)));

		digitalCoils.addComponent(new ToggableBooleanStatusLabel("Green Light",
				s -> s.getAlarmLights()[0], machineStatus,
				s -> s.setAlarmLights(true, s.getAlarmLights()[1],
						s.getAlarmLights()[2]),
				s -> s.setAlarmLights(false, s.getAlarmLights()[1],
						s.getAlarmLights()[2])));
		digitalCoils.addComponent(new ToggableBooleanStatusLabel("Orange Light",
				s -> s.getAlarmLights()[1], machineStatus,
				s -> s.setAlarmLights(s.getAlarmLights()[0], true,
						s.getAlarmLights()[2]),
				s -> s.setAlarmLights(s.getAlarmLights()[0], false,
						s.getAlarmLights()[2])));
		digitalCoils.addComponent(new ToggableBooleanStatusLabel("Red Light",
				s -> s.getAlarmLights()[2], machineStatus,
				s -> s.setAlarmLights(s.getAlarmLights()[0],
						s.getAlarmLights()[1], true),
				s -> s.setAlarmLights(s.getAlarmLights()[0],
						s.getAlarmLights()[1], false)));

		VerticalLayout analogInputs = new VerticalLayout();
		analogInputs.setCaption("<h3>Analog Inputs</h3>");
		analogInputs.setCaptionAsHtml(true);
		analogInputs.setMargin(false);
		digitalGroup.addComponent(analogInputs);

		analogInputs.addComponent(new StatusTextField("Heater One",
				s -> String.format("%.2f C", s.getHeaterOneTemperature())));
		analogInputs.addComponent(new StatusTextField("Heater Two",
				s -> String.format("%.2f C", s.getHeaterTwoTemperature())));

		analogInputs.addComponent(new StatusTextField("Zone One",
				s -> String.format("%.2f C", s.getZoneOneTemperature())));
		analogInputs.addComponent(new StatusTextField("Zone Two",
				s -> String.format("%.2f C", s.getZoneTwoTemperature())));
		analogInputs.addComponent(new StatusTextField("Zone Three",
				s -> String.format("%.2f C", s.getZoneThreeTemperature())));

		VerticalLayout analogOutputs = new VerticalLayout();
		analogOutputs.setCaption("<h3>Analog Outputs</h3>");
		analogOutputs.setCaptionAsHtml(true);
		analogOutputs.setMargin(false);
		analogOutputs.addComponent(new ChangeableStatusTextField(
				"Pre Heater One",
				s -> String.format("%.2f %%", s.getPreHeaterOnePower() * 100),
				(s, v) -> s.setPreHeaterOnePower(v), machineStatus));
		analogOutputs.addComponent(new ChangeableStatusTextField(
				"Heater Fan One",
				s -> String.format("%.2f %%", s.getHeaterFanOnePower() * 100),
				(s, v) -> s.setHeaterFanOnePower(v), machineStatus));
		analogOutputs.addComponent(new ChangeableStatusTextField(
				"Pre Heater Two",
				s -> String.format("%.2f %%", s.getPreHeaterTwoPower() * 100),
				(s, v) -> s.setPreHeaterTwoPower(v), machineStatus));
		analogOutputs.addComponent(new ChangeableStatusTextField(
				"Heater Fan Two",
				s -> String.format("%.2f %%", s.getHeaterFanTwoPower() * 100),
				(s, v) -> s.setHeaterFanTwoPower(v), machineStatus));

		digitalGroup.addComponent(analogOutputs);

		this.addComponent(digitalGroup);
	}

	@PostConstruct
	protected void postConstrcut() {
		bus.subscribe(this);
	}
	@PreDestroy
	protected void preDestory() {
		bus.unsubscribe(this);
	}

	@EventBusListenerMethod(scope = EventScope.APPLICATION)
	public void newStats(MachineStatusEvent event) {
		MachineStatusSnapshot status = event.getStatus();
		ui.access(() -> {
			labels.forEach(consumer -> consumer.accept(status));
		});
	}

	private class ToggableBooleanStatusLabel extends HorizontalLayout
			implements
				Consumer<MachineStatusSnapshot> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6758142069249120436L;
		private BooleanStatusLabel booleanStatusLabel;
		private MachineStatus statusService;

		public ToggableBooleanStatusLabel(String name,
				Function<MachineStatusSnapshot, Boolean> statusGetter,
				MachineStatus statusService, MachineStatusConsumer setOn,
				MachineStatusConsumer setOff) {
			booleanStatusLabel = new BooleanStatusLabel(name, statusGetter);
			addComponent(booleanStatusLabel);
			this.statusService = statusService;

			Button buttonOn = new Button("On");
			buttonOn.addStyleName(ValoTheme.BUTTON_PRIMARY);
			buttonOn.addClickListener(click -> {
				try {
					setOn.accept(this.statusService);
				} catch (IOServiceException e) {
					Notification notification = new Notification("IO Error",
							e.getMessage(), Type.ERROR_MESSAGE);
					notification.show(ui.getPage());
				}
			});
			addComponent(buttonOn);

			Button buttonOff = new Button("Off");
			buttonOff.addStyleName(ValoTheme.BUTTON_DANGER);
			buttonOff.addClickListener(click -> {
				try {
					setOff.accept(this.statusService);
				} catch (IOServiceException e) {
					Notification notification = new Notification("IO Error",
							e.getMessage(), Type.ERROR_MESSAGE);
					notification.show(ui.getPage());
				}
			});
			addComponent(buttonOff);

		}

		@Override
		public void accept(MachineStatusSnapshot t) {
			booleanStatusLabel.accept(t);
		}

	}

	private class BooleanStatusLabel extends Label
			implements
				Consumer<MachineStatusSnapshot> {

		private final Function<MachineStatusSnapshot, Boolean> statusGetter;

		public BooleanStatusLabel(String name,
				Function<MachineStatusSnapshot, Boolean> statusGetter) {
			super(name);
			this.statusGetter = statusGetter;
			labels.add(this);
		}

		@Override
		public void accept(MachineStatusSnapshot status) {
			if (statusGetter.apply(status) == false) {
				addStyleName(ValoTheme.LABEL_FAILURE);
				removeStyleName(ValoTheme.LABEL_SUCCESS);
			} else {
				addStyleName(ValoTheme.LABEL_SUCCESS);
				removeStyleName(ValoTheme.LABEL_FAILURE);
			}

		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 4434632562141384841L;

	}

	private class StatusTextField extends TextField
			implements
				Consumer<MachineStatusSnapshot> {

		private Function<MachineStatusSnapshot, String> statusGetter;

		public StatusTextField(String name,
				Function<MachineStatusSnapshot, String> statusGetter) {
			super(name);
			setReadOnly(true);
			this.statusGetter = statusGetter;
			labels.add(this);

		}

		@Override
		public void accept(MachineStatusSnapshot t) {
			setValue(statusGetter.apply(t));
		}
	}

	private class ChangeableStatusTextField extends HorizontalLayout {
		private StatusTextField statusField;

		private boolean isSliding = false;

		public ChangeableStatusTextField(String name,
				Function<MachineStatusSnapshot, String> statusGetter,
				MachineStatusBiConsumer consumer, MachineStatus service) {
			statusField = new StatusTextField(name, statusGetter);
			addComponent(statusField);

			Slider slider = new Slider(0, 100, 1);
			slider.addValueChangeListener(value -> {
				try {
					consumer.accept(service, value.getValue() / 100.0);
				} catch (IOServiceException e) {
					Notification notification = new Notification("IO Error",
							e.getMessage(), Type.ERROR_MESSAGE);
					notification.show(ui.getPage());
				}

			});

			addComponent(slider);
		}

	}

}
