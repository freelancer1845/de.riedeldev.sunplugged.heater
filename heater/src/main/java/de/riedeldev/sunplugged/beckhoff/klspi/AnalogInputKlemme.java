package de.riedeldev.sunplugged.beckhoff.klspi;

import java.util.concurrent.CompletableFuture;

public interface AnalogInputKlemme extends Klemme {

	CompletableFuture<Double> read(int number);

	int inputs();
}
