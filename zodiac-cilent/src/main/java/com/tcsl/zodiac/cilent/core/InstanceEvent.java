package com.tcsl.zodiac.cilent.core;

import com.tcsl.zodiac.cilent.cilent.InstanceStatus;

/**
 * @className: InstanceEvent
 * @description:
 * @author: yang pan
 */
public class InstanceEvent {


    private InstanceStatus status;

    public InstanceEvent(InstanceStatus status) {
        this.status = status;
    }

    public InstanceStatus getStatus() {
        return status;
    }

    public void setStatus(InstanceStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "InstanceStatusChangeEvent{" +
                "status=" + status +
                '}';
    }
}
