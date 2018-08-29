package de.riedeldev.sunplugged.beckhoff.kl3xxx;

import java.util.concurrent.CompletableFuture;

import com.digitalpetri.modbus.requests.ReadInputRegistersRequest;
import com.digitalpetri.modbus.responses.ReadInputRegistersResponse;

import de.riedeldev.sunplugged.beckhoff.klspi.AbstractClamp;
import de.riedeldev.sunplugged.beckhoff.klspi.AnalogInputKlemme;
import de.riedeldev.sunplugged.beckhoff.klspi.Configurator;

public class KL3312 extends AbstractClamp implements AnalogInputKlemme {

	private static final int INPUTS = 2;
	private static final int ADDRESS_SPACE_SIZE = 2 * INPUTS;

	public KL3312(String id) {
		super(ADDRESS_SPACE_SIZE, ADDRESS_SPACE_SIZE, id);
	}

	public Configurator getConfigurator(int input) {
		if (master == null) {
			throw new IllegalStateException("Not attached! TcpMaster was null");
		}
		return new Configurator(master, readAddressOffset + (2 * input),
				writeAddressOffset + (2 * input));
	}

	@Override
	public CompletableFuture<Double> read(int number) {
		return master
				.sendRequest(new ReadInputRegistersRequest(
						readAddressOffset + 2 * number + 1, 1), 1)
				.thenApply(res -> {
					ReadInputRegistersResponse response = (ReadInputRegistersResponse) res;
					int value = response.getRegisters().getShort(0);
					double temp = value / 10.0;
					return temp;
				});
	}

	@Override
	public int inputs() {
		return INPUTS;
	}

}
