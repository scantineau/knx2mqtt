package be.domotiqueinformatique.tools.knx.loader.util;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.NodeList;

public class NodeExplorer {
    private final String path;
    private final NodeList nodeList;
    private String lastNodeNameAttr;

    NodeExplorer(String path, String nodeNameAttr, NodeList nodeList) {
        lastNodeNameAttr = nodeNameAttr;
        if (StringUtils.isEmpty(path)) {
            this.path = nodeNameAttr;
        } else if (StringUtils.isEmpty(nodeNameAttr)) {
            this.path = path;
        } else {
            this.path = path + "/" + nodeNameAttr;
        }
        this.nodeList = nodeList;
    }

    NodeExplorer(NodeList nodeList) {
        this.path = "";
        this.nodeList = nodeList;
    }

    public String getPath() {
        return path;
    }

    public NodeList getNodeList() {
        return nodeList;
    }

    public String getLastNodeNameAttr() {
        return lastNodeNameAttr;
    }
}
