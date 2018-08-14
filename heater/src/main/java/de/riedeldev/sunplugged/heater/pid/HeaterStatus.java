package de.riedeldev.sunplugged.heater.pid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.riedeldev.sunplugged.heater.io.IOServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonIgnoreProperties
public class HeaterStatus {

	private final Boolean on;

	private final Boolean controlling;

	private final Double power;

	private final Double targetTemperature;

	private final Double currentTemperature;

	private final Double p;

	private final Double i;

	private final Double d;

	public static HeaterStatus buildFromHeater(ConfigurableHeater heater)
			throws IOServiceException {
		HeaterStatus status = new HeaterStatus(heater.isOn(),
				heater.isControlling(), heater.getPower(),
				heater.getTargetTemperature(), heater.getCurrentTemperature(),
				heater.getPIDValues()[0], heater.getPIDValues()[1],
				heater.getPIDValues()[2]);
		return status;
	}

}
