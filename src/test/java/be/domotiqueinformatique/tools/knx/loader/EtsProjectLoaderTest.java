package be.domotiqueinformatique.tools.knx.loader;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

class EtsProjectLoaderTest {

    @Test
    void test() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        EtsProjectLoader etsProjectLoader = new EtsProjectLoader();
        etsProjectLoader.load(getClass().getClassLoader().getResourceAsStream("0_with_locations.xml")).forEach(System.out::println);
    }
}