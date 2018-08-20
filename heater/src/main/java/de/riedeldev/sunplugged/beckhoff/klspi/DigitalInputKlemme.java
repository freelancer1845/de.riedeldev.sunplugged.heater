package de.riedeldev.sunplugged.beckhoff.klspi;

import java.util.concurrent.CompletableFuture;

public interface DigitalInputKlemme extends Klemme {

	CompletableFuture<Boolean> read(int number);

}
