package de.riedeldev.sunplugged.heater.pid;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractHeater implements ConfigurableHeater, Runnable {

	private volatile boolean running = true;

	private volatile boolean control = false;

	protected MiniPID miniPID = new MiniPID(1.0, 1.0, 1.0);

	private double updateInterval = 0.0;

	protected final String name;

	protected Thread thread;

	private Object waitObject = new Object();

	private double power = 0.0;

	public AbstractHeater(String name) {
		this.name = name;
		this.thread = new Thread(this, name);
		miniPID.setSetpoint(0.0);
	}

	@Override
	public void startControlLoop() {
		if (thread.isAlive() == false) {
			this.thread = new Thread(this, name);
		}
		running = true;
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void stopControlLoop() {
		thread.interrupt();
	}

	@Override
	public void run() {
		log.debug(String.format("Starting Heater PID Loop '%s'", name));
		while (running && Thread.interrupted() == false) {
			long lastTime = System.currentTimeMillis();

			if (control) {
				double change = miniPID.getOutput(getCurrentTemperature());
				submitChange(change);
			} else {
				synchronized (waitObject) {
					try {
						waitObject.wait();
					} catch (InterruptedException e) {
						break;
					}
				}
			}

			long timePassed = System.currentTimeMillis() - lastTime;
			if (timePassed < 0) {
				timePassed = 0;
			}
			long millisecondsEachCycle = (long) (updateInterval * 1000);

			long millisecondsToWait = millisecondsEachCycle - timePassed;
			if (millisecondsToWait > 0) {
				try {
					Thread.sleep(millisecondsToWait);
				} catch (InterruptedException e) {
					log.error("Heater PID Loop interrupted!", e);
				}
			}

		}
		log.debug(String.format("Heater PID Loop '%s' stopped.", name));
	}

	protected abstract void submitChange(double change);

	@Override
	public void activateControlling() {
		log.debug(String.format("Activated Controlling for Heater Loop '%s'",
				name));
		this.control = true;
		if (thread.isAlive() == false) {
			startControlLoop();
		}
		synchronized (waitObject) {
			waitObject.notifyAll();
		}
	}

	@Override
	public void deactivateControlling() {
		log.debug(String.format("Deactivated Controlling for Heater Loop '%s'",
				name));
		this.control = false;
	}

	@Override
	public boolean isControlling() {
		return control;
	}

	@Override
	public void setTargetTemperature(double target) {
		log.debug(String.format(
				"New Target Temperature for Heater Loop '%s': '%.2f'", name,
				target));
		miniPID.setSetpoint(target);
	}

	@Override
	public double getTargetTemperature() {
		return miniPID.getSetpoint();
	}

	@Override
	public void setPIDValues(double P, double I, double D) {
		log.debug(String.format(
				"New PID Values for Heater Loop '%s': P='%.3f' I='%.3f' D='%.3f'",
				name, P, I, D));
		miniPID.setPID(P, I, D);
	}

	@Override
	public double[] getPIDValues() {
		return new double[]{miniPID.getP(), miniPID.getI(), miniPID.getD()};
	}

	@Override
	public double getUpdateInterval() {
		return updateInterval;
	}

	@Override
	public void setUpdateInterval(double t) {
		log.debug(String.format(
				"New UpdateInterval for Heater Loop '%s':'%.3f' ", name, t));
		this.updateInterval = t;
	}

	@Override
	public void forcePower(double power) {
		if (isControlling()) {
			log.warn(
					"Trying to force power, but PID is running. Consider deactivating it!");
		}
		setPower(power);
	}

	public double getPower() {
		return power;
	}

	protected void setPower(double power) {
		if (power < 0) {
			this.power = 0;
		} else if (power > 1) {
			this.power = 1;
		}
		this.power = power;
	}

}