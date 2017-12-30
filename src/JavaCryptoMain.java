
import Utilities.CommandLineUI;

import java.io.IOException;

public class JavaCryptoMain {


    public static void main (String args[]) throws IOException {
        if (args.length < 3){
            System.out.printf("please enter command [Configuration file path] [keystore password] [-keypass]");
            return;
        }

        CommandLineUI main = new CommandLineUI();
        if (args.length == 3){
            main.runUI(args[0],args[1],args[2].toCharArray(), args[2].toCharArray());
        } else {
            main.runUI(args[0],args[1],args[2].toCharArray(), args[3].toCharArray());
        }
    }
}