package com.tellerulam.knx2mqtt.mqtt;

import com.tellerulam.knx2mqtt.Main;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MQTTHandler {
    protected static MQTTHandler instance;
    private static boolean knxConnectionState;
    private final Logger L = Logger.getLogger(getClass().getName());
    protected final String topicPrefix;
    protected MqttClient mqttc;
    private boolean shouldBeConnected;

    protected MQTTHandler() {
        String tp = System.getProperty("knx2mqtt.mqtt.topic", getDefaultTopicPrefix());
        if (!tp.endsWith("/"))
            tp += "/";
        topicPrefix = tp;
    }

    public static void setKNXConnectionState(boolean connected) {
        knxConnectionState = connected;
        instance.sendConnectionState();
    }

    public static void publish(String name, Object val, String src, String dpt, String textual, long updateTime, long lastChange, String ga) {
        instance.doPublish(name, val, src, dpt, textual, updateTime, lastChange, ga);
    }

    private void queueConnect() {
        shouldBeConnected = false;
        Main.getTimer().schedule(new TimerTask() {
            @Override
            public void run() {
                doConnect();
            }
        }, 10 * 1000);
    }

    protected abstract String getDefaultTopicPrefix();

    protected abstract String getWillTopic();

    protected abstract void doPublish(String name, Object val, String src, String dpt, String textual, long updateTime, long lastChange, String ga);

    protected abstract void doPublishConfigTopics();

    protected abstract void processMessage(String topic, MqttMessage msg);

    protected abstract void doSubscribe() throws MqttException;

    private void doConnect() {
        L.info("Connecting to MQTT broker " + mqttc.getServerURI() + " with CLIENTID=" + mqttc.getClientId() + " and TOPIC PREFIX=" + topicPrefix);

        MqttConnectOptions copts = new MqttConnectOptions();
        copts.setWill(getWillTopic(), "0".getBytes(), 1, true);
        copts.setCleanSession(true);
        copts.setUserName(System.getProperty("knx2mqtt.mqtt.user", null));
        if (System.getProperties().containsKey("knx2mqtt.mqtt.password")) {
            copts.setPassword(System.getProperty("knx2mqtt.mqtt.password").toCharArray());
        }
        try {
            mqttc.connect(copts);
            sendConnectionState();
            try {
                doSubscribe();
                shouldBeConnected = true;
            } catch (MqttException mqe) {
                L.log(Level.WARNING, "Error subscribing to topic hierarchy, check your configuration", mqe);
                throw mqe;
            }
        } catch (MqttException mqe) {
            L.log(Level.WARNING, "Error while connecting to MQTT broker, will retry: " + mqe.getMessage(), mqe);
            queueConnect(); // Attempt reconnect
        }
    }

    protected void doInit() throws MqttException {
        String server = System.getProperty("knx2mqtt.mqtt.server", "tcp://localhost:1883");
        String clientID = System.getProperty("knx2mqtt.mqtt.clientid", "knx2mqtt");
        mqttc = new MqttClient(server, clientID, new MemoryPersistence());
        mqttc.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String topic, MqttMessage msg) throws Exception {
                try {
                    processMessage(topic, msg);
                } catch (Exception e) {
                    L.log(Level.WARNING, "Error when processing message " + msg + " for " + topic, e);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                /* Intentionally ignored */
            }

            @Override
            public void connectionLost(Throwable t) {
                L.log(Level.WARNING, "Connection to MQTT broker lost", t);
                queueConnect();
            }
        });
        doConnect();
        Main.getTimer().schedule(new StateChecker(), 30 * 1000, 30 * 1000);
    }

    private void sendConnectionState() {
        try {
            instance.mqttc.publish(getWillTopic(), (knxConnectionState ? "2" : "1").getBytes(), 1, true);
        } catch (MqttException ignored) {
        }

    }

    private class StateChecker extends TimerTask {
        @Override
        public void run() {
            if (!mqttc.isConnected() && shouldBeConnected) {
                L.warning("Should be connected but aren't, reconnecting");
                queueConnect();
            }
        }
    }

}
