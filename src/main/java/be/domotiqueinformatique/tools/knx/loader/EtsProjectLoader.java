package be.domotiqueinformatique.tools.knx.loader;

import be.domotiqueinformatique.tools.knx.GroupAddressInfo;
import be.domotiqueinformatique.tools.knx.loader.util.NodeCrawler;
import be.domotiqueinformatique.tools.knx.loader.util.NodeExplorer;
import be.domotiqueinformatique.tools.model.HomieDevice;
import be.domotiqueinformatique.tools.model.factory.DeviceFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class EtsProjectLoader {
    private static final String LOCATIONS_EXPR = "//Locations";
    private static final String GROUP_ADDRESS_EXPR = "//GroupAddress";
    public static final String GROUP_ADDRESS = "GroupAddress";
    public static final String FUNCTION = "Function";
    private HashMap<String, GroupAddressInfo> gaTable = new HashMap<>();

    EtsProjectLoader() {

    }

    public List<HomieDevice> load(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        Document doc = getDocumentBuilder().parse(inputStream);
        doc.getDocumentElement().normalize();
        NodeCrawler.getNodeExplorerStream(doc, GROUP_ADDRESS_EXPR, List.of(GROUP_ADDRESS))
                .forEach(nodeExplorer -> {

gaTable.put()
                });
        return NodeCrawler.getNodeExplorerStream(doc, LOCATIONS_EXPR, List.of(FUNCTION))
                .map(this::toDevice)
                .collect(Collectors.toList());// map to device
    }

    private HomieDevice toDevice(NodeExplorer nodeExplorer) {
        return DeviceFactory.getInstance().getFromDevice(nodeExplorer.getPath(), nodeExplorer.getLastNodeNameAttr(), nodeExplorer.getNodeList());
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setCoalescing(true);
        return docBuilderFactory.newDocumentBuilder();
    }

    private String translateDPT(String datapointType) {
        Pattern p = Pattern.compile("DPS?T-([0-9]+)(-([0-9]+))?");
        Matcher m = p.matcher(datapointType);
        if (!m.find()) {
            throw new IllegalArgumentException("Unable to parse DPST '" + datapointType + "'");
        }
        StringBuilder dptBuilder = new StringBuilder();
        dptBuilder.append(m.group(1));
        dptBuilder.append('.');
        String suffix = m.group(3);
        if (suffix == null) {
            dptBuilder.append("001");
        } else {
            int suffixLength = suffix.length();
            while (suffixLength++ < 3)
                dptBuilder.append('0');
            dptBuilder.append(suffix);
        }
        return dptBuilder.toString();
    }
}
