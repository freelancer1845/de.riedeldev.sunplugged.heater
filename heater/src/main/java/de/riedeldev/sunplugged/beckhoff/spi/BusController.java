package de.riedeldev.sunplugged.beckhoff.spi;

import java.util.concurrent.CompletableFuture;

public interface BusController {

	CompletableFuture<Void> setAnalogAddress(int address, int value);

	CompletableFuture<Void> readAnalogAddress(int address, int value);

}
