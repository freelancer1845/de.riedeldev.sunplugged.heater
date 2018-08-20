package de.riedeldev.sunplugged.heater.io;

public interface IOService {

	public void setDO(int address, boolean value) throws IOServiceException;

	public boolean getDO(int address) throws IOServiceException;

	public boolean getDI(int address) throws IOServiceException;

	public void setAO(int address, double value) throws IOServiceException;

	public double getAO(int address) throws IOServiceException;

	public double getAI(int address) throws IOServiceException;

}
