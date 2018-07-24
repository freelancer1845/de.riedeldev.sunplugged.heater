package de.riedeldev.sunplugged.heater.pid;

public interface Heater {

	public void on();

	public void off();

	public boolean isOn();

	public boolean isControlling();

	public void activateControlling();

	public void deactivateControlling();

	public void setTargetTemperature(double target);

	public double getTargetTemperature();

	public double getCurrentTemperature();

	public void forcePower(double power);

}
