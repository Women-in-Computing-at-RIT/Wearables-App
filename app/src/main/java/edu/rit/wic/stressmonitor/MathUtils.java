package edu.rit.wic.stressmonitor;

import java.util.Arrays;

/**
 * A class for general mathematical utilities, including some statistics utilities.
 *
 * @author Matthew Crocco
 */
public final class MathUtils {

    private MathUtils() {
        // Prevent any attempt to instantiate, even Reflective
        throw new UnsupportedOperationException();
    }

    /**
     * Takes a list of integers and finds the median of that list of integers.
     *
     * @param list List of Integers
     * @return median of list
     */
    public static double findMedian(int[] list) {
        // TODO Could be optimally converted to Median of Medians Algorithm
        // Reduced to Sorting Problem
        Arrays.sort(list);

        // Median Selection
        if(list.length % 2 == 0)
            return ((double)list[list.length/2] + (double)list[list.length/2 - 1])/2;
        else
            return list[list.length/2];
    }

}
