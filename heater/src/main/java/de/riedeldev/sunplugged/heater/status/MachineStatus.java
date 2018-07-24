package de.riedeldev.sunplugged.heater.status;

public interface MachineStatus {

	public boolean plcSaysAllFine();

	public boolean isOvertemperature();

	public boolean panelInterlockOne();

	public boolean panelInterlockTwo();

	public boolean panelInterlockThree();

	public boolean coverAlarmTop();

	public boolean coverAlarmBottom();

	public boolean panelInterlockAlarm();

	public boolean isHorn();

	public void setHorn(boolean on);

	public boolean isPlcStart();

	public void setPlcStart(boolean on);

	public boolean isPlcRun();

	public void setPlcRun(boolean on);

	public boolean[] getAlarmLights();

	public void setAlarmLights(boolean green, boolean orange, boolean red);
}
