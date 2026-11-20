package com.tcsl.zodiac.comm.utils;

import java.util.UUID;

/**
 * @className: UuidUtils
 * @description:
 * @author: yang pan
 */
public class UuidUtils {

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
