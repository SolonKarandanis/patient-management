package com.pm.paymentservice.broker;

public interface Producer<T> {
    public void sendEvent(T object);
}
