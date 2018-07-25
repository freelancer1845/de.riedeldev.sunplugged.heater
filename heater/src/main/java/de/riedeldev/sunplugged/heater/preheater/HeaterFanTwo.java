package de.riedeldev.sunplugged.heater.preheater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.heater.io.Addresses;
import de.riedeldev.sunplugged.heater.io.Conversions;
import de.riedeldev.sunplugged.heater.io.IOService;
import de.riedeldev.sunplugged.heater.io.IOServiceException;

@Service
public class HeaterFanTwo implements HeaterFan {

	private IOService ioService;

	@Autowired
	public HeaterFanTwo(IOService ioService) {
		this.ioService = ioService;
	}

	@Override
	public double getPower() throws IOServiceException {
		return Conversions.unsingedIntToUnsingedVoltage(
				ioService.getAO(Addresses.HEATER_FAN_TWO)) / 10.0;
	}

	@Override
	public void setPower(double newPower) throws IOServiceException {
		if (newPower < 0) {
			newPower = 0;
		} else if (newPower > 1) {
			newPower = 1;
		}
		ioService.setAO(Addresses.HEATER_FAN_TWO,
				Conversions.unsingedVoltageToUnsingedInt(newPower * 10.0));
	}

}
