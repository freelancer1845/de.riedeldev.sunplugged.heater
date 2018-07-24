package de.riedeldev.sunplugged.heater.preheater;

import de.riedeldev.sunplugged.heater.config.Parameters;
import de.riedeldev.sunplugged.heater.io.Conversions;
import de.riedeldev.sunplugged.heater.io.IOService;
import de.riedeldev.sunplugged.heater.pid.AbstractHeater;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractPreHeater extends AbstractHeater {

	private static final double MAX_TEMPERATURE = 150.0;

	private static final double MAX_VOLTAGE = 10.0;

	private final int analogInput;

	private final int analogOutput;

	private volatile boolean isOn = false;

	private IOService ioService;

	public AbstractPreHeater(String name, int analogInput, int analogOutput,
			IOService ioService, Parameters parameters) {
		super(name);
		this.analogInput = analogInput;
		this.analogOutput = analogOutput;
		this.ioService = ioService;

		parameters.registerListener(para -> {
			setPIDValues(para.getPreHeaterP(), para.getPreHeaterI(),
					para.getPreHeaterD());
			setUpdateInterval(para.getPreHeaterIntervalLength());
		});
		setPIDValues(parameters.getPreHeaterP(), parameters.getPreHeaterI(),
				parameters.getPreHeaterD());
		setUpdateInterval(parameters.getPreHeaterIntervalLength());
	}

	@Override
	public void on() {
		isOn = true;

	}

	@Override
	public void off() {
		ioService.setAO(analogOutput, 0);
		isOn = false;
	}

	@Override
	public boolean isOn() {
		return isOn;
	}

	@Override
	public double getCurrentTemperature() {
		return Conversions.typeKConversion(ioService.getAI(analogInput));
	}
	@Override
	public void forcePower(double power) {
		super.forcePower(power);
		updateOutput();
	}

	@Override
	protected void submitChange(double change) {
		setPower(getPower() + change);
		updateOutput();
	}

	@Override
	public void setTargetTemperature(double target) {
		if (target > MAX_TEMPERATURE) {
			log.error(String.format(
					"Target Temperature for PreHeater '%s' too high. (%.2f)",
					name, target));
			return;
		}
		super.setTargetTemperature(target);
	}

	private void updateOutput() {
		if (isOn) {
			ioService.setAO(analogOutput, Conversions
					.unsingedVoltageToUnsingedInt(getPower() * 10.0));
		} else if (getPower() > 0.0) {
			log.warn("Trying to set power > 0. But PreHeater is off - " + name);
		}

	}

	/**
	 * Static do be testable
	 * 
	 * @param t
	 * @return
	 */
	public static double temperatureToVoltage(double t) {
		return t / MAX_TEMPERATURE * MAX_VOLTAGE;
	}

}