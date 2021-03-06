package de.riedeldev.sunplugged.heater.preheater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.heater.io.Addresses;
import de.riedeldev.sunplugged.heater.io.Conversions;
import de.riedeldev.sunplugged.heater.io.IOService;
import de.riedeldev.sunplugged.heater.io.IOServiceException;

@Service
public class HeaterFanOne implements HeaterFan {

	private IOService ioService;

	@Autowired
	public HeaterFanOne(IOService ioService) {
		this.ioService = ioService;
	}

	@Override
	public double getPower() throws IOServiceException {
		return ioService.getAO(Addresses.HEATER_FAN_ONE) / 10.0;
	}

	@Override
	public void setPower(double newPower) throws IOServiceException {
		if (newPower < 0) {
			newPower = 0;
		} else if (newPower > 1) {
			newPower = 1;
		}
		ioService.setAO(Addresses.HEATER_FAN_ONE,
				Conversions.unsingedVoltageToUnsingedInt(newPower * 10.0));
	}

}
