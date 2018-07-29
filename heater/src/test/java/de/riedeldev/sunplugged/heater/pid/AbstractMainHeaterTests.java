package de.riedeldev.sunplugged.heater.pid;

import static org.mockito.Mockito.atLeastOnce;

import org.junit.Test;
import org.mockito.Mockito;

import de.riedeldev.sunplugged.heater.config.Parameters;
import de.riedeldev.sunplugged.heater.io.IOService;
import de.riedeldev.sunplugged.heater.io.IOServiceException;
import de.riedeldev.sunplugged.heater.mainheater.AbstractMainHeater;

public class AbstractMainHeaterTests {

	@Test
	public void relayLoopStarts()
			throws InterruptedException, IOServiceException {
		IOService ioService = Mockito.mock(IOService.class);
		Heater heater = constructStandard(ioService);

		heater.forcePower(0.5);

		heater.on();
		Thread.sleep(100);
		Mockito.verify(ioService, atLeastOnce()).setDO(0, true);

	}

	private AbstractMainHeater constructStandard(IOService ioService) {
		return new AbstractMainHeater("Test Heater", 0, 0, ioService,
				new Parameters(), "mockTopic") {

		};

	}

}
