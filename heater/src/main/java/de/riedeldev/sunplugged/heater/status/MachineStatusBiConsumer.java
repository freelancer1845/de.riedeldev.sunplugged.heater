package de.riedeldev.sunplugged.heater.status;

import de.riedeldev.sunplugged.heater.io.IOServiceException;

@FunctionalInterface
public interface MachineStatusBiConsumer {

	public void accept(MachineStatus s, Double d) throws IOServiceException;

}
