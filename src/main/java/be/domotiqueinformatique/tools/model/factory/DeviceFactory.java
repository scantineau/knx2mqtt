package be.domotiqueinformatique.tools.model.factory;

import be.domotiqueinformatique.tools.model.HomieDevice;
import be.domotiqueinformatique.tools.model.HomieNode;
import be.domotiqueinformatique.tools.model.builder.HomieDeviceBuilder;
import be.domotiqueinformatique.tools.knx.loader.util.LoaderUtil;
import org.w3c.dom.NodeList;

import java.util.List;

public class DeviceFactory {
    private static DeviceFactory INSTANCE;

    private DeviceFactory() {
    }

    public static DeviceFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DeviceFactory();
        }
        return INSTANCE;
    }

    public HomieDevice getFromDevice(String path, String nodeNameAttr, NodeList nodeList) {
        return HomieDeviceBuilder.aDevice()
                .withName(LoaderUtil.getDeviceNameFrom(path, nodeNameAttr))
                .withNodes(buildNodesFrom(nodeList))
                .build();
    }

    private List<HomieNode> buildNodesFrom(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            org.w3c.dom.Node item = nodeList.item(i);
            String nodeNameAttr = LoaderUtil.getNameAttrFrom(item);
            if (item.getNodeName().equals("GroupAddressRef")) {

            }
        }
        return null;
    }
}
