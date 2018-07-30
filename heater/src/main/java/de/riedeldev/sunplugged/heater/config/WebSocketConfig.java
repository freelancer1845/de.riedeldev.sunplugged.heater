package de.riedeldev.sunplugged.heater.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic");
		registry.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/execute").setAllowedOrigins("*");
	}

	public final class Topics {

		public static final String PLC_ALL_FINE = "/plcAllFine";
		public static final String IS_OVERTEMPERATURE = "/isOvertemperature";
		public static final String PANEL_INTERLOCK_ONE = "/panelInterlockOne";
		public static final String PANEL_INTERLOCK_TWO = "/panelInterlockTwo";
		public static final String PANEL_INTERLOCK_THREE = "/panelInterlockThree";
		public static final String COVER_ALARM_TOP = "/coverAlarmTop";
		public static final String COVER_ALARM_BOTTOM = "/coverAlarmBottom";
		public static final String PANEL_INTERLOCK_ALARM = "/panelInterlockAlarm";
		public static final String HORN = "/horn";
		public static final String PLC_START = "/plcStart";
		public static final String PLC_RUN = "/plcRun";
		public static final String ALARM_LIGHTS = "/alarmLights";
		public static final String ZONE_ONE_TEMPERATURE = "/zoneOneTemperature";
		public static final String ZONE_TWO_TEMPERATURE = "/zoneTwoTemperature";
		public static final String ZONE_THREE_TEMPERATURE = "/zoneThreeTemperature";
		public static final String HEATER_ONE_TEMPERATURE = "/heaterOneTemperature";
		public static final String HEATER_TWO_TEMPERATURE = "/heaterTwoTemperature";
		public static final String HEATER_FAN_ONE_POWER = "/heaterFanOnePower";
		public static final String HEATER_FAN_TWO_POWER = "/heaterFanTwoPower";
		public static final String PRE_HEATER_ONE = "/preHeaterOne";
		public static final String PRE_HEATER_TWO = "/preHeaterTwo";
		public static final String MAIN_HEATER_ONE = "/mainHeaterOne";
		public static final String MAIN_HEATER_TWO = "/mainHeaterTwo";
		public static final String MAIN_HEATER_THREE = "/mainHeaterThree";

		public static final String TOPIC_CHANGE_COIL = "/changecoil";
		public static final String TOPIC_CHNGE_AO = "/changeao";
		public static final String AO_ACCESS = "/aoaccess/{address}";
		public static final String DO_ACCESS = "/doaccess/{address}";
		public static final String DI = "/di/{address}";
		private Topics() {
		}
	}
}
