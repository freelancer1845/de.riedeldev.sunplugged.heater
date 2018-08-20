package de.riedeldev.sunplugged.beckhoff.klspi;

import java.util.concurrent.CompletableFuture;

public interface DigitalOutputKlemme extends DigitalInputKlemme {

	CompletableFuture<Void> set(int number, boolean value);

}
