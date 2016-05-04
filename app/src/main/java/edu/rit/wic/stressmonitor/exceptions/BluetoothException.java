package edu.rit.wic.stressmonitor.exceptions;

/**
 * Created by Matthew on 5/4/2016.
 */
public class BluetoothException extends Exception {

    public BluetoothException(String message) {
        super(message);
    }

    public BluetoothException(Throwable t) {
        super(t);
    }

    public BluetoothException(String msg, Throwable t) {
        super(msg, t);
    }

}
