package Utilities;

import CipherCommands.DecryptCommand;
import CipherCommands.EncryptCommand;
import java.time.LocalDateTime;
import java.io.FileNotFoundException;
import java.util.Properties;

public class CommandLineUI {


    final String f_appTitle = "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n" +
            "________ File Crypto Maker _______" +
            "\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$";
    final String f_loadingPropStr = "\n\tLoading configuration from %s...\n\n";
    final String f_configFileNotFoundExceptio = "Could not find configuration file.\n %s";

    public void runUI(String i_UserCommand,
                      String i_configFilePath,
                      char[] i_keyStorePassword,
                      char[] i_keyPassword) {

        Properties userConfiguration = null;
        System.out.println(f_appTitle);

        System.out.printf(f_loadingPropStr,i_configFilePath);
        try{
            userConfiguration = FilesUtill.getConfigurations(i_configFilePath);
        }catch (FileNotFoundException ex){
            System.out.printf(f_configFileNotFoundExceptio, ex.getMessage());
        }
    try{
        switch (i_UserCommand) {
            case "encrypt":
                EncryptCommand encryptFile = new EncryptCommand(
                        userConfiguration,
                        i_keyStorePassword,
                        i_keyPassword);
                encryptFile.createFileSignature();
                encryptFile.encryptAndWriteOutCipherData();
                break;
            case "decrypt":
                DecryptCommand decryptFile = new DecryptCommand(
                        userConfiguration,
                        i_keyStorePassword,
                        i_keyPassword);
                decryptFile.decryptAndWriteDecryptedData();
                break;
            default:
                System.out.printf("Please enter a command FileCryptoMaker [encrypt | decrypt]");
        }
        } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
    }
    }

    public static void CommandPrintOut(
            String i_mode,
            String i_commandOutPutToPrint){

        System.out.printf("\t\t%s - %s: %s \n", LocalDateTime.now(), i_mode, i_commandOutPutToPrint);

    }
}
