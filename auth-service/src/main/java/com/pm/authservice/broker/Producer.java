package com.pm.authservice.broker;

public interface Producer<T> {
    public void sendEvent(T object);
}
