package de.riedeldev.sunplugged.beckhoff.klspi;

public interface ByteKlemme extends Klemme {

	int addressesNeededInRead();

	int addressesNeededInWrite();

}
