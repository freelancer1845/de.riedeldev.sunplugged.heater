package de.riedeldev.sunplugged.heater.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.ApplicationScope;

import de.riedeldev.sunplugged.heater.config.WebSocketConfig.Topics;
import de.riedeldev.sunplugged.heater.io.IOServiceException;
import de.riedeldev.sunplugged.heater.preheater.PreHeaterOne;

@Controller
@ApplicationScope
public class HeaterAccessController {

	@Autowired
	private PreHeaterOne preHeaterOne;

	@MessageMapping(Topics.PRE_HEATER_ONE)
	public void setPreHeaterOnePower(double power) throws IOServiceException {
		preHeaterOne.forcePower(power);
	}

}
