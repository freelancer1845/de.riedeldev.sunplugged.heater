package de.riedeldev.sunplugged.beckhoff.klspi;

import java.util.concurrent.CompletableFuture;

public interface AnalogOutputKlemme extends AnalogInputKlemme {

	/**
	 * 
	 * @param number
	 *            of the output at this "klemme"
	 * @param value
	 *            you want to set. On a 0-10V Klemme use 0-10V and so forth.
	 * @return True if successful...
	 */
	CompletableFuture<Void> setOutput(int number, double value);

	int outputs();

}
