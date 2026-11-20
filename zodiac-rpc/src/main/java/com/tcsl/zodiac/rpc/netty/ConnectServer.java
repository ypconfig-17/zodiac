package com.tcsl.zodiac.rpc.netty;


import java.io.IOException;

/**
 * @className: ConnectCilent
 * @description:
 * @author: yang pan
 */
public interface  ConnectServer extends AutoCloseable {

    void accept()throws IOException;
}
