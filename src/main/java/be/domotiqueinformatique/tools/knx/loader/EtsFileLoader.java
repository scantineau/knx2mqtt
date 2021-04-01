package be.domotiqueinformatique.tools.knx.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class EtsFileLoader {
    private static final Logger logger = LogManager.getLogger(EtsFileLoader.class);
    private String etsFilePath;

    private EtsFileLoader(String etsFilePath) {
    }

    public static EtsFileLoader anEtsFileLoader(String etsFilePath) {
        return new EtsFileLoader(etsFilePath);
    }

    public static void loadFile(String path){
        InputStream inputStream = anEtsFileLoader(path).load();
        EtsProjectLoader etsProjectLoader = new EtsProjectLoader();
        try {
            etsProjectLoader.load(inputStream);
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    private InputStream load() {
        ensureFileExists();
        try (ZipFile zf = new ZipFile(etsFilePath)) {
            ZipEntry xml0File = zf.stream()
                    .filter(this::containsProjectXmlFile)
                    .map(this::get0FilePathFromSameParent)
                    .map(zf::getEntry)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unable to locate 0.xml in project"));
            return zf.getInputStream(xml0File);
        } catch (Exception e) {
            logger.fatal("Error reading project file " + etsFilePath, e);
            System.exit(1);
        }
        return null;
    }

    private void ensureFileExists() {
        File projectFile = new File(etsFilePath);
        if (!projectFile.exists()) {
            logger.fatal("ETS project file " + etsFilePath + " does not exit");
            System.exit(1);
        }
    }

    private String get0FilePathFromSameParent(ZipEntry zipEntry) {
        return zipEntry.getName().substring(0, zipEntry.getName().indexOf('/') + 1) + "0.xml";
    }

    private boolean containsProjectXmlFile(ZipEntry zipEntry) {
        return zipEntry.getName().toLowerCase().endsWith("project.xml");
    }
}
