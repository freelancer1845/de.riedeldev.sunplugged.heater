package de.riedeldev.sunplugged.heater.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.heater.io.Addresses;
import de.riedeldev.sunplugged.heater.io.Conversions;
import de.riedeldev.sunplugged.heater.io.IOService;
import de.riedeldev.sunplugged.heater.io.IOServiceException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MachineStatusImpl implements MachineStatus {

	@Value("${publish.status}")
	private boolean publishStatus = false;

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

	@Autowired
	private ApplicationEventPublisher publisher;

	@Scheduled(fixedRateString = "${publish.status.interval}", initialDelay = 5000)
	public void publishStatus() {
		if (publishStatus == true) {
			try {
				publisher.publishEvent(new MachineStatusEvent(this,
						MachineStatusSnapshot.create(this)));
			} catch (IOServiceException e) {
				log.error("Failed to publish machinestatus", e);
			}
		}
	}

	@Override
	public double getZoneOneTemperature() throws IOServiceException {
		return Conversions
				.typeKConversion(ioService.getAI(Addresses.MAIN_HEATER_ONE_AI));
	}

	@Override
	public double getZoneTwoTemperature() throws IOServiceException {
		return Conversions
				.typeKConversion(ioService.getAI(Addresses.MAIN_HEATER_TWO_AI));
	}

	@Override
	public double getZoneThreeTemperature() throws IOServiceException {
		return Conversions.typeKConversion(
				ioService.getAI(Addresses.MAIN_HEATER_THREE_AI));
	}

	@Override
	public double getHeaterOneTemperature() throws IOServiceException {
		return Conversions
				.typeKConversion(ioService.getAI(Addresses.PRE_HEATER_ONE_AI));
	}

	@Override
	public double getHeaterTwoTemperature() throws IOServiceException {
		return Conversions
				.typeKConversion(ioService.getAI(Addresses.PRE_HEATER_TWO_AI));
	}

	@Override
	public double getPreHeaterOnePower() throws IOServiceException {
		return Conversions.unsingedIntToUnsingedVoltage(
				ioService.getAO(Addresses.PRE_HEATER_ONE_AO)) / 10.0;
	}

	@Override
	public void setPreHeaterOnePower(double power) throws IOServiceException {
		ioService.setAO(Addresses.PRE_HEATER_ONE_AO,
				Conversions.unsingedVoltageToUnsingedInt(power * 10.0));
	}

	@Override
	public double getPreHeaterTwoPower() throws IOServiceException {
		return Conversions.unsingedIntToUnsingedVoltage(
				ioService.getAO(Addresses.PRE_HEATER_TWO_AO)) / 10.0;
	}

	@Override
	public void setPreHeaterTwoPower(double power) throws IOServiceException {
		ioService.setAO(Addresses.PRE_HEATER_TWO_AO,
				Conversions.unsingedVoltageToUnsingedInt(power * 10.0));
	}

	@Override
	public double getHeaterFanOnePower() throws IOServiceException {
		return Conversions.unsingedIntToUnsingedVoltage(
				ioService.getAO(Addresses.HEATER_FAN_ONE)) / 10.0;
	}

	@Override
	public void setHeaterFanOnePower(double power) throws IOServiceException {
		ioService.setAO(Addresses.HEATER_FAN_ONE,
				Conversions.unsingedVoltageToUnsingedInt(power * 10.0));
	}

	@Override
	public double getHeaterFanTwoPower() throws IOServiceException {
		return Conversions.unsingedIntToUnsingedVoltage(
				ioService.getAO(Addresses.HEATER_FAN_TWO)) / 10.0;
	}

	@Override
	public void setHeaterFanTwoPower(double power) throws IOServiceException {
		ioService.setAO(Addresses.HEATER_FAN_TWO,
				Conversions.unsingedVoltageToUnsingedInt(power * 10.0));
	}

}
