package de.riedeldev.sunplugged.heater.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.heater.config.WebSocketConfig.Topics;
import de.riedeldev.sunplugged.heater.io.Addresses;
import de.riedeldev.sunplugged.heater.io.Conversions;
import de.riedeldev.sunplugged.heater.io.IOService;
import de.riedeldev.sunplugged.heater.io.IOServiceException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MachineStatusImpl implements MachineStatus {

	@Autowired
	private SimpMessagingTemplate template;

	@Value("${publish.status}")
	private boolean publishStatus = false;

	private IOService ioService;

	@Autowired
	public MachineStatusImpl(IOService ioService) {
		this.ioService = ioService;
	}

	private Map<String, Object> oldValuesList = new HashMap<>();

	@Override
	public boolean plcSaysAllFine() throws IOServiceException {
		boolean newValue = ioService.getDI(Addresses.PLC_ALL_FINE);
		sendMessageIfChanged(Topics.PLC_ALL_FINE, newValue);
		return newValue;
	}

	@Override
	public boolean isOvertemperature() throws IOServiceException {
		boolean newValue = ioService.getDI(Addresses.OVERTEMPERATURE);
		sendMessageIfChanged(Topics.IS_OVERTEMPERATURE, newValue);
		return newValue;
	}

	@Override
	public boolean panelInterlockOne() throws IOServiceException {
		boolean newValue = ioService.getDI(Addresses.PANEL_INTERLOCK_ONE);
		sendMessageIfChanged(Topics.PANEL_INTERLOCK_ONE, newValue);
		return newValue;
	}

	@Override
	public boolean panelInterlockTwo() throws IOServiceException {
		boolean newValue = ioService.getDI(Addresses.PANEL_INTERLOCK_TWO);
		sendMessageIfChanged(Topics.PANEL_INTERLOCK_TWO, newValue);
		return newValue;
	}

	@Override
	public boolean panelInterlockThree() throws IOServiceException {
		boolean newValue = ioService.getDI(Addresses.PANEL_INTERLOCK_THREE);
		sendMessageIfChanged(Topics.PANEL_INTERLOCK_THREE, newValue);
		return newValue;
	}

	@Override
	public boolean coverAlarmTop() throws IOServiceException {

		boolean newValue = ioService.getDI(Addresses.COVER_ALARM_TOP);
		sendMessageIfChanged(Topics.COVER_ALARM_TOP, newValue);
		return newValue;
	}

	@Override
	public boolean coverAlarmBottom() throws IOServiceException {
		boolean newValue = ioService.getDI(Addresses.COVER_ALARM_BOTTOM);
		sendMessageIfChanged(Topics.COVER_ALARM_BOTTOM, newValue);
		return newValue;
	}

	@Override
	public boolean panelInterlockAlarm() throws IOServiceException {
		boolean newValue = ioService.getDI(Addresses.PANEL_INTERLOCK_ALARM);
		sendMessageIfChanged(Topics.PANEL_INTERLOCK_ALARM, newValue);
		return newValue;
	}

	@Override
	public boolean isHorn() throws IOServiceException {
		boolean newValue = ioService.getDO(Addresses.HORN);
		sendMessageIfChanged(Topics.IS_HORN, newValue);
		return ioService.getDO(Addresses.HORN);
	}

	@Override
	public void setHorn(boolean on) throws IOServiceException {
		ioService.setDO(Addresses.HORN, on);;
		sendMessageIfChanged(Topics.IS_HORN, on);
	}

	@Override
	public boolean isPlcStart() throws IOServiceException {
		boolean newValue = ioService.getDO(Addresses.PLC_START);
		sendMessageIfChanged(Topics.IS_PLC_START, newValue);
		return newValue;
	}

	@Override
	public void setPlcStart(boolean on) throws IOServiceException {
		ioService.setDO(Addresses.PLC_START, on);
		sendMessageIfChanged(Topics.IS_PLC_START, on);
	}

	@Override
	public boolean isPlcRun() throws IOServiceException {
		boolean newValue = ioService.getDO(Addresses.PLC_RUN);
		sendMessageIfChanged(Topics.IS_PLC_RUN, newValue);
		return newValue;
	}

	@Override
	public void setPlcRun(boolean on) throws IOServiceException {
		ioService.setDO(Addresses.PLC_RUN, on);
	}

	@Override
	public boolean[] getAlarmLights() throws IOServiceException {
		boolean[] newArray = new boolean[]{
				ioService.getDO(Addresses.GREEN_LIGHT),
				ioService.getDO(Addresses.ORANGE_LIGHT),
				ioService.getDO(Addresses.RED_LIGHT)};
		sendMessageIfChanged(Topics.ALARM_LIGHTS, newArray);
		return newArray;
	}

	@Override
	public void setAlarmLights(boolean green, boolean orange, boolean red)
			throws IOServiceException {

		boolean[] values = {green, orange, red};
		ioService.setDO(Addresses.GREEN_LIGHT, green);
		ioService.setDO(Addresses.ORANGE_LIGHT, orange);
		ioService.setDO(Addresses.RED_LIGHT, red);
		sendMessageIfChanged(Topics.ALARM_LIGHTS, values);
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
		double newValue = Conversions
				.typeKConversion(ioService.getAI(Addresses.MAIN_HEATER_ONE_AI));
		sendMessageIfChanged(Topics.ZONE_ONE_TEMPERATURE, newValue);
		return newValue;
	}

	@Override
	public double getZoneTwoTemperature() throws IOServiceException {
		double newValue = Conversions
				.typeKConversion(ioService.getAI(Addresses.MAIN_HEATER_TWO_AI));
		sendMessageIfChanged(Topics.ZONE_TWO_TEMPERATURE, newValue);
		return newValue;
	}

	@Override
	public double getZoneThreeTemperature() throws IOServiceException {
		double newValue = Conversions.typeKConversion(
				ioService.getAI(Addresses.MAIN_HEATER_THREE_AI));
		sendMessageIfChanged(Topics.ZONE_THREE_TEMPERATURE, newValue);
		return newValue;
	}

	@Override
	public double getPreHeaterOneTemperature() throws IOServiceException {
		double newValue = Conversions
				.typeKConversion(ioService.getAI(Addresses.PRE_HEATER_ONE_AI));
		sendMessageIfChanged(Topics.HEATER_ONE_TEMPERATURE, newValue);
		return newValue;
	}

	@Override
	public double getPreHeaterTwoTemperature() throws IOServiceException {
		double newValue = Conversions
				.typeKConversion(ioService.getAI(Addresses.PRE_HEATER_TWO_AI));
		sendMessageIfChanged(Topics.HEATER_TWO_TEMPERATURE, newValue);
		return newValue;
	}

	@Override
	public double getHeaterFanOnePower() throws IOServiceException {
		double newValue = Conversions.unsingedIntToUnsingedVoltage(
				ioService.getAO(Addresses.HEATER_FAN_ONE)) / 10.0;
		sendMessageIfChanged(Topics.HEATER_FAN_ONE_POWER, newValue);
		return newValue;
	}

	@Override
	public void setHeaterFanOnePower(double power) throws IOServiceException {
		ioService.setAO(Addresses.HEATER_FAN_ONE,
				Conversions.unsingedVoltageToUnsingedInt(power * 10.0));
		sendMessageIfChanged(Topics.HEATER_FAN_ONE_POWER, power);
	}

	@Override
	public double getHeaterFanTwoPower() throws IOServiceException {
		double newValue = Conversions.unsingedIntToUnsingedVoltage(
				ioService.getAO(Addresses.HEATER_FAN_TWO)) / 10.0;
		sendMessageIfChanged(Topics.HEATER_FAN_TWO_POWER, newValue);
		return newValue;
	}

	@Override
	public void setHeaterFanTwoPower(double power) throws IOServiceException {
		ioService.setAO(Addresses.HEATER_FAN_TWO,
				Conversions.unsingedVoltageToUnsingedInt(power * 10.0));
		sendMessageIfChanged(Topics.HEATER_FAN_TWO_POWER, power);
	}

	private void sendMessageIfChanged(String topic, Object value) {
		if (oldValuesList.containsKey(topic) == false) {
			oldValuesList.put(topic, value);
			template.convertAndSend(topic, value);
		} else {
			if (oldValuesList.get(topic).equals(value) == false) {
				template.convertAndSend(topic, value);
				oldValuesList.put(topic, value);
			}
		}
	}

	private void sendMessageIfChanged(String topic, boolean[] values) {
		if (oldValuesList.containsKey(topic) == false) {
			oldValuesList.put(topic, values);
			template.convertAndSend(topic, values);
		} else {
			if (Arrays.equals((boolean[]) oldValuesList.get(topic),
					values) == false) {
				template.convertAndSend(topic, values);
				oldValuesList.put(topic, values);
			}
		}
	}

}
