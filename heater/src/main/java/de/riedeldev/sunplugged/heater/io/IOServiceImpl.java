package de.riedeldev.sunplugged.heater.io;

import java.util.concurrent.ExecutionException;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

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
import de.riedeldev.sunplugged.heater.config.WebSocketConfig.Topics;
import de.riedeldev.sunplugged.heater.io.IOServiceEvent.Type;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

@Service
@Controller
@Slf4j
public class IOServiceImpl implements IOService {

	private static final int MAX_DIGITAL_INPUTS = 8;

	private static final int MAX_ANALOG_INPUTS = 4;

	private ApplicationEventPublisher publisher;

	@Autowired
	private SimpMessagingTemplate template;

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

	private boolean[] previousDI = new boolean[MAX_DIGITAL_INPUTS];
	private int[] previousAI = new int[MAX_ANALOG_INPUTS];

	@Scheduled(fixedRate = 100)
	public void publishInputValues() {
		if (master != null) {
			master.sendRequest(
					new ReadDiscreteInputsRequest(0, MAX_DIGITAL_INPUTS), 0)
					.thenAccept(res -> {
						ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) res;
						ByteBuf buf = response.getInputStatus();
						int currentByte = 0;
						int bitsRead = 0;
						byte ans = buf.getByte(currentByte);
						for (int i = 0; i < MAX_DIGITAL_INPUTS; i++) {
							if (bitsRead == 8) {
								bitsRead = 0;
								currentByte++;
								ans = buf.getByte(currentByte);
							}
							boolean value = ((ans) & (0x01 << bitsRead)) != 0;
							if (previousDI[i] != value) {
								String path = UriComponentsBuilder
										.fromPath("/topic").path(Topics.DI)
										.buildAndExpand(i).toString();
								template.convertAndSend(path, value);
								previousDI[i] = value;
							}
							bitsRead++;

						}
					});
			master.sendRequest(
					new ReadInputRegistersRequest(0, MAX_ANALOG_INPUTS), 0)
					.thenAccept(res -> {
						ReadInputRegistersResponse response = (ReadInputRegistersResponse) res;
						ByteBuf buf = response.getRegisters();
						for (int i = 0; i < MAX_ANALOG_INPUTS; i++) {
							int value = buf.readUnsignedShort();
							if (previousAI[i] != value) {
								String path = UriComponentsBuilder
										.fromPath("/topic").path(Topics.AI)
										.buildAndExpand(i).toString();
								template.convertAndSend(path, value);
								previousAI[i] = value;
							}
						}
					});
		}
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
	@MessageMapping(Topics.DO_ACCESS)
	public void setDO(@DestinationVariable int address, boolean value)
			throws IOServiceException {
		checkConnectionOrThrowError();
		master.sendRequest(new WriteSingleCoilRequest(address, value), 0)
				.thenAcceptAsync(res -> {
					template.convertAndSend("/topic/doaccess/" + address,
							value);
				});;
	}

	@Override
	@MessageMapping(Topics.DO_ACCESS + "/get")
	@SendTo("/topic" + Topics.DO_ACCESS)
	public boolean getDO(@DestinationVariable int address)
			throws IOServiceException {
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
	@MessageMapping(Topics.DI + "/get")
	@SendTo("/topic" + Topics.DI)
	public boolean getDI(@DestinationVariable int address)
			throws IOServiceException {
		if (address < 20) {
			return previousDI[address];
		} else {
			checkConnectionOrThrowError();
			try {
				return master
						.sendRequest(new ReadDiscreteInputsRequest(address, 1),
								0)
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

	}

	@Override
	@MessageMapping(Topics.AO_ACCESS)
	public void setAO(@DestinationVariable int address, int value)
			throws IOServiceException {
		checkConnectionOrThrowError();
		master.sendRequest(new WriteSingleRegisterRequest(address, value), 0)
				.thenAcceptAsync(res -> {
					template.convertAndSend("/topic/aoaccess/" + address,
							value);
				});

	}

	@Override
	@MessageMapping(Topics.AO_ACCESS + "/get")
	@SendTo("/topic" + Topics.AO_ACCESS)
	public int getAO(@DestinationVariable int address)
			throws IOServiceException {
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
	@MessageMapping(Topics.AI + "/get")
	@SendTo("/topic" + Topics.AI)
	public int getAI(@DestinationVariable int address)
			throws IOServiceException {
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
