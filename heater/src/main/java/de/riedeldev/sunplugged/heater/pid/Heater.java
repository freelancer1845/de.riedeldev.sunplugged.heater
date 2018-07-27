package de.riedeldev.sunplugged.heater.pid;

import de.riedeldev.sunplugged.heater.io.IOServiceException;

public interface Heater {

	public void on();

	public void off() throws IOServiceException;

	public boolean isOn();

	public boolean isControlling();

	public void activateControlling();

	public void deactivateControlling();

	public void setTargetTemperature(double target);

	public double getTargetTemperature();

	public double getCurrentTemperature() throws IOServiceException;

	public void forcePower(double power) throws IOServiceException;

	public double getPower();

}
