package be.domotiqueinformatique.tools.knx.loader;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class EtsProjectLoader {


    static final String LOCATIONS_EXPR = "//Locations";
    static final String GROUP_ADDRESS_EXPR = "//GroupAddress";
    private final XPath xPath = XPathFactory.newInstance().newXPath();

    EtsProjectLoader() {

    }

    public List<NodeExplorer> load(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        Document doc = getDocumentBuilder().parse(inputStream);
        doc.getDocumentElement().normalize();
        Stream<NodeExplorer> nodeExplorerStream = deepSearchFunctions(new NodeExplorer(getNodeList(doc, xPath, LOCATIONS_EXPR)));
        nodeExplorerStream.map(nodeExplorer -> ) // map to device
        return nodeExplorerStream.collect(Collectors.toList());
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setCoalescing(true);
        return docBuilderFactory.newDocumentBuilder();
    }

    private NodeList getNodeList(Document doc, XPath xPath, String expression) throws XPathExpressionException {
        return (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
    }

    private Stream<NodeExplorer> deepSearchFunctions(NodeExplorer nodeExplorer) {
        Stream<NodeExplorer> nodeExplorerStream = Stream.<NodeExplorer>builder().build();
        for (int i = 0; i < nodeExplorer.nodeList.getLength(); i++) {
            Node item = nodeExplorer.nodeList.item(i);
            String nodeNameAttr = getName(item);
            if (item.getNodeName().equals("Function")) {
                nodeExplorerStream = Stream.concat(nodeExplorerStream, Stream.of(new NodeExplorer(nodeExplorer.path, nodeNameAttr, item.getChildNodes())));
            } else {
                nodeExplorerStream = Stream.concat(nodeExplorerStream, deepSearchFunctions(new NodeExplorer(nodeExplorer.path, nodeNameAttr, item.getChildNodes())));
            }
        }
        return nodeExplorerStream;
    }

    private static String getName(Node node) {
        if (node.getAttributes() != null && node.getAttributes().getNamedItem("Name") != null) {
            return node.getAttributes().getNamedItem("Name").getNodeValue();
        }
        return "";
    }

    static class NodeExplorer {
        String path;
        NodeList nodeList;

        NodeExplorer(String path, String nodeNameAttr, NodeList nodeList) {
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
    }
}
