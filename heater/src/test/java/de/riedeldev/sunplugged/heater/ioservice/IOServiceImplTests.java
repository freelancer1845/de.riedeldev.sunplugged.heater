package de.riedeldev.sunplugged.heater.ioservice;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;

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
import com.digitalpetri.modbus.responses.WriteSingleCoilResponse;
import com.digitalpetri.modbus.responses.WriteSingleRegisterResponse;
import com.digitalpetri.modbus.slave.ModbusTcpSlave;
import com.digitalpetri.modbus.slave.ModbusTcpSlaveConfig;
import com.digitalpetri.modbus.slave.ServiceRequestHandler;

import de.riedeldev.sunplugged.heater.config.Parameters;
import de.riedeldev.sunplugged.heater.io.IOServiceEvent;
import de.riedeldev.sunplugged.heater.io.IOServiceException;
import de.riedeldev.sunplugged.heater.io.IOServiceImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.ReferenceCountUtil;

public class IOServiceImplTests {

	private IOServiceImpl service;

	private ModbusTcpSlave slave;

	@Before
	public void beforeTest() throws InterruptedException, ExecutionException {
		slave = getSlave();

		ApplicationEventPublisher publisher = mock(
				ApplicationEventPublisher.class);
		Parameters parameters = new Parameters();
		service = new IOServiceImpl(new Parameters(), publisher);

		doAnswer((InvocationOnMock invo) -> {
			service.newIOServiceEvent(invo.getArgument(0));
			return Void.TYPE;
		}).when(publisher).publishEvent(any(IOServiceEvent.class));

		ApplicationReadyEvent event = mock(ApplicationReadyEvent.class);

		ConfigurableApplicationContext context = mock(
				ConfigurableApplicationContext.class);
		when(context.getBean(Parameters.class)).thenReturn(parameters);
		when(event.getApplicationContext())
				.thenReturn((ConfigurableApplicationContext) context);

		service.applicationReady(event);
	}

	@After
	public void afterTest() {
		slave.shutdown();
	}

	@Test
	public void writesCorrectCoil() throws InterruptedException,
			ExecutionException, IOServiceException {

		int testAddress = new Random().nextInt(3000);
		Thread.sleep(1000);
		List<Integer> calls = new ArrayList<>();

		slave.setRequestHandler(new ServiceRequestHandler() {
			@Override
			public void onWriteSingleCoil(
					ServiceRequest<WriteSingleCoilRequest, WriteSingleCoilResponse> service) {
				calls.add(service.getRequest().getAddress());
			}
		});
		service.setDO(testAddress, true);
		Thread.sleep(200);
		assertEquals("Expected one write request", 1, calls.size());
		assertEquals("Wrong address written.", (Integer) testAddress,
				(Integer) calls.get(0));
	}

	@Test
	public void readsCoils() throws InterruptedException, ExecutionException,
			IOServiceException {

		int coilAddress = new Random().nextInt(2000);
		boolean answer = new Random().nextBoolean();

		slave.setRequestHandler(new ServiceRequestHandler() {

			@Override
			public void onReadCoils(
					ServiceRequest<ReadCoilsRequest, ReadCoilsResponse> service) {
				int address = service.getRequest().getAddress();
				assertEquals("Read Coil request carried wrong address.",
						coilAddress, address);
				ByteBuf coilStatus = PooledByteBufAllocator.DEFAULT
						.buffer(service.getRequest().getQuantity());
				coilStatus.writeByte(answer ? 1 : 0);
				service.sendResponse(new ReadCoilsResponse(coilStatus));
				ReferenceCountUtil.release(service.getRequest());
			}
		});

		boolean sendAnswer = service.getDO(coilAddress);
		assertEquals("Answers did not equal", answer, sendAnswer);
	}

	@Test
	public void readsDigitalInput() throws IOServiceException {
		int inputAddress = new Random().nextInt(2000);
		boolean answer = new Random().nextBoolean();

		slave.setRequestHandler(new ServiceRequestHandler() {

			@Override
			public void onReadDiscreteInputs(
					ServiceRequest<ReadDiscreteInputsRequest, ReadDiscreteInputsResponse> service) {
				ReadDiscreteInputsRequest request = service.getRequest();
				assertEquals("DiscreteInput address wrong",
						request.getAddress(), inputAddress);

				ByteBuf inputStatus = PooledByteBufAllocator.DEFAULT
						.buffer(service.getRequest().getQuantity());
				inputStatus.writeByte(answer ? 1 : 0);
				service.sendResponse(
						new ReadDiscreteInputsResponse(inputStatus));
				ReferenceCountUtil.release(request);
			}
		});

		boolean serviceAnswer = service.getDI(inputAddress);

		assertEquals("Ansswer did not equal", answer, serviceAnswer);

	}

	@Test
	public void writesHoldingRegister() throws IOServiceException {
		int registerAddress = new Random().nextInt(2000);
		int value = new Random().nextInt(Short.MAX_VALUE);

		slave.setRequestHandler(new ServiceRequestHandler() {

			@Override
			public void onWriteSingleRegister(
					ServiceRequest<WriteSingleRegisterRequest, WriteSingleRegisterResponse> service) {
				WriteSingleRegisterRequest request = service.getRequest();
				assertEquals("Addresses did not match", registerAddress,
						request.getAddress());
				assertEquals("Write value did not match", value,
						request.getValue());
				ReferenceCountUtil.release(request);
			}
		});

		slave.setRequestHandler(new ServiceRequestHandler() {

			@Override
			public void onReadHoldingRegisters(
					ServiceRequest<ReadHoldingRegistersRequest, ReadHoldingRegistersResponse> service) {
				ReadHoldingRegistersRequest request = service.getRequest();
				assertEquals("Addresses did not match", request.getAddress(),
						registerAddress);
				ByteBuf buf = PooledByteBufAllocator.DEFAULT
						.buffer(request.getQuantity());
				buf.writeShort(value);

				service.sendResponse(new ReadHoldingRegistersResponse(buf));
				ReferenceCountUtil.release(request);

			}
		});

		service.setAO(registerAddress, value);
		assertEquals("Answer did not equal expected.", value,
				service.getAO(registerAddress));

	}

	@Test
	public void readsInputRegister() throws IOServiceException {
		int registerAddress = new Random().nextInt(2000);
		int value = new Random().nextInt(Short.MAX_VALUE);
		slave.setRequestHandler(new ServiceRequestHandler() {

			@Override
			public void onReadInputRegisters(
					ServiceRequest<ReadInputRegistersRequest, ReadInputRegistersResponse> service) {
				ReadInputRegistersRequest request = service.getRequest();
				assertEquals("Addresses did not match", request.getAddress(),
						registerAddress);
				ByteBuf buf = PooledByteBufAllocator.DEFAULT
						.buffer(request.getQuantity());
				buf.writeShort(value);

				service.sendResponse(new ReadInputRegistersResponse(buf));
				ReferenceCountUtil.release(request);
			}
		});

		assertEquals("Answer did not equal expected.", value,
				service.getAI(registerAddress));

	}

	private ModbusTcpSlave getSlave()
			throws InterruptedException, ExecutionException {
		ModbusTcpSlaveConfig config = new ModbusTcpSlaveConfig.Builder()
				.build();
		ModbusTcpSlave slave = new ModbusTcpSlave(config);

		slave.bind("localhost", 502).get();
		return slave;
	}

}
