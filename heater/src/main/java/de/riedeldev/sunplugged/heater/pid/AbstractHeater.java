package de.riedeldev.sunplugged.heater.pid;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import de.riedeldev.sunplugged.heater.config.ParameterChangeEvent;
import de.riedeldev.sunplugged.heater.config.Parameters;
import de.riedeldev.sunplugged.heater.io.IOServiceException;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(of = "name")
public abstract class AbstractHeater implements ConfigurableHeater, Runnable {

	@Value("${pid.logging.enable:false}")
	private boolean logging = false;

	@Value("${pid.logging.folder:data}")
	private String logFolder = "data";

	private volatile boolean running = true;

	private volatile boolean control = false;

	protected MiniPID miniPID = new MiniPID(1.0, 1.0, 1.0);

	private double updateInterval = 0.0;

	protected final String name;

	protected Thread thread;

	private Object waitObject = new Object();

	private double power = 0.0;

	@Autowired
	private ApplicationEventPublisher publisher = null;

	@Autowired
	protected SimpMessagingTemplate template = null;

	private final String topic;

	public AbstractHeater(String name, Parameters parameters, String topic) {
		this.name = name;
		this.thread = new Thread(this, name);
		this.topic = topic;
		miniPID.setSetpoint(0.0);
		// miniPID.setMaxIOutput(0.5);
		miniPID.setOutputLimits(0.0, 1.0);
		miniPID.setOutputRampRate(0.2);
		setParameters(parameters);
	}

	@EventListener(classes = ParameterChangeEvent.class)
	public void onNewParameters(ParameterChangeEvent event) {
		setParameters(event.getNewParameters());
	}

	@MessageMapping
	public void onMessage(Message<HeaterStatus> message) {
		if (message.getHeaders()
				.get(SimpMessageHeaderAccessor.DESTINATION_HEADER)
				.equals(topic)) {
			HeaterStatus status = message.getPayload();
			handleNewStatus(status);

		}
	}

	protected void handleNewStatus(HeaterStatus status) {
		if (status.getPower() != getPower()) {
			setPower(status.getPower());
		}
		if (status.getP() != miniPID.getP()) {
			setPIDValues(status.getP(), status.getI(), status.getD());
		} else if (status.getI() != miniPID.getI()) {
			setPIDValues(status.getP(), status.getI(), status.getD());
		} else if (status.getD() != miniPID.getD()) {
			setPIDValues(status.getP(), status.getI(), status.getD());
		}
		if (status.getTargetTemperature() != getTargetTemperature()) {
			setTargetTemperature(status.getTargetTemperature());
		}
	}

	protected void setParameters(Parameters parameters) {
		setPIDValues(parameters.getPreHeaterP(), parameters.getPreHeaterI(),
				parameters.getPreHeaterD());
		setUpdateInterval(parameters.getPreHeaterIntervalLength());
	}

	@Override
	public void startControlLoop() {
		if (thread.isAlive() == false) {
			this.thread = new Thread(this, name);
		}
		running = true;
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void stopControlLoop() {
		thread.interrupt();
	}

	@Override
	public void run() {
		log.debug(String.format("Starting Heater PID Loop '%s'", name));
		if (logging == true) {
			PIDLogger logger = new PIDLogger(
					Paths.get(logFolder, name).toString(), this);

			logger.start();
			log.debug(String.format(
					"Data Logging is enabeled. Logging into \"%s\".",
					logger.getFilePath()));
		}
		while (running && Thread.interrupted() == false) {
			long lastTime = System.currentTimeMillis();

			if (control) {
				double change;
				try {
					change = miniPID.getOutput(getCurrentTemperature());
					submitChange(change);

					if (publisher != null) {
						publisher.publishEvent(HeaterStatusEvent.getFor(this));
					}
				} catch (IOServiceException e) {
					log.error("Heater PID Loop crashed because of IOException",
							e);
				}

			} else {
				synchronized (waitObject) {
					try {
						waitObject.wait();
					} catch (InterruptedException e) {
						break;
					}
				}
			}

			long timePassed = System.currentTimeMillis() - lastTime;
			if (timePassed < 0) {
				timePassed = 0;
			}
			long millisecondsEachCycle = (long) (updateInterval * 1000);

			long millisecondsToWait = millisecondsEachCycle - timePassed;
			if (millisecondsToWait > 0) {
				try {
					Thread.sleep(millisecondsToWait);
				} catch (InterruptedException e) {
					log.error("Heater PID Loop interrupted!", e);
				}
			}

		}
		log.debug(String.format("Heater PID Loop '%s' stopped.", name));
	}

	protected abstract void submitChange(double change)
			throws IOServiceException;

	@Override
	public void activateControlling() {
		log.debug(String.format("Activated Controlling for Heater Loop '%s'",
				name));
		this.control = true;
		if (thread.isAlive() == false) {
			startControlLoop();
		}
		synchronized (waitObject) {
			waitObject.notifyAll();
		}
		sendNewStatus();
	}

	@Override
	public void deactivateControlling() {
		log.debug(String.format("Deactivated Controlling for Heater Loop '%s'",
				name));
		this.control = false;
		sendNewStatus();
	}

	@Override
	public boolean isControlling() {
		return control;
	}

	@Override
	public void setTargetTemperature(double target) {
		log.debug(String.format(
				"New Target Temperature for Heater Loop '%s': '%.2f'", name,
				target));
		miniPID.setSetpoint(target);
	}

	@Override
	public double getTargetTemperature() {
		return miniPID.getSetpoint();
	}

	@Override
	public void setPIDValues(double P, double I, double D) {
		log.debug(String.format(
				"New PID Values for Heater Loop '%s': P='%.3f' I='%.3f' D='%.3f'",
				name, P, I, D));
		miniPID.setPID(P, I, D);
		sendNewStatus();
	}

	@Override
	public double[] getPIDValues() {
		return new double[]{miniPID.getP(), miniPID.getI(), miniPID.getD()};
	}

	@Override
	public double getUpdateInterval() {
		return updateInterval;
	}

	@Override
	public void setUpdateInterval(double t) {
		log.debug(String.format(
				"New UpdateInterval for Heater Loop '%s':'%.3f' ", name, t));
		this.updateInterval = t;
	}

	@Override
	public void forcePower(double power) throws IOServiceException {
		if (isControlling()) {
			log.warn(
					"Trying to force power, but PID is running. Consider deactivating it!");
		}
		setPower(power);
	}

	public double getPower() {
		return power;
	}

	protected void setPower(double power) {
		if (power < 0) {
			this.power = 0;
		} else if (power > 1) {
			this.power = 1;
		}
		this.power = power;
		sendNewStatus();
	}

	protected void sendNewStatus() {
		double currentTemperature;
		try {
			currentTemperature = getCurrentTemperature();
		} catch (IOServiceException e) {
			currentTemperature = Double.NaN;
		}
		HeaterStatus status = new HeaterStatus(isOn(), isControlling(),
				getPower(), miniPID.getP(), miniPID.getI(), miniPID.getD(),
				getTargetTemperature(), currentTemperature);
		if (template != null) {
			template.convertAndSend(topic, status);
		}

	}

}
