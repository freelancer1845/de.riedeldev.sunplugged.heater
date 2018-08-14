package de.riedeldev.sunplugged.heater.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.util.UriComponentsBuilder;

import de.riedeldev.sunplugged.heater.config.WebSocketConfig.Topics;
import de.riedeldev.sunplugged.heater.io.IOServiceException;
import de.riedeldev.sunplugged.heater.pid.AbstractHeater;
import de.riedeldev.sunplugged.heater.pid.HeaterStatus;
import de.riedeldev.sunplugged.heater.preheater.PreHeaterOne;

@Controller
@ApplicationScope
public class HeaterAccessController {

	@Autowired
	private PreHeaterOne preHeaterOne;

	@Autowired
	private List<AbstractHeater> heaters;

	@Autowired
	private SimpMessagingTemplate template;

	@MessageMapping(Topics.PRE_HEATER_ONE)
	public void setPreHeaterOnePower(double power) throws IOServiceException {
		preHeaterOne.forcePower(power);
	}

	@MessageMapping(Topics.HEATER_ACCESS + "/get")
	public void onGet(@DestinationVariable String topic)
			throws MessagingException, IOServiceException {
		AbstractHeater heater = heaters.stream()
				.filter(h -> h.getTopic().equals(topic)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"No Heater registered under topic: " + topic));
		sendNewStatusForHeater(heater);
	}

	private void sendNewStatusForHeater(AbstractHeater heater)
			throws MessagingException, IOServiceException {
		String path = UriComponentsBuilder.fromPath("/topic")
				.path(Topics.HEATER_ACCESS).buildAndExpand(heater.getTopic())
				.toString();
		template.convertAndSend(path, HeaterStatus.buildFromHeater(heater));
	}

	@MessageMapping(Topics.HEATER_ACCESS)
	public void onMessage(@DestinationVariable String topic,
			@Payload HeaterStatus status)
			throws MessagingException, IOServiceException {
		AbstractHeater heater = heaters.stream()
				.filter(h -> h.getTopic().equals(topic)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"No Heater registered under topic: " + topic));
		heater.handleNewStatus(status);
		sendNewStatusForHeater(heater);
	}

}
