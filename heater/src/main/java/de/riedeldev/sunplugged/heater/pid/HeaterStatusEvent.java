package de.riedeldev.sunplugged.heater.pid;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEvent;

import de.riedeldev.sunplugged.heater.io.IOServiceException;

public class HeaterStatusEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3653904311149805419L;

	private final HeaterStatusSnapshot status;

	public static HeaterStatusEvent getFor(Heater heater)
			throws IOServiceException {
		return new HeaterStatusEvent(heater);
	}

	public Heater getStatus() {
		return status;
	}

	private HeaterStatusEvent(Heater heater) throws IOServiceException {
		super(heater);
		status = HeaterStatusSnapshot.create(heater);
	}

	public static class HeaterStatusSnapshot implements Heater {

		private final LocalDateTime timeStamp;

		private final boolean on;

		private final boolean controlling;

		private final double targetTemperature;

		private final double currentTemperature;

		private final double power;

		private static HeaterStatusSnapshot create(Heater heater)
				throws IOServiceException {
			return new HeaterStatusSnapshot(heater.isOn(),
					heater.isControlling(), heater.getTargetTemperature(),
					heater.getCurrentTemperature(), heater.getPower());
		}

		private HeaterStatusSnapshot(boolean on, boolean controlling,
				double targetTemperature, double currentTemperature,
				double power) {
			this.timeStamp = LocalDateTime.now();
			this.on = on;
			this.controlling = controlling;
			this.targetTemperature = targetTemperature;
			this.currentTemperature = currentTemperature;
			this.power = power;
		}

		public LocalDateTime getTimeStamp() {
			return timeStamp;
		}

		@Override
		public void on() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void off() throws IOServiceException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isOn() {
			return on;
		}

		@Override
		public boolean isControlling() {
			return controlling;
		}

		@Override
		public void activateControlling() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void deactivateControlling() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setTargetTemperature(double target) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double getTargetTemperature() {
			return targetTemperature;
		}

		@Override
		public double getCurrentTemperature() throws IOServiceException {
			return currentTemperature;
		}

		@Override
		public void forcePower(double power) throws IOServiceException {
			throw new UnsupportedOperationException();
		}

		@Override
		public double getPower() {
			return power;
		}
	}
}
