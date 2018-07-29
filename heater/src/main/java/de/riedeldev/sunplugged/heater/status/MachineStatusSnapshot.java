package de.riedeldev.sunplugged.heater.status;

import java.time.LocalDateTime;

import de.riedeldev.sunplugged.heater.io.IOServiceException;

public class MachineStatusSnapshot implements MachineStatus {

	private final LocalDateTime timestamp;

	private final boolean plcSaysAllFine;

	private final boolean isOvertemperature;

	private final boolean panelInterlockOne;

	private final boolean panelInterlockTwo;

	private final boolean panelInterlockThree;

	private final boolean coverAlarmTop;

	private final boolean coverAlarmBottom;

	private final boolean panelInterlockAlarm;

	private final boolean isHorn;

	private final boolean isPlcStart;

	private final boolean isPlcRun;

	private final boolean[] alarmLights;

	private final double zoneOne;

	private final double zoneTwo;

	private final double zoneThree;

	private final double preHeaterOneTemperature;

	private final double preHeaterTwoTemperature;

	private final double heaterFanOnePower;

	private final double heaterFanTwoPower;

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	private MachineStatusSnapshot(MachineStatus status)
			throws IOServiceException {
		plcSaysAllFine = status.plcSaysAllFine();
		isOvertemperature = status.isOvertemperature();
		panelInterlockOne = status.panelInterlockOne();
		panelInterlockTwo = status.panelInterlockTwo();
		panelInterlockThree = status.panelInterlockThree();
		coverAlarmTop = status.coverAlarmTop();
		coverAlarmBottom = status.coverAlarmBottom();
		panelInterlockAlarm = status.panelInterlockAlarm();
		isHorn = status.isHorn();
		isPlcRun = status.isPlcRun();
		isPlcStart = status.isPlcStart();
		alarmLights = status.getAlarmLights();
		zoneOne = status.getZoneOneTemperature();
		zoneTwo = status.getZoneTwoTemperature();
		zoneThree = status.getZoneThreeTemperature();
		preHeaterOneTemperature = status.getPreHeaterOneTemperature();
		preHeaterTwoTemperature = status.getPreHeaterTwoTemperature();
		heaterFanOnePower = status.getHeaterFanOnePower();
		heaterFanTwoPower = status.getHeaterFanTwoPower();
		timestamp = LocalDateTime.now();
	}

	public static MachineStatusSnapshot create(MachineStatus status)
			throws IOServiceException {
		MachineStatusSnapshot snapshot = new MachineStatusSnapshot(status);
		return snapshot;
	}

	public void update(MachineStatus status) {

	}

	@Override
	public boolean plcSaysAllFine() {
		return plcSaysAllFine;
	}

	@Override
	public boolean isOvertemperature() {
		return isOvertemperature;
	}

	@Override
	public boolean panelInterlockOne() {
		return panelInterlockOne;
	}

	@Override
	public boolean panelInterlockTwo() {
		return panelInterlockTwo;
	}

	@Override
	public boolean panelInterlockThree() {
		return panelInterlockThree;
	}

	@Override
	public boolean coverAlarmTop() {
		return coverAlarmTop;
	}

	@Override
	public boolean coverAlarmBottom() {
		return coverAlarmBottom;
	}

	@Override
	public boolean panelInterlockAlarm() {
		return panelInterlockAlarm;
	}

	@Override
	public boolean isHorn() {
		return isHorn;
	}

	@Override
	public void setHorn(boolean on) {
		throw new UnsupportedOperationException("This is only a snapshot!");
	}

	@Override
	public boolean isPlcStart() {
		return isPlcStart;
	}

	@Override
	public void setPlcStart(boolean on) {
		throw new UnsupportedOperationException("This is only a snapshot!");
	}

	@Override
	public boolean isPlcRun() {
		return isPlcRun;
	}

	@Override
	public void setPlcRun(boolean on) {
		throw new UnsupportedOperationException("This is only a snapshot!");
	}

	@Override
	public boolean[] getAlarmLights() {
		return alarmLights;
	}

	@Override
	public void setAlarmLights(boolean green, boolean orange, boolean red) {
		throw new UnsupportedOperationException("This is only a snapshot!");
	}

	@Override
	public double getZoneThreeTemperature() {
		return zoneThree;
	}

	@Override
	public double getZoneTwoTemperature() {
		return zoneTwo;
	}

	@Override
	public double getZoneOneTemperature() {
		return zoneOne;
	}

	@Override
	public double getHeaterFanOnePower() {
		return heaterFanOnePower;
	}

	@Override
	public void setHeaterFanOnePower(double power) throws IOServiceException {
		throw new UnsupportedOperationException("This is only a snapshot!");
	}

	@Override
	public double getHeaterFanTwoPower() {
		return heaterFanTwoPower;
	}

	@Override
	public void setHeaterFanTwoPower(double power) throws IOServiceException {
		throw new UnsupportedOperationException("This is only a snapshot!");
	}

	@Override
	public double getPreHeaterOneTemperature() {
		return preHeaterOneTemperature;
	}

	@Override
	public double getPreHeaterTwoTemperature() {
		return preHeaterTwoTemperature;
	}

}
