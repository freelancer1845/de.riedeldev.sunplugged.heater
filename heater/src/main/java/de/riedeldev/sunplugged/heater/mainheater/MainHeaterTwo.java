package de.riedeldev.sunplugged.heater.mainheater;

import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.heater.config.Parameters;
import de.riedeldev.sunplugged.heater.config.WebSocketConfig.Topics;
import de.riedeldev.sunplugged.heater.io.Addresses;
import de.riedeldev.sunplugged.heater.io.IOService;

//@Service
public class MainHeaterTwo extends AbstractMainHeater {

	public MainHeaterTwo(IOService ioService, Parameters parameters) {
		super("Main Heater Two", Addresses.MAIN_HEATER_TOW_DO,
				Addresses.MAIN_HEATER_TWO_AI, ioService, parameters,
				Topics.MAIN_HEATER_TWO);
	}

}
