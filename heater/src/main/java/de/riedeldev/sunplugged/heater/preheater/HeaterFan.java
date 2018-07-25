package de.riedeldev.sunplugged.heater.preheater;

import de.riedeldev.sunplugged.heater.io.IOServiceException;

public interface HeaterFan {

	public double getPower() throws IOServiceException;

	public void setPower(double newPower) throws IOServiceException;

}
