import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.org.mozilla.javascript.annotations.JSGetter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Preprocessor {
    private String ROOT ;
    private String PARSER_ROOT;
    private String POM_ROOT;
    private String PARSER_DIRECTORY_SUFFIX;
    private String TEST_DIRECTORY_SUFFIX;
    Map<String, String> converstionMap = new HashMap<String, String>();
    ArrayList<String> deviceNames = new ArrayList<String>();
    ArrayList<File> parserFiles = new ArrayList<File>();
    ArrayList<File> testFiles = new ArrayList<File>();

    public Preprocessor(final String root) throws Exception {
        this.ROOT = root;
        PARSER_ROOT = ROOT + "lib/parser/";
        POM_ROOT = PARSER_ROOT + "pom.xml";
        PARSER_DIRECTORY_SUFFIX = "/src/main/java/veriflow/parser/";
        TEST_DIRECTORY_SUFFIX = "/src/test/java/";

        // Call the helper functions to load paths
        loadDeviceNames();
        loadParserPaths(true);
        loadTestPaths(true);
    }

    public Preprocessor(final String root, boolean includeCommon) throws Exception {
        this.ROOT = root;
        PARSER_ROOT = ROOT + "lib/parser/";
        POM_ROOT = PARSER_ROOT + "pom.xml";
        PARSER_DIRECTORY_SUFFIX = "/src/main/java/veriflow/parser";
        TEST_DIRECTORY_SUFFIX = "/src/test/java/";

        // Call the helper functions to load paths
        loadDeviceNames();
        loadParserPaths(includeCommon);
        loadTestPaths(includeCommon);
        loadConvertionMap("key.txt");
    }

    public Preprocessor(final String root, final String mapFileName, boolean includeCommon) throws Exception {
        this.ROOT = root;
        PARSER_ROOT = ROOT + "lib/parser/";
        POM_ROOT = PARSER_ROOT + "pom.xml";
        PARSER_DIRECTORY_SUFFIX = "/src/main/java/veriflow/parser";
        TEST_DIRECTORY_SUFFIX = "/src/test/java/";

        // Call the helper functions to load paths
        loadDeviceNames();
        loadParserPaths(includeCommon);
        loadTestPaths(includeCommon);
        loadConvertionMap(mapFileName);
    }

    public void loadConvertionMap(String fileName) throws Exception {
        final BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String type = "String";

        while (reader.ready()) {
            final String line = reader.readLine();
            if (line.matches("---(.*)---")){
                type = line.replaceAll("---(.*)---", "$1");
                continue;
            }

            final String[] entries = line.split(" -> ");
            converstionMap.put(entries[0], "(" + type + ")" + entries[1]);
        }
    }

    public void loadDeviceNames() throws Exception {
        // Read pom.xml
        File fXmlFile = new File(POM_ROOT);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();

        // Parse the parserName
        NodeList nList = doc.getElementsByTagName("modules");
        Node node = nList.item(0);
        NodeList children = node.getChildNodes();

        for (int temp = 0; temp < children.getLength(); temp++) {
            Node nNode = children.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                deviceNames.add(nNode.getTextContent());
            }
        }
    }

    public void loadParserPaths(boolean includeCommon) {
        for (final String device : deviceNames) {
            if (device.matches("common") && !includeCommon) {
                continue;
            }


            File parserDirectory = new File(PARSER_ROOT + device + PARSER_DIRECTORY_SUFFIX);
            parserDirectory = parserDirectory.listFiles()[0];
            for (final File parser : parserDirectory.listFiles()) {
                if (parser.getName().endsWith("2.java")) {
                    parser.delete();
                    continue;
                }
                parserFiles.add(parser);
            }
        }
    }

    public void loadTestPaths(boolean includeCommon) throws Exception {
        for (final String device : deviceNames) {
            if (device.matches("common") && !includeCommon) {
                continue;
            }

            File parserDirectory = new File(PARSER_ROOT + device + "/src/");
            for (final File temp : parserDirectory.listFiles()) {
                if (temp.getCanonicalPath().matches(".*test.*")) {
                    parserDirectory = temp.listFiles()[0];
                } else {
                    continue;
                }
            }

            if (parserDirectory != null) {
                for (final File uTest : parserDirectory.listFiles()) {
                    if (uTest.isFile()) {
                        testFiles.add(uTest);
                    }
                }
            }
        }
    }
}
