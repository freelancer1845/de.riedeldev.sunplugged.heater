package de.riedeldev.sunplugged.beckhoff.kl1xxx;

import java.util.concurrent.CompletableFuture;

import com.digitalpetri.modbus.requests.ReadDiscreteInputsRequest;
import com.digitalpetri.modbus.responses.ReadDiscreteInputsResponse;

import de.riedeldev.sunplugged.beckhoff.klspi.AbstractClamp;
import de.riedeldev.sunplugged.beckhoff.klspi.DigitalInputKlemme;

public class KL1104 extends AbstractClamp implements DigitalInputKlemme {

	public KL1104(String id) {
		super(0, 4, id);
	}

	@Override
	public CompletableFuture<Boolean> read(int number) {

		return master.sendRequest(
				new ReadDiscreteInputsRequest(readAddressOffset + number, 1), 1)
				.thenApply(res -> {
					ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) res;
					byte status = response.getInputStatus().readByte();

					return ((status) & (0x01)) != 0;
				});
	}

}
