package de.riedeldev.sunplugged.heater.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.heater.io.Addresses;
import de.riedeldev.sunplugged.heater.io.IOService;
import de.riedeldev.sunplugged.heater.io.IOServiceException;

@Service
public class MachineStatusImpl implements MachineStatus {

	private IOService ioService;

	@Autowired
	public MachineStatusImpl(IOService ioService) {
		this.ioService = ioService;
	}

	@Override
	public boolean plcSaysAllFine() throws IOServiceException {
		return ioService.getDI(Addresses.PLC_ALL_FINE);
	}

	@Override
	public boolean isOvertemperature() throws IOServiceException {
		return ioService.getDI(Addresses.OVERTEMPERATURE);
	}

	@Override
	public boolean panelInterlockOne() throws IOServiceException {
		return ioService.getDI(Addresses.PANEL_INTERLOCK_ONE);
	}

	@Override
	public boolean panelInterlockTwo() throws IOServiceException {
		return ioService.getDI(Addresses.PANEL_INTERLOCK_TWO);
	}

	@Override
	public boolean panelInterlockThree() throws IOServiceException {
		return ioService.getDI(Addresses.PANEL_INTERLOCK_THREE);
	}

	@Override
	public boolean coverAlarmTop() throws IOServiceException {
		return ioService.getDI(Addresses.COVER_ALARM_TOP);
	}

	@Override
	public boolean coverAlarmBottom() throws IOServiceException {
		return ioService.getDI(Addresses.COVER_ALARM_BOTTOM);
	}

	@Override
	public boolean panelInterlockAlarm() throws IOServiceException {
		return ioService.getDI(Addresses.PANEL_INTERLOCK_ALARM);
	}

	@Override
	public boolean isHorn() throws IOServiceException {
		return ioService.getDO(Addresses.HORN);
	}

	@Override
	public void setHorn(boolean on) throws IOServiceException {
		ioService.setDO(Addresses.HORN, on);;
	}

	@Override
	public boolean isPlcStart() throws IOServiceException {
		return ioService.getDO(Addresses.PLC_START);
	}

	@Override
	public void setPlcStart(boolean on) throws IOServiceException {
		ioService.setDO(Addresses.PLC_START, on);
	}

	@Override
	public boolean isPlcRun() throws IOServiceException {
		return ioService.getDO(Addresses.PLC_RUN);
	}

	@Override
	public void setPlcRun(boolean on) throws IOServiceException {
		ioService.setDO(Addresses.PLC_RUN, on);
	}

	@Override
	public boolean[] getAlarmLights() throws IOServiceException {
		return new boolean[]{ioService.getDO(Addresses.GREEN_LIGHT),
				ioService.getDO(Addresses.ORANGE_LIGHT),
				ioService.getDO(Addresses.RED_LIGHT)};
	}

	@Override
	public void setAlarmLights(boolean green, boolean orange, boolean red)
			throws IOServiceException {
		ioService.setDO(Addresses.GREEN_LIGHT, green);
		ioService.setDO(Addresses.ORANGE_LIGHT, orange);
		ioService.setDO(Addresses.RED_LIGHT, red);
	}

}
