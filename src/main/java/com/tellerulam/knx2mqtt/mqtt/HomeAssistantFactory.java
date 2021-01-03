package com.tellerulam.knx2mqtt.mqtt;

import com.eclipsesource.json.JsonObject;

import java.util.HashMap;

class HomeAssistantFactory {
    private final String name;
    private final String abbrTopic;
    private final HomeAssistantTopicType type;
    private final HashMap<String, String> stateTranslate = new HashMap<String, String>() {{
        put("ON", "ON");
        put("1", "ON");
        put("OFF", "OFF");
        put("0", "OFF");
    }};

    public HomeAssistantFactory(String name, HomeAssistantTopicType type) {
        this.name = name;
        this.abbrTopic = "homeassistant/" + type.getTopic() + "/" + this.name;
        this.type = type;
    }

    public HomeAssistantFactory(String name, String dpt) {
        this.name = name;
        this.type = HomeAssistantTopicType.fromDpt(dpt);
        this.abbrTopic = "homeassistant/" + type.getTopic() + "/" + this.name;
    }

    public String getConfig() {
        JsonObject jso = new JsonObject();
        jso.add("~", abbrTopic)
                .add("name", name)
                .add("command_topic", "~/set")
                .add("state_topic", "~/state");
        //TODO : device class
        return jso.toString();
    }

    public String getTopicConfig() {
        return abbrTopic + "/config";
    }

    public String getTopicSet() {
        return abbrTopic + "/set";
    }

    public String getTopicGet() {
        return abbrTopic + "/get";
    }

    public String getTopicState() {
        return abbrTopic + "/state";
    }

    public String getName() {
        return name;
    }

    public String buildMessageFrom(Object val) {
//        if (type.equals(HomeAssistantTopicType.SWITCH)) {
        return stateTranslate.getOrDefault(val.toString().toUpperCase(), val.toString());
//        } else {
//            JsonObject jso = new JsonObject();
//            jso.add("ts", updateTime).add("lc", lastChange).add("knx_src_addr", src).add("knx_dpt", dpt).add("GA", ga);
//            if (textual != null) {
//                jso.add("knx_textual", textual);
//            }
//            if (val instanceof Integer) {
//                jso.add("val", (Integer) val);
//            } else if (val instanceof Number) {
//                jso.add("val", ((Number) val).doubleValue());
//            } else {
//                jso.add("val", val.toString());
//            }
//            txtmsg = jso.toString();
//        }

    }

}
