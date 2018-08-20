package de.riedeldev.sunplugged.heater.io;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import de.riedeldev.sunplugged.beckhoff.bk9000.BK9000;
import de.riedeldev.sunplugged.heater.config.WebSocketConfig.Topics;

@Component
public class InputDataPuplisher {
	private static final int MAX_DIGITAL_INPUTS = 12;

	private static final int MAX_ANALOG_INPUTS = 4;

	private boolean[] previousDI = new boolean[MAX_DIGITAL_INPUTS];
	private double[] previousAI = new double[MAX_ANALOG_INPUTS];

	@Autowired
	private BK9000 bk9000;

	@Autowired
	private SimpMessagingTemplate template;

	@Scheduled(fixedRate = 500)
	public void publishInputValues()
			throws InterruptedException, ExecutionException {
		for (int i = 0; i < MAX_DIGITAL_INPUTS; i++) {
			boolean value = bk9000.readDigitalInput(i).get();
			if (previousDI[i] != value) {
				String path = UriComponentsBuilder.fromPath("/topic")
						.path(Topics.DI).buildAndExpand(i).toString();
				template.convertAndSend(path, value);
				previousDI[i] = value;
			}
		}
		for (int i = 0; i < MAX_ANALOG_INPUTS; i++) {
			double value = bk9000.readAnalogInput(i).get();
			if (previousAI[i] != value) {
				String path = UriComponentsBuilder.fromPath("/topic")
						.path(Topics.AI).buildAndExpand(i).toString();
				template.convertAndSend(path, value);
				previousAI[i] = value;
			}
		}
	}
}
