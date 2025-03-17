package com.pm.patientservice.broker;

import com.pm.patientservice.model.Patient;

public interface Producer<T> {
    public void sendEvent(T object);
}
