package de.riedeldev.sunplugged.heater.io;

public interface IOService {

	public void setDO(int address, boolean value) throws IOServiceException;

	public boolean getDO(int address) throws IOServiceException;

	public boolean getDI(int address) throws IOServiceException;

	public void setAO(int address, int value) throws IOServiceException;

	public int getAO(int address) throws IOServiceException;

	public int getAI(int address) throws IOServiceException;

}
