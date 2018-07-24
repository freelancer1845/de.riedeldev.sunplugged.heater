package de.riedeldev.sunplugged.heater.io;

public interface IOService {

	public void setDO(int address, boolean value);

	public boolean getDO(int address);

	public boolean getDI(int address);

	public void setAO(int address, int value);

	public int getAO(int address);

	public int getAI(int address);

}
