package de.riedeldev.sunplugged.beckhoff.klspi;

import java.util.concurrent.CompletableFuture;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.requests.ReadInputRegistersRequest;
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest;
import com.digitalpetri.modbus.requests.WriteSingleRegisterRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import com.digitalpetri.modbus.responses.ReadInputRegistersResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Configurator {

	public static long CONTROLLER_WAIT_TIME = 100;

	private ModbusTcpMaster master;
	private int statusRegister;
	private int controlRegister;

	private boolean isused = false;

	public Configurator(ModbusTcpMaster master, int statusRegister,
			int controlRegister) {
		this.master = master;
		this.statusRegister = statusRegister;
		this.controlRegister = controlRegister;
	}

	public CompletableFuture<Void> activateReadOnly() {
		return writeValueToConfigRegister(31, 0);
	}

	public CompletableFuture<Void> deactivateReadOnly() {
		return writeValueToConfigRegister(31, 0x1235);
	}

	public CompletableFuture<Void> writeValueToConfigRegister(int register,
			int value) {

		if (isused == true) {
			synchronized (this) {
				try {
					this.wait(2000);
				} catch (InterruptedException e) {
				}
				if (isused == true) {
					throw new IllegalStateException(
							"Thread woken but configurator is still in use!");
				}
				isused = true;
			}

		}

		ByteBuf values = Unpooled.buffer();
		int controlByte = (register | 0b11000000);
		values.writeShort(controlByte);
		values.writeShort(value);

		return master.sendRequest(
				new WriteMultipleRegistersRequest(controlRegister, 2, values),
				1).handle((res, ex) -> {
					if (ex != null) {
						synchronized (this) {
							isused = false;
							this.notify();
						}
						log.error(
								"Failed to write register in configurator. Address: "
										+ controlRegister + " Value: " + value);
						throw new IllegalStateException(
								"Failed to write register.");
					} else {
						return res;
					}
				}).thenAcceptAsync(res -> {
					try {
						// Give controller some time to respond
						Thread.sleep(CONTROLLER_WAIT_TIME);
					} catch (InterruptedException e) {
						log.error("Configuration has been interrupted.", e);
						isused = false;
						this.notify();
						return;
					}
					master.sendRequest(
							new ReadInputRegistersRequest(statusRegister, 1), 1)
							.handleAsync((res2, ex) -> {
								synchronized (this) {
									isused = false;
									this.notify();
								}
								if (ex != null) {
									log.error(
											"Failed to read status Register after write.");
									return Void.TYPE;
								}
								ReadInputRegistersResponse response = (ReadInputRegistersResponse) res2;
								byte answer = response.getRegisters()
										.readByte();
								boolean registerAccessAck = ((answer >> 7) == 1);

								byte registerWritten = ((byte) (answer
										& ~(0b1110000)));

								if (registerAccessAck == true
										&& (registerWritten == register)) {
									return Void.TYPE;
								} else {
									log.error("Failed to writer Register: "
											+ register);
									return Void.TYPE;
								}

							});

				});
		// return master.sendRequest(
		// new WriteMultipleRegistersRequest(controlRegister, 2, values),
		// 0).handleAsync((res, ex) -> {
		// if (ex != null) {
		// log.error(
		// "Failed to write register in configurator. Address: "
		// + controlRegister + " Value: " + value);
		// throw new IllegalStateException(
		// "Failed to write register.");
		// } else {
		// ReferenceCountUtil.release(values);
		// return master
		// .sendRequest(new ReadInputRegistersRequest(
		// statusRegister, 1), 0)
		// .thenAccept(res -> {}));
		// }
		//
		// }).thenAccept((res) -> {
		// });
	}

	public CompletableFuture<Integer> readValuteFromConfigRegister(
			int register) {
		ByteBuf values = Unpooled.buffer();
		int controlByte = (register | 0b10000000);
		values.writeShort(controlByte);

		if (isused == true) {
			synchronized (this) {
				try {
					this.wait(2000);
				} catch (InterruptedException e) {
				}
				if (isused == true) {
					throw new IllegalStateException(
							"Thread woken but configurator is still in use!");
				}
				isused = true;
			}
		}

		return master.sendRequest(
				new WriteSingleRegisterRequest(controlRegister, controlByte), 1)
				.thenCompose(res -> {

					try {
						Thread.sleep(CONTROLLER_WAIT_TIME);
					} catch (InterruptedException e) {
						log.error("Interrupted while waiting for controller.");
						isused = false;
						this.notify();
						throw new IllegalStateException(
								"Interrupted while waiting for bus answer.");
					}

					return master
							.sendRequest(new ReadHoldingRegistersRequest(
									controlRegister + 1, 1), 0)
							.handle((res2, ex) -> {

								synchronized (this) {
									isused = false;
									this.notify();
								}

								if (ex != null) {
									log.error(
											"Error reading back the register value.");
								}
								ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) res2;
								int value = (int) response.getRegisters()
										.readShort();
								return value;
							});
				});
	}

	public void switchOffRegisterCommunication() {
		master.sendRequest(new WriteSingleRegisterRequest(controlRegister, 0),
				1);
	}

}
