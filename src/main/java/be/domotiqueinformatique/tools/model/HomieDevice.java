package be.domotiqueinformatique.tools.model;

import java.util.List;

public class HomieDevice {
    private String homie;
    private String name;
    private DeviceState state;
    private List<HomieNode> homieNodes;
    private String extensions;

    public String getHomie() {
        return homie;
    }

    public void setHomie(String homie) {
        this.homie = homie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceState getState() {
        return state;
    }

    public void setState(DeviceState state) {
        this.state = state;
    }

    public List<HomieNode> getNodes() {
        return homieNodes;
    }

    public void setNodes(List<HomieNode> homieNodes) {
        this.homieNodes = homieNodes;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }
}
