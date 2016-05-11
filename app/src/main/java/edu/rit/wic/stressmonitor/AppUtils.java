package edu.rit.wic.stressmonitor;

import android.os.Looper;

/**
 * General application utilities that handle some fairly mundane things with the Android Environment
 * such as detecting if we are on the UI Thread or not, or view manipulation, etc.
 *
 * @author Matthew Crocco
 */
public final class AppUtils {

    private AppUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     *     Determines whether or not execution is currently occurring on the Main UI Thread.
     * </p>
     *
     * <p>
     *     It is worth noting that the UI Thread is equivalent to the Main Thread of the application.
     * </p>
     *
     * @return True if current thread is UI Thread, false otherwise.
     */
    public static boolean isOnUiThread() {
        return Looper.getMainLooper().getThread().equals(Thread.currentThread());
    }

}
