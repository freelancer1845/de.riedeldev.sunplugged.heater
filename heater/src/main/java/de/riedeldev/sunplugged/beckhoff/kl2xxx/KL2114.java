package de.riedeldev.sunplugged.beckhoff.kl2xxx;

import java.util.concurrent.CompletableFuture;

import com.digitalpetri.modbus.requests.ReadCoilsRequest;
import com.digitalpetri.modbus.requests.WriteSingleCoilRequest;
import com.digitalpetri.modbus.responses.ReadCoilsResponse;

import de.riedeldev.sunplugged.beckhoff.klspi.AbstractClamp;
import de.riedeldev.sunplugged.beckhoff.klspi.DigitalOutputKlemme;

public class KL2114 extends AbstractClamp implements DigitalOutputKlemme {

	public KL2114(String id) {
		super(4, 0, id);
	}

	@Override
	public CompletableFuture<Boolean> read(int number) {

		return master
				.sendRequest(
						new ReadCoilsRequest(number + writeAddressOffset, 1), 0)
				.thenApply(res -> {
					ReadCoilsResponse response = (ReadCoilsResponse) res;
					byte status = response.getCoilStatus().readByte();

					return ((status) & (0x01)) != 0;
				});
	}

	@Override
	public CompletableFuture<Void> set(int number, boolean value) {
		return master
				.sendRequest(new WriteSingleCoilRequest(
						number + writeAddressOffset, value), 0)
				.thenAccept((res) -> {
				});

	}

}
