package Utilities;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class FilesUtill {


    public static Properties getConfigurations(String i_configurationPath) throws FileNotFoundException {

        File configFile = new File(i_configurationPath);
        FileReader reader = new FileReader(configFile);
        Properties props = new Properties();
        // load the properties file:
        try {
            props.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return props;
    }

    public static byte[] readFile(String i_fileDirectory) throws IOException {

        byte[] textFromFile;

        Path filePath = FileSystems.getDefault().getPath(i_fileDirectory);
        if(Files.exists(filePath)){
            textFromFile = Files.readAllBytes(filePath);
        }else{
            System.err.printf("The file %s Dose not exists", i_fileDirectory);
            System.exit(1);
            return null;
        }
        return textFromFile;
    }

    public static void writeOutputFile(
            String i_outPutFileDest,
            byte [] data)
    {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(i_outPutFileDest);
            output.write(data);
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if(output != null){
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
