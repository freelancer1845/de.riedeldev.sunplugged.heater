package de.riedeldev.sunplugged.beckhoff.kl4xxx;

import java.util.concurrent.CompletableFuture;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.requests.ReadInputRegistersRequest;
import com.digitalpetri.modbus.requests.WriteSingleRegisterRequest;
import com.digitalpetri.modbus.responses.ReadInputRegistersResponse;

import de.riedeldev.sunplugged.beckhoff.klspi.AnalogOutputKlemme;
import de.riedeldev.sunplugged.beckhoff.klspi.Configurator;

public class KL4004 implements AnalogOutputKlemme {

	private static final int ADDRESSES_NEEDED_IN_READ = 0;
	private static final int ADDRESSES_NEEDED_IN_WRITE = 8;
	private static final int OUTPUTS = 4;

	private ModbusTcpMaster master;

	private int writeAddressOffset;
	private int readAddressOffset;

	private Configurator configurator = new Configurator(master,
			writeAddressOffset);

	private Double[] maxValueForOutput = {10.0, 10.0, 10.0, 10.0};

	@Override
	public void attach(int readAddressOfset, int writeAddressOffset,
			ModbusTcpMaster master) {
		this.master = master;
		this.writeAddressOffset = writeAddressOffset;
		this.readAddressOffset = readAddressOfset;
		this.configurator = new Configurator(master, writeAddressOffset);
	}

	public void setUserScaling(boolean on) {
		if (on) {
			activateUserScaling();
		} else {
			deactivateUserScaling();
		}
	}

	public void setUserSwitchOnValue(boolean on) {
		if (on) {
			activateUserSwitchOnValue();
		} else {
			deactivateUserSwitchOnValue();
		}
	}

	public void setUserGain(int gain) {
		configurator.deactivateReadOnly().thenCompose((voidValue) -> {
			return configurator.writeValueToConfigRegister(34, gain);
		});
	}

	public void setUserOffset(int offset) {
		configurator.deactivateReadOnly().thenCompose((voidValue) -> {
			return configurator.writeValueToConfigRegister(33, offset);
		});
	}

	public void setUserSwitchOnValue(int value) {
		configurator.deactivateReadOnly().thenCompose((voidValue) -> {
			return configurator.writeValueToConfigRegister(35, value);
		});
	}

	@Override
	public CompletableFuture<Double> readOutput(int number) {
		return master
				.sendRequest(
						new ReadInputRegistersRequest(readAddressOffset, 1), 0)
				.thenApply(res -> {
					ReadInputRegistersResponse response = (ReadInputRegistersResponse) res;

					int result = response.getRegisters().getShort(0);
					return convertRegisterValueToVoltage(number, result);
				});
	}
	@Override
	public int addressesNeededInRead() {
		return ADDRESSES_NEEDED_IN_READ;
	}

	@Override
	public int addressesNeededInWrite() {
		return ADDRESSES_NEEDED_IN_WRITE;
	}

	@Override
	public CompletableFuture<Void> setOutput(int number, double value) {
		return master
				.sendRequest(
						new WriteSingleRegisterRequest(
								writeAddressOffset + (number * 2) + 1,
								convertVoltageToRegisterValue(number, value)),
						0)
				.thenAccept(res -> {
				});
	}

	private int convertVoltageToRegisterValue(int number, double value) {
		return (int) ((value / maxValueForOutput[number]) * Short.MAX_VALUE);
	}

	private double convertRegisterValueToVoltage(int number, int value) {
		return (((double) value / (double) Short.MAX_VALUE)
				* maxValueForOutput[number]);
	}

	private void activateUserScaling() {
		configurator.deactivateReadOnly().thenCompose((voidValue) -> {
			return configurator.readValuteFromConfigRegister(32);
		}).thenCompose(value -> {

			return configurator.writeValueToConfigRegister(32, (value | 1));

		});
	}

	private void deactivateUserScaling() {
		configurator.deactivateReadOnly().thenCompose((voidValue) -> {
			return configurator.readValuteFromConfigRegister(32);
		}).thenCompose(value -> {

			return configurator.writeValueToConfigRegister(32, (value & ~1));

		});
	}

	private void activateUserSwitchOnValue() {
		configurator.deactivateReadOnly().thenCompose((voidValue) -> {
			return configurator.readValuteFromConfigRegister(32);
		}).thenCompose(value -> {

			return configurator.writeValueToConfigRegister(32,
					(value | (1 << 8)));

		});
	}

	private void deactivateUserSwitchOnValue() {
		configurator.deactivateReadOnly().thenCompose((voidValue) -> {
			return configurator.readValuteFromConfigRegister(32);
		}).thenCompose(value -> {

			return configurator.writeValueToConfigRegister(32,
					(value & ~(1 << 8)));

		});
	}

	@Override
	public int outputs() {
		return OUTPUTS;
	}

}
