package de.riedeldev.sunplugged.heater.config;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class ParameterChangeEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4430212501135690477L;

	public ParameterChangeEvent(Object source, Parameters parameters) {
		super(source);
		this.newParameters = parameters;
	}

	private final Parameters newParameters;

}
