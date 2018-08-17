package de.riedeldev.sunplugged.beckhoff.bk9000;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;

import de.riedeldev.sunplugged.beckhoff.klspi.AnalogOutputKlemme;
import de.riedeldev.sunplugged.beckhoff.klspi.BitKlemme;
import de.riedeldev.sunplugged.beckhoff.klspi.ByteKlemme;

public class BK9000 {

	private final List<BitKlemme> bitKlemmen;
	private final List<ByteKlemme> byteKlemmen;

	private BK9000(List<BitKlemme> bitKlemmen, List<ByteKlemme> byteKlemmen) {
		this.bitKlemmen = bitKlemmen;
		this.byteKlemmen = byteKlemmen;
	}

	public void connect(String host, int port) {
		ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder(host)
				.setPort(port).build();
		ModbusTcpMaster master = new ModbusTcpMaster(config);

		int readAddressOffset = 0;
		int writeAddressOffset = 0;

		for (BitKlemme klemme : bitKlemmen) {
			klemme.attach(readAddressOffset, writeAddressOffset, master);
			readAddressOffset += klemme.bitsNeededInRead();
			writeAddressOffset += klemme.bitsNeededInWrite();
		}
		readAddressOffset = 0;
		writeAddressOffset = 0;
		for (ByteKlemme klemme : byteKlemmen) {
			klemme.attach(readAddressOffset, writeAddressOffset, master);
			readAddressOffset += klemme.addressesNeededInRead();
			writeAddressOffset += klemme.addressesNeededInWrite();
		}
	}

	public CompletableFuture<Void> writeAnalogOutput(int number, double value) {
		List<AnalogOutputKlemme> outputKlemmen = byteKlemmen.stream()
				.filter(klemme -> klemme instanceof AnalogOutputKlemme)
				.map(klemme -> (AnalogOutputKlemme) klemme)
				.collect(Collectors.toList());

		for (AnalogOutputKlemme klemme : outputKlemmen) {
			if (klemme.outputs() < number) {
				number = number - klemme.outputs();
			} else {
				return klemme.setOutput(number, value);
			}
		}
		throw new IllegalArgumentException(
				"No output with number " + number + " attached...");
	}

	public CompletableFuture<Double> readAnalogOutput(int number) {
		List<AnalogOutputKlemme> outputKlemmen = byteKlemmen.stream()
				.filter(klemme -> klemme instanceof AnalogOutputKlemme)
				.map(klemme -> (AnalogOutputKlemme) klemme)
				.collect(Collectors.toList());

		for (AnalogOutputKlemme klemme : outputKlemmen) {
			if (klemme.outputs() < number) {
				number = number - klemme.outputs();
			} else {
				return klemme.readOutput(number);
			}
		}
		throw new IllegalArgumentException(
				"No output with number " + number + " attached...");
	}

	public static class BK9000Builder {

		List<BitKlemme> bitKlemmen = new ArrayList<>();
		List<ByteKlemme> byteKlemmen = new ArrayList<>();

		public BK9000Builder with(BitKlemme klemme) {
			this.bitKlemmen.add(klemme);
			return this;
		}

		public BK9000Builder with(ByteKlemme klemme) {
			this.byteKlemmen.add(klemme);
			return this;
		}

		public BK9000 build() {
			return new BK9000(bitKlemmen, byteKlemmen);
		}
	}

}
