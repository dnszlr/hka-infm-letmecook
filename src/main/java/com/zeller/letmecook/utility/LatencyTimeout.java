package com.zeller.letmecook.utility;

import java.util.concurrent.TimeUnit;

public class LatencyTimeout {

    /**
     * Puts the current thread to sleep for a random duration between min and max
     * @param min minimum sleep duration
     * @param max maximum sleep duration
     */
    public static void duration(int min, int max) {
        // TODO CAUTION, TIMEOUT BLOCK TO SIMULATE TRAFFIC
        try {TimeUnit.MILLISECONDS.sleep(RandomGenerator.generate(min, max));}
        catch(InterruptedException e) {throw new RuntimeException(e);}
        // TODO CAUTION, TIMEOUT BLOCK TO SIMULATE TRAFFIC
    }
}
