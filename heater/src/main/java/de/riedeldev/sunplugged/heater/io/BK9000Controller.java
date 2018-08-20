package de.riedeldev.sunplugged.heater.io;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.beckhoff.bk9000.BK9000;
import de.riedeldev.sunplugged.heater.config.WebSocketConfig.Topics;

@Controller
@Service
public class BK9000Controller implements IOService {

	@Autowired
	private BK9000 bk9000;

	@Autowired
	private SimpMessagingTemplate template;

	@Override
	@MessageMapping(Topics.DO_ACCESS)
	public void setDO(@DestinationVariable int address, boolean value)
			throws IOServiceException {
		bk9000.setDigitalOutput(address, value).thenAcceptAsync(res -> {
			template.convertAndSend("/topic/doaccess/" + address, value);
		});
	}

	@Override
	@MessageMapping(Topics.DO_ACCESS + "/get")
	@SendTo("/topic" + Topics.DO_ACCESS)
	public boolean getDO(@DestinationVariable int address)
			throws IOServiceException {
		try {
			return bk9000.readDigitalOutput(address).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IOServiceException(e);
		}
	}

	@Override
	@MessageMapping(Topics.DI + "/get")
	@SendTo("/topic" + Topics.DI)
	public boolean getDI(@DestinationVariable int address)
			throws IOServiceException {
		try {
			return bk9000.readDigitalInput(address).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IOServiceException(e);
		}
	}

	@Override
	@MessageMapping(Topics.AO_ACCESS)
	public void setAO(@DestinationVariable int address, double value)
			throws IOServiceException {
		bk9000.writeAnalogOutput(address, value).thenAcceptAsync(res -> {
			template.convertAndSend("/topic/aoaccess/" + address, value);
		});
	}

	@Override
	@MessageMapping(Topics.AO_ACCESS + "/get")
	@SendTo("/topic" + Topics.AO_ACCESS)
	public double getAO(@DestinationVariable int address)
			throws IOServiceException {
		try {
			return bk9000.readAnalogOutput(address).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IOServiceException(e);
		}
	}

	@Override
	@MessageMapping(Topics.AI + "/get")
	@SendTo("/topic" + Topics.AI)
	public double getAI(@DestinationVariable int address)
			throws IOServiceException {
		try {
			return bk9000.readAnalogInput(address).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IOServiceException(e);
		}
	}

}
