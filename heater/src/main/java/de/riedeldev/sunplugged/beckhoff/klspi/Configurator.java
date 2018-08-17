package de.riedeldev.sunplugged.beckhoff.klspi;

import java.util.concurrent.CompletableFuture;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest;
import com.digitalpetri.modbus.requests.WriteSingleRegisterRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

public class Configurator {

	private ModbusTcpMaster master;
	private int addressControl;

	public Configurator(ModbusTcpMaster master, int addressControl) {
		this.master = master;
		this.addressControl = addressControl;
	}

	public CompletableFuture<Void> activateReadOnly() {
		return writeValueToConfigRegister(31, 0);
	}

	public CompletableFuture<Void> deactivateReadOnly() {
		return writeValueToConfigRegister(31, 0x1235);
	}

	public CompletableFuture<Void> writeValueToConfigRegister(int register,
			int value) {
		ByteBuf values = Unpooled.buffer();
		int controlByte = (register | 0x11000000);
		values.writeShort(controlByte);
		values.writeShort(value);
		return master
				.sendRequest(new WriteMultipleRegistersRequest(addressControl,
						2, values), 0)
				.thenAccept(res -> ReferenceCountUtil.release(values));
	}

	public CompletableFuture<Integer> readValuteFromConfigRegister(
			int register) {
		ByteBuf values = Unpooled.buffer();
		int controlByte = (register | 0x10000000);
		values.writeShort(controlByte);
		return master.sendRequest(
				new WriteSingleRegisterRequest(addressControl, controlByte), 0)
				.thenCompose(res -> {
					return master
							.sendRequest(new ReadHoldingRegistersRequest(
									addressControl + 1, 1), 0)
							.thenApply(res2 -> {
								ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) res2;
								int value = (int) response.getRegisters()
										.readShort();
								return value;
							});
				});
	}

	public void switchOffRegisterCommunication() {
		master.sendRequest(new WriteSingleRegisterRequest(addressControl, 0),
				0);
	}

}
