package de.riedeldev.sunplugged.heater;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;

import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;

import de.riedeldev.sunplugged.heater.config.Parameters;
import de.riedeldev.sunplugged.heater.io.IOServiceEvent;
import de.riedeldev.sunplugged.heater.io.IOServiceException;
import de.riedeldev.sunplugged.heater.io.IOServiceImpl;

public class ModbusRealTest {
	public static void main(String[] args) throws InterruptedException,
			ExecutionException, IOServiceException {
		ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder(
				"localhost").setPort(502).build();

		ApplicationEventPublisher publisher = mock(
				ApplicationEventPublisher.class);
		Parameters parameters = new Parameters();
		IOServiceImpl service = new IOServiceImpl(new Parameters(), publisher);

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

		while (true) {
			System.out.println();

			System.out.println("Coil [5]: " + service.getDO(5));
			System.out.println("Input [5]: " + service.getDI(5));
			System.out.println("Holding [5]: " + service.getAO(5));
			System.out.println("InutReg [5]: " + service.getAI(5));
			Thread.sleep(2000);
		}

	}
}
