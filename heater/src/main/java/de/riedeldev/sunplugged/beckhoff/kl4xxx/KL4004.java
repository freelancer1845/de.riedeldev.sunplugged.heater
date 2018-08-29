package de.riedeldev.sunplugged.beckhoff.kl4xxx;

import java.util.concurrent.CompletableFuture;

import com.digitalpetri.modbus.requests.ReadInputRegistersRequest;
import com.digitalpetri.modbus.requests.WriteSingleRegisterRequest;
import com.digitalpetri.modbus.responses.ReadInputRegistersResponse;

import de.riedeldev.sunplugged.beckhoff.klspi.AbstractClamp;
import de.riedeldev.sunplugged.beckhoff.klspi.AnalogOutputKlemme;
import de.riedeldev.sunplugged.beckhoff.klspi.Configurator;

public class KL4004 extends AbstractClamp implements AnalogOutputKlemme {

	private static final int ADDRESS_SPACE_SIZE = 8;
	private static final int OUTPUTS = 4;

	private Double[] maxValueForOutput = {10.0, 10.0, 10.0, 10.0};

	public KL4004(String id) {
		super(ADDRESS_SPACE_SIZE, ADDRESS_SPACE_SIZE, id);

	}

	public Configurator getConfigurator(int output) {
		if (master == null) {
			throw new IllegalStateException("Not attached! TcpMaster was null");
		}
		return new Configurator(master, readAddressOffset + (2 * output),
				writeAddressOffset + (2 * output));
	}

	// @Override
	// public void setOutputOffset(int offset) {
	// super.setOutputOffset(offset);
	// this.configurator = new Configurator(master, offset);
	// }
	//
	// public void setUserScaling(boolean on) {
	// if (on) {
	// activateUserScaling();
	// } else {
	// deactivateUserScaling();
	// }
	// }
	//
	// public void setUserSwitchOnValue(boolean on) {
	// if (on) {
	// activateUserSwitchOnValue();
	// } else {
	// deactivateUserSwitchOnValue();
	// }
	// }
	//
	// public void setUserGain(int gain) {
	// configurator.deactivateReadOnly().thenCompose((voidValue) -> {
	// return configurator.writeValueToConfigRegister(34, gain);
	// });
	// }
	//
	// public void setUserOffset(int offset) {
	// configurator.deactivateReadOnly().thenCompose((voidValue) -> {
	// return configurator.writeValueToConfigRegister(33, offset);
	// });
	// }
	//
	// public void setUserSwitchOnValue(int value) {
	// configurator.deactivateReadOnly().thenCompose((voidValue) -> {
	// return configurator.writeValueToConfigRegister(35, value);
	// });
	// }

	@Override
	public CompletableFuture<Double> read(int number) {
		return master
				.sendRequest(new ReadInputRegistersRequest(
						readAddressOffset + 1 + 2 * number, 1), 0)
				.thenApply(res -> {
					ReadInputRegistersResponse response = (ReadInputRegistersResponse) res;

					int result = response.getRegisters().getShort(0);
					return convertRegisterValueToVoltage(number, result);
				});
	}

	@Override
	public CompletableFuture<Void> set(int number, double value) {
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

	// private void activateUserScaling() {
	// configurator.deactivateReadOnly().thenCompose((voidValue) -> {
	// return configurator.readValuteFromConfigRegister(32);
	// }).thenCompose(value -> {
	//
	// return configurator.writeValueToConfigRegister(32, (value | 1));
	//
	// });
	// }
	//
	// private void deactivateUserScaling() {
	// configurator.deactivateReadOnly().thenCompose((voidValue) -> {
	// return configurator.readValuteFromConfigRegister(32);
	// }).thenCompose(value -> {
	//
	// return configurator.writeValueToConfigRegister(32, (value & ~1));
	//
	// });
	// }
	//
	// private void activateUserSwitchOnValue() {
	// configurator.deactivateReadOnly().thenCompose((voidValue) -> {
	// return configurator.readValuteFromConfigRegister(32);
	// }).thenCompose(value -> {
	//
	// return configurator.writeValueToConfigRegister(32,
	// (value | (1 << 8)));
	//
	// });
	// }
	//
	// private void deactivateUserSwitchOnValue() {
	// configurator.deactivateReadOnly().thenCompose((voidValue) -> {
	// return configurator.readValuteFromConfigRegister(32);
	// }).thenCompose(value -> {
	//
	// return configurator.writeValueToConfigRegister(32,
	// (value & ~(1 << 8)));
	//
	// });
	// }

	@Override
	public int outputs() {
		return OUTPUTS;
	}

}
