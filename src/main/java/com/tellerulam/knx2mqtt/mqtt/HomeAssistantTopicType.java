package com.tellerulam.knx2mqtt.mqtt;

import java.util.Arrays;

public enum HomeAssistantTopicType {
    SENSOR("sensor", "9.001"),
    SWITCH("switch", "1.001");

    private final String topic;
    private final String dpt;

    HomeAssistantTopicType(String topic, String dpt) {
        this.topic = topic;
        this.dpt = dpt;
    }

    public String getTopic() {
        return topic;
    }

    public String getDpt() {
        return dpt;
    }

    public static HomeAssistantTopicType fromDpt(String dpt) {
        return Arrays.stream(HomeAssistantTopicType.values())
                .filter(homeAssistantTopicType -> homeAssistantTopicType.getDpt().equals(dpt))
                .findFirst()
                .orElse(SWITCH);
    }
}
