package Utilities;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

public class CryptoUtill {

    public static KeyStore getUserKeyStore(
            String i_keyStorePath, char[] i_keyStorePassword) throws KeyStoreException, IOException {
        KeyStore keyStore = KeyStore.getInstance("JKS");

        FileInputStream keyStoreFileInputStream = null;
        try {
            keyStoreFileInputStream = new FileInputStream(i_keyStorePath);
            try {
                keyStore.load(keyStoreFileInputStream, i_keyStorePassword);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            }
        } finally {
            if (keyStoreFileInputStream != null) {
                keyStoreFileInputStream.close();
            }
        }
        return keyStore;
    }

    public static void WriteCipherOutPut(
            String i_outPutFileDest,
            Cipher i_cipher,
            byte [] data)
    {
        FileOutputStream output = null;
        CipherOutputStream cipherOut = null;
        try {
            output = new FileOutputStream(i_outPutFileDest);
            cipherOut = new CipherOutputStream(output,i_cipher);
            cipherOut.write(data);
            cipherOut.close();
            output.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(cipherOut != null){
                    cipherOut.close();
                }
                if(output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] readCipherInputFromFile(
            String i_fileDirectory,
            Cipher i_cipher
            ){
        ArrayList<Byte> fileDataInBytes = new ArrayList<Byte>();
        Path filePath;
        int currentByte;
        FileInputStream inputStream = null;
        CipherInputStream cipherInputStream = null;
        try{
            filePath = FileSystems.getDefault().getPath(i_fileDirectory);
            if(Files.exists(filePath)){
                inputStream = new FileInputStream(i_fileDirectory);
                cipherInputStream = new CipherInputStream(inputStream,i_cipher);
                currentByte = cipherInputStream.read();
                while (currentByte != -1){
                    fileDataInBytes.add((byte)currentByte);
                    currentByte = cipherInputStream.read();
                }
            }else{
                System.err.printf("The file %s Dose not exists", i_fileDirectory);
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream != null){
                    inputStream.close();
                }
                if(cipherInputStream != null) {
                    cipherInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getByteArray(fileDataInBytes);
    }

    private static byte[] getByteArray( ArrayList<Byte> i_byteArrayList) {
            byte[] bytes = new byte[i_byteArrayList.size()];
            for (int i = 0; i < i_byteArrayList.size(); i++) {
                bytes[i] = i_byteArrayList.get(i);
            }
            return bytes;
        }
}
