import java.io.*;
import java.util.Map;

public class FileTools {
    /**
     *
     * @param file File to be updated
     * @param key string to be replaced
     * @param replace string to replace with
     */
    public static void replaceStringsInFile(File file, String key, String replace) throws Exception {
        // Reader to read the file
        final FileReader reader = new FileReader(file);
        final BufferedReader bufferedReader = new BufferedReader(reader);

        // New file to write the modified version of the given 'file'
        final File newFile = FileTools.getNewFilefromModifiedOldFileName(file);
        final FileWriter writer = new FileWriter(newFile);
        final BufferedWriter bufferedWriter = new BufferedWriter(writer);

        int lineno = 1;
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line.matches(".*" + key + ".*")) {
                System.out.println("\nDescription:\n\tLineNo:" + lineno + "\n\tFile: " + file.getName() + "\n\tFound: " + line);
                line = line.replaceAll(key, replace);
                System.out.println("\tReplaced Line: " + line);
            }

            bufferedWriter.write(line);
            bufferedWriter.write("\n");
            lineno++;
        }

        bufferedWriter.close();
        bufferedReader.close();
        reader.close();
        writer.close();

        FileTools.replaceFile(newFile, file);
    }

    public static void replaceStringsInFileFromMap(File file, Map<String, String> map) throws Exception {
        // Reader to read the file
        final FileReader reader = new FileReader(file);
        final BufferedReader bufferedReader = new BufferedReader(reader);

        // New file to write the modified version of the given 'file'
        final File newFile = FileTools.getNewFilefromModifiedOldFileName(file);
        final FileWriter writer = new FileWriter(newFile);
        final BufferedWriter bufferedWriter = new BufferedWriter(writer);

        int lineno = 1;
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            for (final Map.Entry<String, String> entry : map.entrySet()) {
                final String key = entry.getKey();
                final String replace = entry.getValue();
                if (line.matches(".*" + key + ".*")) {
                    System.out.println("\nDescription:\n\tLineNo:" + lineno + "\n\tFile: " + file.getName() + "\n\tFound: " + line);
                    line = line.replaceAll(key, replace);
                    System.out.println("\tReplaced Line: " + line);
                }
            }

            bufferedWriter.write(line);
            bufferedWriter.write("\n");
            lineno++;
        }

        bufferedWriter.close();
        bufferedReader.close();
        reader.close();
        writer.close();

        FileTools.replaceFile(newFile, file);
    }

    public static File getNewFilefromModifiedOldFileName(final File file) {
        final String oldFileName = file.getName();
        final String newFileName = oldFileName.replaceAll("(.*)\\.java", "$1_2\\.java");
        final String newFilePath = file.getAbsolutePath().replaceAll(oldFileName, newFileName);
        File newFile;
        if ((newFile = new File(newFilePath)).exists()) {
            newFile.delete();
        }

        return new File(newFilePath);
    }

    public static void replaceFile(final File file1, final File file2) {
        file1.renameTo(file2);
    }

    public static void printMatch(File file, Map<String, String> map) throws Exception {
        // Reader to read the file
        final FileReader reader = new FileReader(file);
        final BufferedReader bufferedReader = new BufferedReader(reader);

        int lineno = 1;
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            for (final Map.Entry<String, String> entry : map.entrySet()) {
                final String key = entry.getKey();
                final String replace = entry.getValue();
                if (line.matches(".*" + key + ".*")) {
                    System.out.println("\nDescription:\n\tLineNo:" + lineno + "\n\tFile: " + file.getName() + "\n\tFound: " + line);
                    line = line.replaceAll(key, replace);
                    System.out.println("\tReplaced Line: " + line);
                }
            }

            lineno++;
        }

        bufferedReader.close();
        reader.close();
    }
}
