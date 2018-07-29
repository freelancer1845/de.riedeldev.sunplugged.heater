package de.riedeldev.sunplugged.heater.pid;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;

import org.junit.Test;
import org.mockito.Mockito;

import de.riedeldev.sunplugged.heater.config.Parameters;
import de.riedeldev.sunplugged.heater.io.Conversions;
import de.riedeldev.sunplugged.heater.io.IOService;
import de.riedeldev.sunplugged.heater.io.IOServiceException;
import de.riedeldev.sunplugged.heater.preheater.AbstractPreHeater;

public class AbstractPreHeaterTests {

	@Test
	public void forceOffWorks() throws IOServiceException {
		IOService ioMock = Mockito.mock(IOService.class);
		Heater heater = constructStandard(ioMock);
		heater.off();
		Mockito.verify(ioMock).setAO(1, 0);
	}

	@Test
	public void forceControlValueTest()
			throws InterruptedException, IOServiceException {
		IOService ioMock = Mockito.mock(IOService.class);
		ConfigurableHeater heater = constructStandard(ioMock);
		heater.on();

		heater.deactivateControlling();

		heater.setUpdateInterval(100);
		heater.setTargetTemperature(100.0);
		heater.forcePower(1.0);

		Thread.sleep(50);
		Mockito.verify(ioMock).setAO(1,
				Conversions.unsingedVoltageToUnsingedInt(10.0));

	}

	@Test
	public void controlsValue()
			throws InterruptedException, IOServiceException {
		IOService ioMock = Mockito.mock(IOService.class);
		ConfigurableHeater heater = constructStandard(ioMock);
		heater.on();
		heater.activateControlling();
		heater.setUpdateInterval(0.2);
		heater.setTargetTemperature(50);

		Thread.sleep(250);

		Mockito.verify(ioMock, atLeast(2)).setAO(Mockito.eq(1),
				Mockito.anyInt());
	}

	@Test
	public void respectsUpdateIntervalChange()
			throws InterruptedException, IOServiceException {
		IOService ioMock = Mockito.mock(IOService.class);
		ConfigurableHeater heater = constructStandard(ioMock);
		heater.on();
		heater.activateControlling();
		heater.setUpdateInterval(0.2);
		heater.setTargetTemperature(50);

		Thread.sleep(250);

		Mockito.verify(ioMock, atLeast(1)).setAO(Mockito.eq(1),
				Mockito.anyInt());

		heater.setUpdateInterval(0.01);

		Thread.sleep(260);

		Mockito.verify(ioMock, atLeast(9)).setAO(Mockito.eq(1),
				Mockito.anyInt());
	}

	@Test
	public void stopsAndRestartSPID()
			throws InterruptedException, IOServiceException {

		IOService ioMock = Mockito.mock(IOService.class);
		ConfigurableHeater heater = constructStandard(ioMock);
		heater.on();
		heater.setUpdateInterval(.2);
		heater.setTargetTemperature(50);
		heater.activateControlling();

		Thread.sleep(250);
		Mockito.verify(ioMock, atLeast(1)).setAO(Mockito.eq(1),
				Mockito.anyInt());

		heater.deactivateControlling();
		Thread.sleep(250);
		Mockito.reset(ioMock);
		Thread.sleep(250);

		Mockito.verify(ioMock, never()).setAO(Mockito.anyInt(),
				Mockito.anyInt());

		heater.activateControlling();
		Thread.sleep(250);
		Mockito.verify(ioMock, atLeast(1)).setAO(Mockito.eq(1),
				Mockito.anyInt());

	}

	private AbstractPreHeater constructStandard(IOService ioService) {
		Parameters para = new Parameters();
		return new AbstractPreHeater("Test Heater", 1, 1, ioService, para,
				"mockTopic") {
		};
	}

}
