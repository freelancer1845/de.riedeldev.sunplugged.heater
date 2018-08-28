package de.riedeldev.sunplugged.beckhoff.kl3xxx;

import java.util.concurrent.CompletableFuture;

import com.digitalpetri.modbus.requests.ReadInputRegistersRequest;
import com.digitalpetri.modbus.responses.ReadInputRegistersResponse;

import de.riedeldev.sunplugged.beckhoff.klspi.AbstractClamp;
import de.riedeldev.sunplugged.beckhoff.klspi.AnalogInputKlemme;
import de.riedeldev.sunplugged.beckhoff.klspi.Configurator;

public class KL3064 extends AbstractClamp implements AnalogInputKlemme {

	private static final int ADDRESS_SPACE_SIZE = 4;

	private Double[] maxValueForInput = {10.0, 10.0, 10.0, 10.0};

	private Configurator configurator = new Configurator(master,
			writeAddressOffset);

	public KL3064(String id) {
		super(ADDRESS_SPACE_SIZE, ADDRESS_SPACE_SIZE, id);
	}

	public Configurator getConfigurator(int input) {
		return new Configurator(master, writeAddressOffset + (2 * input));
	}

	@Override
	public CompletableFuture<Double> read(int number) {
		return master
				.sendRequest(new ReadInputRegistersRequest(
						readAddressOffset + number + 1, 1), 0)
				.thenApply(res -> {
					ReadInputRegistersResponse response = (ReadInputRegistersResponse) res;
					int value = response.getRegisters().getShort(0);
					return convertRegisterValueToVoltage(number, value);
				});
	}

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
	//
	private double convertRegisterValueToVoltage(int number, int value) {
		return (((double) value / (double) Short.MAX_VALUE)
				* maxValueForInput[number]);
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
	public int inputs() {
		return 4;
	}

}
