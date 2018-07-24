package de.riedeldev.sunplugged.heater.pid;

public interface ConfigurableHeater extends Heater {

	public void setPIDValues(double P, double I, double D);

	public double[] getPIDValues();

	/**
	 * Gets the Update Interval length in s
	 * 
	 * @return
	 */
	public double getUpdateInterval();

	/**
	 * Update interval time length in s
	 * 
	 * @param t
	 *            in s
	 * 
	 */
	public void setUpdateInterval(double t);

	public void startControlLoop();

	public void stopControlLoop();

}
