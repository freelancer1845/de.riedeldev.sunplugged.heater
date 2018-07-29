package de.riedeldev.sunplugged.heater.mainheater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.heater.config.Parameters;
import de.riedeldev.sunplugged.heater.config.WebSocketConfig.Topics;
import de.riedeldev.sunplugged.heater.io.Addresses;
import de.riedeldev.sunplugged.heater.io.IOService;

@Service
public class MainHeaterThree extends AbstractMainHeater {

	@Autowired
	public MainHeaterThree(IOService ioService, Parameters parameters) {
		super("Main Heater Three", Addresses.MAIN_HEATER_THREE_DO,
				Addresses.MAIN_HEATER_THREE_AI, ioService, parameters,
				Topics.MAIN_HEATER_THREE);
	}

}
