package de.riedeldev.sunplugged.beckhoff.klspi;

import java.util.concurrent.CompletableFuture;

public interface AnalogInputKlemme extends ByteKlemme {

	CompletableFuture<Double> readOutput(int number);
}
