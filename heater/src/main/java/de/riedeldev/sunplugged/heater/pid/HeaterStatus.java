package de.riedeldev.sunplugged.heater.pid;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HeaterStatus {

	private final boolean on;

	private final boolean controlling;

	private final double power;

	private final double targetTemperature;

	private final double currentTemperature;

	private final double p;

	private final double i;

	private final double d;

}
