package de.bytestore.pvheating.configuration;

public class DefaultPinout {
    // Fault Button
    public static final int FAULT_BUTTON_GPIO = 17;
    public static final int FAULT_BUTTON_PWM_GPIO = 5;

    // Status Button
    public static final int STATUS_BUTTON_GPIO = 27;
    public static final int STATUS_BUTTON_PWM_GPIO = 12;

    // SCR (Enable IO => Low = On, High = Off)
    public static final int SCR_PWM_GPIO = 13;
    public static final int SCR_ENABLE_GPIO = 25;
    public static final int SCR_RESPONSE_MODE_GPIO = 22;
    public static final int SCR_RESPONSE_FAULT_GPIO = 26;

    // Flow Meter
    public static final int FLOW_METER_GPIO = 24;

    // Pump (Enable IO => Low = On, High = Off)
    public static final int PUMP_ENABLE_GPIO = 16;
}
