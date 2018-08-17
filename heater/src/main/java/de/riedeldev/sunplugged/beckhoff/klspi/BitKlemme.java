package de.riedeldev.sunplugged.beckhoff.klspi;

public interface BitKlemme extends Klemme {

	int bitsNeededInRead();

	int bitsNeededInWrite();

}
