package de.riedeldev.sunplugged.heater.ui;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

public class Utils {

	public static Label createHorizontalSeperator() {
		Label horizontalSperator = new Label("<hr />", ContentMode.HTML);
		horizontalSperator.setWidth("100%");
		return horizontalSperator;
	}
}
