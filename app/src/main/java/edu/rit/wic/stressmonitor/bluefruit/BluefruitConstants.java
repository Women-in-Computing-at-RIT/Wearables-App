package edu.rit.wic.stressmonitor.bluefruit;

import java.util.UUID;

/**
 * Created by Matthew on 5/5/2016.
 */
public final class BluefruitConstants {

    private BluefruitConstants() {
        throw new UnsupportedOperationException();
    }

    public static final String UUID_UART_STRING = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";
    public static final String UUID_TX_STRING = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E";
    public static final String UUID_RX_STRING = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E";
    public static final String UUID_CLIENT_STRING = "00002902-0000-1000-8000-00805F9B34FB";

    public static final UUID UUID_UART = UUID.fromString(UUID_UART_STRING);
    public static final UUID UUID_TX = UUID.fromString(UUID_TX_STRING);
    public static final UUID UUID_RX = UUID.fromString(UUID_RX_STRING);
    public static final UUID UUID_CLIENT = UUID.fromString(UUID_CLIENT_STRING);

    public static final UUID[] BLUEFRUIT_UUIDS = {
            UUID_UART,
            UUID_TX,
            UUID_RX,
            UUID_CLIENT
    };

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

}
