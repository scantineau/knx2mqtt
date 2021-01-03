package com.tellerulam.knx2mqtt.mqtt;

import com.tellerulam.knx2mqtt.exception.MQTTHandlerException;
import com.tellerulam.knx2mqtt.knx.GroupAddressManager;
import com.tellerulam.knx2mqtt.knx.KNXConnector;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class MQTTHandlerHomeAssistant extends MQTTHandler {
    private final Logger L = Logger.getLogger(getClass().getName());
    private final HomeAssistantFactory knx2mqttConfigTopic = new HomeAssistantFactory("knx2mqtt", HomeAssistantTopicType.SENSOR);

    public static void init() throws MqttException {
        instance = new MQTTHandlerHomeAssistant();
        instance.doInit();
    }

    public static void publishConfigTopics() {
        instance.doPublishConfigTopics();
    }

    @Override
    protected void doPublishConfigTopics() {
        getHomeAssistantTopicStream()
                .forEach(homeAssistantFactory -> {
                    try {
                        MqttMessage msg = new MqttMessage(homeAssistantFactory.getConfig().getBytes(StandardCharsets.UTF_8));
                        msg.setQos(0);
                        msg.setRetained(true);
                        mqttc.publish(homeAssistantFactory.getTopicConfig(), msg);
                    } catch (MqttException e) {
                        L.log(Level.WARNING, "Error when publishing message " + homeAssistantFactory.getConfig(), e);
                    }
                });
        try {
            MqttMessage msg = new MqttMessage(knx2mqttConfigTopic.getConfig().getBytes(StandardCharsets.UTF_8));
            msg.setQos(0);
            msg.setRetained(true);
            mqttc.publish(knx2mqttConfigTopic.getTopicConfig(), msg);
        } catch (MqttException e) {
            L.log(Level.WARNING, "Error when publishing message " + knx2mqttConfigTopic.getConfig(), e);
        }
    }

    private Stream<HomeAssistantFactory> getHomeAssistantTopicStream() {
        return GroupAddressManager.getGATable().values().stream()
                .map(groupAddressInfo -> new HomeAssistantFactory(groupAddressInfo.getName(), groupAddressInfo.getDpt()));
    }

    protected void processMessage(String topic, MqttMessage msg) {
        L.fine("Received " + msg + " to " + topic);
        String[] split = topic.split("/");
        if (split[split.length - 1].equals("set"))
            processSetGet(split[split.length - 2], msg, true);
        else if (split[split.length - 1].equals("get"))
            processSetGet(split[split.length - 2], msg, false);
        else
            L.warning("Ignored message " + msg + " to unknown topic " + topic);
    }

    @Override
    protected void doSubscribe() throws MqttException {
        L.info("Successfully connected to broker, subscribing to state topics");
        try {
            getHomeAssistantTopicStream()
                    .forEach(homeAssistantFactory -> {
                        try {
                            mqttc.subscribe(homeAssistantFactory.getTopicSet(), 1);
                            mqttc.subscribe(homeAssistantFactory.getTopicGet(), 1);
                        } catch (MqttException e) {
                            throw new MQTTHandlerException(e);
                        }
                    });
        } catch (MQTTHandlerException mqttHandlerException) {
            throw mqttHandlerException.getMqttException();
        }
    }

    @Override
    protected String getDefaultTopicPrefix() {
        return "homeassistant";
    }

    @Override
    protected String getWillTopic() {
        return knx2mqttConfigTopic.getTopicState();
    }

    protected void doPublish(String name, Object val, String src, String dpt, String textual, long updateTime, long lastChange, String ga) {
        HomeAssistantFactory homeAssistantFactory = new HomeAssistantFactory(name, dpt);

        MqttMessage msg = new MqttMessage();
        msg.setQos(0);
        msg.setRetained(true);
        String txtmsg = homeAssistantFactory.buildMessageFrom(val);

        msg.setPayload(txtmsg.getBytes(StandardCharsets.UTF_8));
        try {
            mqttc.publish(homeAssistantFactory.getTopicState(), msg);
            L.finer("Published " + txtmsg + " to " + homeAssistantFactory.getTopicState());
        } catch (MqttException e) {
            L.log(Level.WARNING, "Error when publishing message " + txtmsg, e);
        }
    }

    private void processSetGet(String namePart, MqttMessage msg, boolean set) {
        if (msg.isRetained()) {
            L.finer("Ignoring retained message " + msg + " to " + namePart);
            return;
        }
        // Now translate the topic into a group address
        GroupAddressManager.GroupAddressInfo gai = GroupAddressManager.getGAInfoForName(namePart);
        if (gai == null) {
            // We didn't detect a group name, is it a group address ?
            gai = GroupAddressManager.getGAInfoForAddress(namePart);
            if (gai == null) {
                L.warning("Unable to translate name " + namePart + " into a group address, ignoring message " + msg);
                return;
            }
        }
        L.fine("Name " + namePart + " translates to GA " + gai.getAddress());
        String data = new String(msg.getPayload(), StandardCharsets.UTF_8);
        if (set) {
            KNXConnector.doGroupWrite(gai.getAddress(), data, gai);
            doPublish(gai.getName(), new String(msg.getPayload()), null, gai.getDpt(), gai.getTextual(), 0, 0, gai.getAddress());
        } else {
            KNXConnector.doGroupRead(gai.getAddress(), data, gai);
        }
    }
}
