package de.riedeldev.sunplugged.heater.status;

import de.riedeldev.sunplugged.heater.io.IOServiceException;

@FunctionalInterface
public interface MachineStatusConsumer {

	void accept(MachineStatus service) throws IOServiceException;

}
