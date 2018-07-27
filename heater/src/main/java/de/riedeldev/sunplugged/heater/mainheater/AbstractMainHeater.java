package de.riedeldev.sunplugged.heater.mainheater;

import javax.annotation.PostConstruct;

import de.riedeldev.sunplugged.heater.config.Parameters;
import de.riedeldev.sunplugged.heater.io.Conversions;
import de.riedeldev.sunplugged.heater.io.IOService;
import de.riedeldev.sunplugged.heater.io.IOServiceException;
import de.riedeldev.sunplugged.heater.pid.AbstractHeater;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractMainHeater extends AbstractHeater {

	/** in seconds */
	private long FULL_CYCLE_WIDTH = 5000;

	private final int switchOutput;

	private final int temperatureInput;

	private IOService ioService;

	private Object waitObject = new Object();

	private volatile boolean on = false;

	private Thread relayThread;

	public AbstractMainHeater(String name, int switchOutput,
			int temperatureInput, IOService ioService, Parameters parameters) {
		super(name, parameters);
		this.ioService = ioService;
		this.switchOutput = switchOutput;
		this.temperatureInput = temperatureInput;

		relayThread = createRelayThread();
	}

	@Override
	protected void setParameters(Parameters parameters) {
		super.setParameters(parameters);
		FULL_CYCLE_WIDTH = parameters.getFullcycleWidth();
	}

	@PostConstruct
	protected void postConstruct() {
		startControlLoop();
	}

	private Thread createRelayThread() {
		Thread t = new Thread(() -> relayLoop(), "Relay Thread: " + name);
		t.setDaemon(true);
		return t;
	}

	private void relayLoop() {
		try {
			long onWidth = 0;
			long offWidth = 0;
			while (Thread.interrupted() == false) {
				if (isOn() == false) {
					ioService.setDO(switchOutput, false);
					synchronized (waitObject) {
						waitObject.wait();
					}
				}
				onWidth = (long) (FULL_CYCLE_WIDTH * getPower());
				offWidth = (long) ((1 - getPower()) * FULL_CYCLE_WIDTH);

				if (onWidth > 40) {
					ioService.setDO(switchOutput, true);
					Thread.sleep(onWidth);
				}
				if (offWidth > 40) {
					ioService.setDO(switchOutput, false);
					Thread.sleep(offWidth);
				}
			}
		} catch (InterruptedException e) {
			log.debug(Thread.currentThread().getName() + " was interrupted.",
					e);
		} catch (IOServiceException e) {
			log.error("IOService failed.", e);
		}

	}

	@Override
	protected void submitChange(double change) {
		// setPower(getPower() + change);
		setPower(change);
	}

	@Override
	public void on() {
		on = true;

		if (relayThread.isAlive() == false) {
			try {
				relayThread.start();
			} catch (IllegalThreadStateException e) {
				log.warn("Relay Thread for heater " + name
						+ " was dead. Restarting it!");
				relayThread = createRelayThread();
				relayThread.start();
			}
		} else {
			synchronized (waitObject) {
				waitObject.notifyAll();

			}
		}

	}

	@Override
	public double getCurrentTemperature() throws IOServiceException {
		return Conversions.typeKConversion(ioService.getAI(temperatureInput));
	}

	@Override
	public void off() {
		on = false;
	}

	@Override
	public boolean isOn() {

		return on;
	}
}
