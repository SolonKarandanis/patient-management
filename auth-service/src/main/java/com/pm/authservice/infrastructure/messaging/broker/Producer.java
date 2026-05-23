package com.pm.authservice.infrastructure.messaging.broker;

public interface Producer<T> {
    void sendEvent(T object);
}
