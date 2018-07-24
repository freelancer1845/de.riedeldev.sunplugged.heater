package de.riedeldev.sunplugged.heater.preheater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.heater.config.Parameters;
import de.riedeldev.sunplugged.heater.io.Addresses;
import de.riedeldev.sunplugged.heater.io.IOService;

@Service
public class PreHeaterOne extends AbstractPreHeater {

	@Autowired
	public PreHeaterOne(IOService ioService, Parameters parameters) {
		super("Pre Heater One", Addresses.PRE_HEAT_ONE_AI,
				Addresses.PRE_HEATER_ONE_AO, ioService, parameters);
	}

}
