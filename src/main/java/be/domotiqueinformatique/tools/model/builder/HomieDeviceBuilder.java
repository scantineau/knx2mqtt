package be.domotiqueinformatique.tools.model.builder;

import be.domotiqueinformatique.tools.model.HomieDevice;
import be.domotiqueinformatique.tools.model.DeviceState;
import be.domotiqueinformatique.tools.model.HomieNode;
import be.domotiqueinformatique.tools.util.Util;

import java.util.List;

public final class HomieDeviceBuilder {
    private String name;
    private DeviceState state;
    private List<HomieNode> homieNodes;
    private String extensions;

    private HomieDeviceBuilder() {
    }

    public static HomieDeviceBuilder aDevice() {
        return new HomieDeviceBuilder();
    }

    public HomieDeviceBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public HomieDeviceBuilder withState(DeviceState state) {
        this.state = state;
        return this;
    }

    public HomieDeviceBuilder withNodes(List<HomieNode> homieNodes) {
        this.homieNodes = homieNodes;
        return this;
    }

    public HomieDeviceBuilder withExtensions(String extensions) {
        this.extensions = extensions;
        return this;
    }

    public HomieDevice build() {
        HomieDevice homieDevice = new HomieDevice();
        homieDevice.setHomie(Util.HOMIE_VERSION);
        homieDevice.setName(name);
        homieDevice.setState(state);
        homieDevice.setNodes(homieNodes);
        homieDevice.setExtensions(extensions);
        return homieDevice;
    }
}
