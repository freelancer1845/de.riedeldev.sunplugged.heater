package de.riedeldev.sunplugged.heater.status;

import de.riedeldev.sunplugged.heater.io.IOServiceException;

public interface MachineStatus {

	public boolean plcSaysAllFine() throws IOServiceException;

	public boolean isOvertemperature() throws IOServiceException;

	public boolean panelInterlockOne() throws IOServiceException;

	public boolean panelInterlockTwo() throws IOServiceException;

	public boolean panelInterlockThree() throws IOServiceException;

	public boolean coverAlarmTop() throws IOServiceException;

	public boolean coverAlarmBottom() throws IOServiceException;

	public boolean panelInterlockAlarm() throws IOServiceException;

	public boolean isHorn() throws IOServiceException;

	public void setHorn(boolean on) throws IOServiceException;

	public boolean isPlcStart() throws IOServiceException;

	public void setPlcStart(boolean on) throws IOServiceException;

	public boolean isPlcRun() throws IOServiceException;

	public void setPlcRun(boolean on) throws IOServiceException;

	public boolean[] getAlarmLights() throws IOServiceException;

	public void setAlarmLights(boolean green, boolean orange, boolean red)
			throws IOServiceException;
}
