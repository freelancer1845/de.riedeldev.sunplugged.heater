package de.riedeldev.sunplugged.heater.io;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class IOServiceEvent extends ApplicationEvent {

	public IOServiceEvent(Object source, Type type, Object payload) {
		super(source);
		this.type = type;
		this.payload = payload;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4066407953016478620L;

	public enum Type {
		CONNECTING, CONNECTED, DISCONNECTED, ERROR
	}

	private final Type type;

	private final Object payload;

}
