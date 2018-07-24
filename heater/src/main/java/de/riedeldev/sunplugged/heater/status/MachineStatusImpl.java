package de.riedeldev.sunplugged.heater.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.heater.io.Addresses;
import de.riedeldev.sunplugged.heater.io.IOService;

@Service
public class MachineStatusImpl implements MachineStatus {

	private IOService ioService;

	@Autowired
	public MachineStatusImpl(IOService ioService) {
		this.ioService = ioService;
	}

	@Override
	public boolean plcSaysAllFine() {
		return ioService.getDI(Addresses.PLC_ALL_FINE);
	}

	@Override
	public boolean isOvertemperature() {
		return ioService.getDI(Addresses.OVERTEMPERATURE);
	}

	@Override
	public boolean panelInterlockOne() {
		return ioService.getDI(Addresses.PANEL_INTERLOCK_ONE);
	}

	@Override
	public boolean panelInterlockTwo() {
		return ioService.getDI(Addresses.PANEL_INTERLOCK_TWO);
	}

	@Override
	public boolean panelInterlockThree() {
		return ioService.getDI(Addresses.PANEL_INTERLOCK_THREE);
	}

	@Override
	public boolean coverAlarmTop() {
		return ioService.getDI(Addresses.COVER_ALARM_TOP);
	}

	@Override
	public boolean coverAlarmBottom() {
		return ioService.getDI(Addresses.COVER_ALARM_BOTTOM);
	}

	@Override
	public boolean panelInterlockAlarm() {
		return ioService.getDI(Addresses.PANEL_INTERLOCK_ALARM);
	}

	@Override
	public boolean isHorn() {
		return ioService.getDO(Addresses.HORN);
	}

	@Override
	public void setHorn(boolean on) {
		ioService.setDO(Addresses.HORN, on);;
	}

	@Override
	public boolean isPlcStart() {
		return ioService.getDO(Addresses.PLC_START);
	}

	@Override
	public void setPlcStart(boolean on) {
		ioService.setDO(Addresses.PLC_START, on);
	}

	@Override
	public boolean isPlcRun() {
		return ioService.getDO(Addresses.PLC_RUN);
	}

	@Override
	public void setPlcRun(boolean on) {
		ioService.setDO(Addresses.PLC_RUN, on);
	}

	@Override
	public boolean[] getAlarmLights() {
		return new boolean[]{ioService.getDO(Addresses.GREEN_LIGHT),
				ioService.getDO(Addresses.ORANGE_LIGHT),
				ioService.getDO(Addresses.RED_LIGHT)};
	}

	@Override
	public void setAlarmLights(boolean green, boolean orange, boolean red) {
		ioService.setDO(Addresses.GREEN_LIGHT, green);
		ioService.setDO(Addresses.ORANGE_LIGHT, orange);
		ioService.setDO(Addresses.RED_LIGHT, red);
	}

}
