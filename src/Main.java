import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(final String[] args) throws Exception {
        Preprocessor preprocessor = new Preprocessor("/home/turja/IdeaProjects/devices/", "intfKeys.txt",false);

        /*for (final File parser : preprocessor.parserFiles) {
            for (Map.Entry<String, String> entry : preprocessor.converstionMap.entrySet()) {
                final String key = "this.deviceHelper.bindPropertyToDevice\\((Property.)?" + entry.getKey() + ", (.*)\\);";

                //System.out.println(key);
                final String replace = "DdmApi\\." + entry.getValue() + "\\(this.device, $2\\);";

                FileTools.replaceStringsInFile(parser, key, replace);
            }
        }*/


        Map<String, String> keyToreplaceMap = new HashMap<String ,String>();
        keyToreplaceMap.put("int(\\S+)\\.getProperties\\(\\)", "device.getInterfaceProperties\\(\\).get\\(int$1\\.getName\\(\\)\\)");
        keyToreplaceMap.put("int(\\S+)\\.findPropertyValueByKey\\((\\S+)\\)", "device.findInterfacePropertyValueByKey\\(int$1.getName\\(\\), $2\\)");
        for (final Map.Entry<String, String> entry : preprocessor.converstionMap.entrySet()) {
            final String searchKey = "device.findInterfacePropertyValueByKey\\((\\S+).getName\\(\\), (Property.)?" + entry.getKey() + "\\)";
            String replace = entry.getValue();
            final String type = replace.replaceAll("\\((\\S+)\\).*", "$1");
            final String funcName = replace.replaceAll("\\(\\S+\\)(.*)", "$1");
            final String getFuncName = funcName.replaceAll("set", "get");

            replace = "$1." + getFuncName + "\\(\\)";

            keyToreplaceMap.put(searchKey, replace);
        }

        for (final File uTests : preprocessor.testFiles) {
            FileTools.replaceStringsInFileFromMap(uTests, keyToreplaceMap);
        }
    }
}
