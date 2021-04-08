package be.domotiqueinformatique.tools.knx.loader.util;

import org.w3c.dom.Node;

public class LoaderUtil {

    public static String sanitizeNodeName(String nodeNameAttr) {
        return nodeNameAttr.replaceAll("/","-");
    }

    public static String getNameAttrFrom(Node node) {
        if (node.getAttributes() != null && node.getAttributes().getNamedItem("Name") != null) {
            return sanitizeNodeName(node.getAttributes().getNamedItem("Name").getNodeValue());
        }
        return "";
    }

    public static String getDeviceNameFrom(String path, String nodeNameAttr) {
        return path.replaceAll("/","_").replaceAll(" ","") + "_" + nodeNameAttr;
    }
}
