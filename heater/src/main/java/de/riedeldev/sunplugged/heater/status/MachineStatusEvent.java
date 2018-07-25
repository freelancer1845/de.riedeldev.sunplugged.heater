package de.riedeldev.sunplugged.heater.status;

import org.springframework.context.ApplicationEvent;

public class MachineStatusEvent extends ApplicationEvent {

	private MachineStatusSnapshot status;

	public MachineStatusEvent(Object source, MachineStatusSnapshot status) {
		super(source);
		this.status = status;

	}

	public MachineStatusSnapshot getStatus() {
		return status;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3104523502758570016L;

}
