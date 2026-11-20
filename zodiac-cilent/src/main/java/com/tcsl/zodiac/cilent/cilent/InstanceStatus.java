package com.tcsl.zodiac.cilent.cilent;

/**
 * @className: InstanceStatus
 * @description:
 * @author: yang pan
 */
public enum InstanceStatus {
    UP, // Ready to receive traffic
    DOWN, // Do not send traffic- healthcheck callback failed
    STARTING, // Just about starting- initializations to be done - do not
    // send traffic
    OUT_OF_SERVICE, // Intentionally shutdown for traffic
    UNKNOWN;

    public static InstanceStatus toEnum(String s) {
        if (s != null) {
            try {
                return InstanceStatus.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }
        return UNKNOWN;
    }
}
