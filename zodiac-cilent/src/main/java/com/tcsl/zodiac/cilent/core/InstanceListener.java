package com.tcsl.zodiac.cilent.core;

/**
 * @className: InstanceListener
 * @description:
 * @author: yang pan
 */
public interface InstanceListener {

    String getName();

    void  onListener(InstanceEvent event);
}
