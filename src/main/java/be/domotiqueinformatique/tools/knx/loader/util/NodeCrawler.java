package be.domotiqueinformatique.tools.knx.loader.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.List;
import java.util.stream.Stream;

public class NodeCrawler {
    private final List<String> nodeNamesToFind;
    private static final XPath xPath = XPathFactory.newInstance().newXPath();

    private NodeCrawler(List<String> nodeNamesToFind) {
        this.nodeNamesToFind = nodeNamesToFind;
    }

    public static Stream<NodeExplorer> getNodeExplorerStream(Document doc, String expression, List<String> nodeNamesToFind) throws XPathExpressionException {
        NodeCrawler nodeCrawler = new NodeCrawler(nodeNamesToFind);
        return nodeCrawler.deepSearch(new NodeExplorer(getNodeList(doc, expression)));
    }

    private static NodeList getNodeList(Document doc, String expression) throws XPathExpressionException {
        return (NodeList) NodeCrawler.xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
    }

    private Stream<NodeExplorer> deepSearch(NodeExplorer nodeExplorer) {
        Stream<NodeExplorer> nodeExplorerStream = Stream.<NodeExplorer>builder().build();
        for (int i = 0; i < nodeExplorer.getNodeList().getLength(); i++) {
            Node item = nodeExplorer.getNodeList().item(i);
            String nodeNameAttr = LoaderUtil.getNameAttrFrom(item);
            if (nodeNamesToFind.contains(item.getNodeName())) {
                nodeExplorerStream = Stream.concat(nodeExplorerStream, Stream.of(new NodeExplorer(nodeExplorer.getPath(), "", item.getChildNodes())));
            } else {
                nodeExplorerStream = Stream.concat(nodeExplorerStream, deepSearch(new NodeExplorer(nodeExplorer.getPath(), nodeNameAttr, item.getChildNodes())));
            }
        }
        return nodeExplorerStream;
    }
}
