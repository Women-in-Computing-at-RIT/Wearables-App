package edu.rit.wic.stressmonitor.bluefruit;

import android.content.IntentFilter;

import com.movisens.smartgattlib.GattUtils;
import com.orhanobut.logger.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Matthew on 5/5/2016.
 */
public final class BluefruitUtils {

    private BluefruitUtils() {
        throw new UnsupportedOperationException();
    }

    public static final String UUID_PARSE_TMPL = "%08x-0000-1000-8000-00805f9b34fb";

    public static Set<UUID> parseUuidData(final byte[] data) {
        final Set<UUID> ids = new HashSet<>();

        int offset = 0;
        while(offset < (data.length - 2)) {
            int len = data[offset++];
            if(len == 0)
                break;

            int type = data[offset++];
            switch(type) {
                case 0x02:
                case 0x03:
                    while(len > 1) {
                        int uuid16 = data[offset++] | (data[offset++] << 8);
//                        ByteBuffer buffer = ByteBuffer.wrap(data, offset, 2).order(ByteOrder.LITTLE_ENDIAN);
//                        offset += 2;
                        len -= 2;

//                        String uuid = GattUtils.toUuid16(buffer.getShort());
//                        ids.add(GattUtils.toUuid(uuid));
                        ids.add(UUID.fromString(String.format(UUID_PARSE_TMPL, uuid16)));
                    }

                    break;
                case 0x06:
                case 0x07:
                    while(len >= 16) {
                        try {
                            ByteBuffer buffer = ByteBuffer.wrap(data, offset, 16).order(ByteOrder.LITTLE_ENDIAN);
                            long mostSignificant = buffer.getLong(), leastSignificant = buffer.getLong();
                            ids.add(new UUID(mostSignificant, leastSignificant));
                        } catch(IndexOutOfBoundsException ex) {
                            // NO OP
                        } finally {
                            offset += 16;
                            len -= 16;
                        }
                    }
                    break;
                default:
                    offset += (len - 1);
                    break;
            }
        }

        return ids;
    }

    public static boolean searchUuidData(byte[] data, UUID target) {
        try {
            return parseUuidData(data).contains(target);
        } catch(Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    public final static String ACTION_GATT_CONNECTED =
            "edu.rit.wic.bluefruit.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "edu.rit.wic.bluefruit.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "edu.rit.wic.bluefruit.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "edu.rit.wic.bluefruit.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "edu.rit.wic.bluefruit.EXTRA_DATA";

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


}
