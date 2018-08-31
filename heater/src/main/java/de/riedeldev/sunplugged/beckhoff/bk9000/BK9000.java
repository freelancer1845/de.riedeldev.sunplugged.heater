package de.riedeldev.sunplugged.beckhoff.bk9000;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.digitalpetri.modbus.requests.WriteSingleRegisterRequest;
import com.digitalpetri.modbus.responses.ModbusResponse;

import de.riedeldev.sunplugged.beckhoff.klspi.AnalogInputKlemme;
import de.riedeldev.sunplugged.beckhoff.klspi.AnalogOutputKlemme;
import de.riedeldev.sunplugged.beckhoff.klspi.DigitalInputKlemme;
import de.riedeldev.sunplugged.beckhoff.klspi.DigitalOutputKlemme;
import de.riedeldev.sunplugged.beckhoff.klspi.Klemme;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BK9000 {

	private static final int ANALOG_OUTPUT_OFFSET = 0x0800 ;

	private final List<Klemme> klemmen;
	private final List<DigitalInputKlemme> diKlemmen;
	private final List<DigitalOutputKlemme> doKlemmen;
	private final List<AnalogInputKlemme> aiKlemmen;
	private final List<AnalogOutputKlemme> aoKlemmen;

	private ModbusTcpMaster master;

	private BK9000(List<Klemme> klemmen) {
		this.klemmen = klemmen;

		List<Klemme> duplicateClamps = this.klemmen.stream()
				.collect(Collectors.groupingBy(klemme -> klemme.getId()))
				.values().stream().filter(list -> list.size() > 1).findAny()
				.orElse(null);
		if (duplicateClamps != null) {
			StringBuilder builder = new StringBuilder();
			builder.append("There were clamps with same ids: ");
			duplicateClamps
					.forEach(clamp -> builder.append(clamp.getId() + "; "));
			throw new IllegalArgumentException(builder.toString());
		}

		this.diKlemmen = klemmen.stream()
				.filter(klemme -> klemme instanceof DigitalInputKlemme)
				.map(k -> (DigitalInputKlemme) k).collect(Collectors.toList());
		this.doKlemmen = klemmen.stream()
				.filter(k -> k instanceof DigitalOutputKlemme)
				.map(k -> (DigitalOutputKlemme) k).collect(Collectors.toList());

		this.aiKlemmen = klemmen.stream()
				.filter(klemme -> klemme instanceof AnalogInputKlemme)
				.map(klemme -> (AnalogInputKlemme) klemme)
				.collect(Collectors.toList());
		this.aoKlemmen = klemmen.stream()
				.filter(klemme -> klemme instanceof AnalogOutputKlemme)
				.map(klemme -> (AnalogOutputKlemme) klemme)
				.collect(Collectors.toList());

	}

	public void connect(String host, int port) {
		ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder(host)
				.setPort(port).build();
		ModbusTcpMaster master = new ModbusTcpMaster(config);
		klemmen.forEach(k -> k.setTcpMaster(master));

		int digitalInputOffset = 0;
		for (DigitalInputKlemme k : diKlemmen) {
			k.setInputOffset(digitalInputOffset);
			digitalInputOffset += k.addressSpaceInput();
		}
		for (DigitalOutputKlemme k : doKlemmen) {
			k.setInputOffset(digitalInputOffset);
			digitalInputOffset += k.addressSpaceInput();
		}

		int digitalOutputOffset = 0;
		for (DigitalOutputKlemme k : doKlemmen) {
			k.setOutputOffset(digitalOutputOffset);
			digitalOutputOffset += k.addressSpaceOutput();
		}

		int analogInputSpaceOffset = 0;
		int analogOutputSpaceOffset = ANALOG_OUTPUT_OFFSET;
		for (AnalogInputKlemme k : aiKlemmen) {
			k.setInputOffset(analogInputSpaceOffset);
			k.setOutputOffset(analogOutputSpaceOffset);
			analogInputSpaceOffset += k.addressSpaceInput();
			analogOutputSpaceOffset += k.addressSpaceOutput();
		}
//
//		for (AnalogOutputKlemme k : aoKlemmen) {
//			k.setOutputOffset(analogOutputSpaceOffset);
//			k.setInputOffset(analogInputSpaceOffset);
//			analogOutputSpaceOffset += k.addressSpaceOutput();
//			analogInputSpaceOffset += k.addressSpaceInput();
//		}
		master.connect();
		this.master = master;
	}
	
	public CompletableFuture<ModbusTcpMaster> disconnect() {
		return master.disconnect();
	}

	public CompletableFuture<Void> setDigitalOutput(int number, boolean value) {
		for (DigitalOutputKlemme k : doKlemmen) {
			if (k.addressSpaceOutput() < number) {
				number = number - k.addressSpaceOutput();
			} else {
				return k.set(number, value);
			}
		}
		throw new IllegalArgumentException(
				"No Output with number " + number + " attached...");
	}

	public CompletableFuture<Boolean> readDigitalOutput(int number) {
		for (DigitalOutputKlemme k : doKlemmen) {
			if (k.addressSpaceOutput() < number) {
				number = number - k.addressSpaceOutput();
			} else {
				return k.read(number);
			}
		}
		throw new IllegalArgumentException(
				"No Output with number " + number + " attached...");
	}

	public CompletableFuture<Boolean> readDigitalInput(int number) {
		for (DigitalInputKlemme k : diKlemmen) {
			if (k.addressSpaceInput() < number) {
				number = number - k.addressSpaceInput();
			} else {
				return k.read(number);
			}
		}
		throw new IllegalArgumentException(
				"No Input with number " + number + " attached...");
	}

	public CompletableFuture<Void> writeAnalogOutput(int number, double value) {

		for (AnalogOutputKlemme klemme : aoKlemmen) {
			if (klemme.outputs() < number) {
				number = number - klemme.outputs();
			} else {
				return klemme.set(number, value);
			}
		}
		throw new IllegalArgumentException(
				"No output with number " + number + " attached...");
	}

	public CompletableFuture<Double> readAnalogOutput(int number) {

		for (AnalogInputKlemme klemme : aiKlemmen) {
			if ((klemme instanceof AnalogOutputKlemme) == false) {
				continue;
			}
			AnalogOutputKlemme oKlemme = (AnalogOutputKlemme) klemme;
			if (oKlemme.outputs() <= number) {
				number = number - oKlemme.outputs();
			} else {
				return oKlemme.read(number);
			}
		}
		throw new IllegalArgumentException(
				"No output with number " + number + " attached...");
	}

	public CompletableFuture<Double> readAnalogInput(int number) {
		for (AnalogInputKlemme k : aiKlemmen) {
			if (k instanceof AnalogOutputKlemme) {
				continue;
			}
			if (k.inputs() <= number) {
				number = number - k.inputs();
			} else {
				return k.read(number);
			}
		}
		throw new IllegalArgumentException(
				"No input with number " + number + " attached...");
	}

	public void resetWatchDog() {
		if (master != null) {
			try {
				master.sendRequest(
						new WriteSingleRegisterRequest(0x1121, 0xBECF), 1)
						.get();
				master.sendRequest(
						new WriteSingleRegisterRequest(0x1121, 0xAFFE), 1)
						.get();
			} catch (InterruptedException | ExecutionException e) {
				log.error("Failed to reset Watchdog!", e);
			}
		}

	}
	
	public CompletableFuture<ModbusResponse> setWatchDogTime(int ms) {
		return master.sendRequest(new WriteSingleRegisterRequest(0x1120, ms), 1);
	}

	public Klemme getClamp(String id) {
		return klemmen.stream().filter(a -> a.getId().equals(id)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
						"Clamp with that not present"));
	}

	public static class BK9000Builder {

		List<Klemme> klemmen = new ArrayList<>();

		public BK9000Builder with(Klemme klemme) {
			this.klemmen.add(klemme);
			return this;
		}

		public BK9000 build() {
			return new BK9000(klemmen);
		}
	}

}
