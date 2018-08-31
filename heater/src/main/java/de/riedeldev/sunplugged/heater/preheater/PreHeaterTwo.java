package de.riedeldev.sunplugged.heater.preheater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.heater.config.Parameters;
import de.riedeldev.sunplugged.heater.config.WebSocketConfig.Topics;
import de.riedeldev.sunplugged.heater.io.Addresses;
import de.riedeldev.sunplugged.heater.io.IOService;

@Service
public class PreHeaterTwo extends AbstractPreHeater {

	@Autowired
	public PreHeaterTwo(IOService ioService, Parameters parameters) {
		super("Pre Heater Two", Addresses.PRE_HEATER_TWO_AI,
				Addresses.PRE_HEATER_TWO_AO, ioService, parameters,
				Topics.PRE_HEATER_TWO);
	}

}
