package de.riedeldev.sunplugged.heater.io;

import java.util.concurrent.ExecutionException;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.digitalpetri.modbus.requests.ReadCoilsRequest;
import com.digitalpetri.modbus.requests.ReadDiscreteInputsRequest;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.requests.ReadInputRegistersRequest;
import com.digitalpetri.modbus.requests.WriteSingleCoilRequest;
import com.digitalpetri.modbus.requests.WriteSingleRegisterRequest;
import com.digitalpetri.modbus.responses.ReadCoilsResponse;
import com.digitalpetri.modbus.responses.ReadDiscreteInputsResponse;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import com.digitalpetri.modbus.responses.ReadInputRegistersResponse;

import de.riedeldev.sunplugged.heater.config.Parameters;
import de.riedeldev.sunplugged.heater.io.IOServiceEvent.Type;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IOServiceImpl implements IOService {

	private ApplicationEventPublisher publisher;

	private ModbusTcpMaster master = null;

	private Type state = Type.DISCONNECTED;

	private Throwable lastException = null;

	private Object connectingWaitLock = new Object();

	private Parameters parameters;

	@Autowired
	public IOServiceImpl(Parameters parameters,
			ApplicationEventPublisher publisher) {
		this.publisher = publisher;
		this.parameters = parameters;
	}

	@PreDestroy
	protected void preDestroy() {
		disconnectedFromModubsTCP();
	}

	private void connectoToModbusTCP(Parameters parameters) {
		ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder(
				parameters.getModbus_host())
						.setPort(parameters.getModbus_port()).build();
		ModbusTcpMaster tcpMaster = new ModbusTcpMaster(config);

		log.debug("Connecting to modbus...");
		publisher.publishEvent(new IOServiceEvent(this, Type.CONNECTING, null));
		tcpMaster.connect().whenComplete(this::handleConnect);
	}

	private void disconnectedFromModubsTCP() {
		if (master != null) {
			master.disconnect().whenComplete(this::handleDisconnect);
		}
	}

	private void handleConnect(ModbusTcpMaster tcpMaster, Throwable ex) {
		if (ex != null) {
			publisher.publishEvent(new IOServiceEvent(this, Type.ERROR, ex));
		} else {
			this.master = tcpMaster;
			publisher.publishEvent(
					new IOServiceEvent(this, Type.CONNECTED, null));
		}
	}

	private void handleDisconnect(ModbusTcpMaster tcpmaster, Throwable ex) {
		if (ex != null) {
			publisher.publishEvent(new IOServiceEvent(this, Type.ERROR, ex));
		} else {
			this.master = null;
			publisher.publishEvent(
					new IOServiceEvent(this, Type.DISCONNECTED, null));
		}
	}

	@Override
	public void setDO(int address, boolean value) throws IOServiceException {
		checkConnectionOrThrowError();
		master.sendRequest(new WriteSingleCoilRequest(address, value), 0);
	}

	@Override
	public boolean getDO(int address) throws IOServiceException {
		checkConnectionOrThrowError();
		try {
			return master.sendRequest(new ReadCoilsRequest(address, 1), 0)
					.handle((res, ex) -> {
						ByteBuf buf = ((ReadCoilsResponse) res).getCoilStatus();
						byte ans = buf.getByte(0);
						return (ans & 0x0000001) == 1;
					}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IOServiceException(e);
		}
	}

	@Override
	public boolean getDI(int address) throws IOServiceException {
		checkConnectionOrThrowError();
		try {
			return master
					.sendRequest(new ReadDiscreteInputsRequest(address, 1), 0)
					.handle((res, ex) -> {
						ByteBuf buf = ((ReadDiscreteInputsResponse) res)
								.getInputStatus();
						byte ans = buf.getByte(0);
						return (ans & 0x0000001) == 1;
					}).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IOServiceException(e);
		}
	}

	@Override
	public void setAO(int address, int value) throws IOServiceException {
		checkConnectionOrThrowError();
		master.sendRequest(new WriteSingleRegisterRequest(address, value), 0);
	}

	@Override
	public int getAO(int address) throws IOServiceException {
		checkConnectionOrThrowError();
		try {
			return master
					.sendRequest(new ReadHoldingRegistersRequest(address, 1), 0)
					.handle((res, ex) -> {
						ByteBuf buf = ((ReadHoldingRegistersResponse) res)
								.getRegisters();

						return buf.getUnsignedShort(0);
					}).get();

		} catch (InterruptedException | ExecutionException e) {
			throw new IOServiceException(e);
		}
	}

	@Override
	public int getAI(int address) throws IOServiceException {
		checkConnectionOrThrowError();
		try {
			return master
					.sendRequest(new ReadInputRegistersRequest(address, 1), 0)
					.handle((res, ex) -> {
						ByteBuf buf = ((ReadInputRegistersResponse) res)
								.getRegisters();

						return buf.getUnsignedShort(0);
					}).get();

		} catch (InterruptedException | ExecutionException e) {
			throw new IOServiceException(e);
		}
	}

	private void checkConnectionOrThrowError() throws IOServiceException {
		if (state == Type.CONNECTING) {
			synchronized (connectingWaitLock) {
				try {
					log.debug("Waiting for connecting...");
					connectingWaitLock.wait();
				} catch (InterruptedException e) {
					throw new IOServiceException(
							"Interrupted while waiting for Connecting finish",
							e);
				}
			}
		}
		if (master == null) {
			throw new IOServiceException("Connection not open.",
					getLastException());
		}
	}

	@EventListener(classes = IOServiceEvent.class)
	public void newIOServiceEvent(IOServiceEvent event) {
		if (this.state == Type.CONNECTING
				&& event.getType() != Type.CONNECTING) {
			synchronized (connectingWaitLock) {
				connectingWaitLock.notifyAll();
			}
		}
		this.state = event.getType();
		log.debug("New IOService State: " + state.name());
		if (state == Type.ERROR) {
			this.lastException = (Throwable) event.getPayload();
		}
	}

	@EventListener(classes = ApplicationReadyEvent.class)
	public void applicationReady(ApplicationReadyEvent event) {

		connectoToModbusTCP(
				event.getApplicationContext().getBean(Parameters.class));
		// connectoToModbusTCP(parameters);
	}

	public Throwable getLastException() {
		return lastException;
	}

}
