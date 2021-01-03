package com.tellerulam.knx2mqtt.exception;

import org.eclipse.paho.client.mqttv3.MqttException;

public class MQTTHandlerException extends RuntimeException {
    private MqttException mqttException;

    public MQTTHandlerException(MqttException e) {
        mqttException = e;
    }

    public MqttException getMqttException() {
        return mqttException;
    }
}
