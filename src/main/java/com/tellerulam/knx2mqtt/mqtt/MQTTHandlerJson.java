package com.tellerulam.knx2mqtt.mqtt;

import com.eclipsesource.json.JsonObject;
import com.tellerulam.knx2mqtt.knx.GroupAddressManager;
import com.tellerulam.knx2mqtt.knx.KNXConnector;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MQTTHandlerJson extends MQTTHandler {
    private final Logger L = Logger.getLogger(getClass().getName());

    public static void init() throws MqttException {
        instance = new MQTTHandlerJson();
        instance.doInit();
    }

    protected void processMessage(String topic, MqttMessage msg) {
        L.fine("Received " + msg + " to " + topic);
        topic = topic.substring(topicPrefix.length());
        if (topic.startsWith("set/"))
            processSetGet(topic.substring(4), msg, true);
        else if (topic.startsWith("get/"))
            processSetGet(topic.substring(4), msg, false);
        else
            L.warning("Ignored message " + msg + " to unknown topic " + topic);
    }

    @Override
    protected void doSubscribe() throws MqttException {
        L.info("Successfully connected to broker, subscribing to " + topicPrefix + "(set|get)/#");
        mqttc.subscribe(topicPrefix + "set/#", 1);
        mqttc.subscribe(topicPrefix + "get/#", 1);
    }

    @Override
    protected String getDefaultTopicPrefix() {
        return "knx";
    }

    @Override
    protected String getWillTopic() {
        return topicPrefix + "connected";
    }

    protected void doPublish(String name, Object val, String src, String dpt, String textual, long updateTime, long lastChange, String ga) {
        JsonObject jso = new JsonObject();
        jso.add("ts", updateTime).add("lc", lastChange).add("knx_src_addr", src).add("knx_dpt", dpt).add("GA", ga);
        if (textual != null)
            jso.add("knx_textual", textual);
        if (val instanceof Integer)
            jso.add("val", (Integer) val);
        else if (val instanceof Number)
            jso.add("val", ((Number) val).doubleValue());
        else
            jso.add("val", val.toString());
        String txtmsg = jso.toString();
        MqttMessage msg = new MqttMessage(jso.toString().getBytes(StandardCharsets.UTF_8));
        msg.setQos(0);
        msg.setRetained(true);
        try {
            String fullTopic = topicPrefix + "status/" + name;
            mqttc.publish(fullTopic, msg);
            L.finer("Published " + txtmsg + " to " + fullTopic);
        } catch (MqttException e) {
            L.log(Level.WARNING, "Error when publishing message " + txtmsg, e);
        }
    }

    @Override
    protected void doPublishConfigTopics() {
        throw new UnsupportedOperationException("Legacy json does not need topic config publication");
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
        if (set)
            KNXConnector.doGroupWrite(gai.getAddress(), data, gai);
        else
            KNXConnector.doGroupRead(gai.getAddress(), data, gai);
    }

}
