package CipherCommands;

import Utilities.CommandLineUI;
import Utilities.CryptoUtill;
import Utilities.EncryptConfig;
import Utilities.FilesUtill;


import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.*;
import java.util.Properties;

public class DecryptCommand extends CipherCommand {

    protected static final String psf_outPutFile = "./decrypted.txt";

    public DecryptCommand(
            Properties i_userConfiguration,
            char[] i_keyStorePassword,
            char[] i_keypass)
            throws
            IOException,
            ClassNotFoundException,
            NoSuchAlgorithmException,
            NoSuchProviderException,
            KeyStoreException,
            NoSuchPaddingException {
        super(i_userConfiguration, i_keyStorePassword, i_keypass);
        readEncryptConfig();

    }

    private void readEncryptConfig() throws IOException {
        ObjectInputStream objectIn = null;
        FileInputStream fileIn = null;
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.Decryption.name(),
                "Reading encryption configuration file from "
                        + EncryptCommand.psf_encryptionConfigOutPutFile);
        try{

            fileIn = new FileInputStream(EncryptCommand.psf_encryptionConfigOutPutFile);
            objectIn = new ObjectInputStream(fileIn);
            pm_EncryptConfig = (EncryptConfig) objectIn.readObject();
            objectIn.close();
            fileIn.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(fileIn != null){
                fileIn.close();
            }
        }
    }

    public boolean verifyMessage(byte[] i_decryptedData)
            throws InvalidKeyException,
            KeyStoreException,
            SignatureException {
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.Decryption.name(),
                "Using host public key to verify" +
                        " file content with signature from encryption " +
                        "configuration file...");
        PublicKey userAPublicKey = pm_KeyStore.getCertificate(pm_keyBAlias).getPublicKey();
        pm_signatureProvider.initVerify(userAPublicKey);
        pm_signatureProvider.update(i_decryptedData);
        return pm_signatureProvider.verify(pm_EncryptConfig.getFileSignature());
    }

    public void decryptAndWriteDecryptedData()
            throws
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            UnrecoverableKeyException,
            NoSuchAlgorithmException,
            KeyStoreException,
            NoSuchPaddingException,
            BadPaddingException,
            IllegalBlockSizeException,
            SignatureException {

        byte[] symmetricKey = decryptSymmetricKey();
        byte[] decryptedData = decryptContent(symmetricKey);
        boolean messageVerification = verifyMessage(decryptedData);
        if (messageVerification ) {
            CommandLineUI.CommandPrintOut(
                    enum_CipherModes.Decryption.name(),
                    "File signature was matched!!!");
            CommandLineUI.CommandPrintOut(
                    enum_CipherModes.Decryption.name(),
                    "Writing decrypted data to " + psf_outPutFile);
            FilesUtill.writeOutputFile(psf_outPutFile, decryptedData);
        } else {
            CommandLineUI.CommandPrintOut(
                    enum_CipherModes.Decryption.name(),
                    "Could not match signatures!!!");

        }
    }

    private byte[] decryptContent(byte[] symmetricKey)
            throws
            InvalidKeyException,
            InvalidAlgorithmParameterException {
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.Decryption.name(),
                "Decrypting file content...");
        SecretKeySpec keySpec = new SecretKeySpec(symmetricKey, pm_cipherAlgorithm);
        IvParameterSpec ivSpec = new IvParameterSpec(pm_EncryptConfig.getPm_IV());
        pm_CipherProvider.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return CryptoUtill.readCipherInputFromFile(pm_targetFileDirectory, pm_CipherProvider);
    }

    private byte[] decryptSymmetricKey()
            throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException,
            UnrecoverableKeyException,
            KeyStoreException {
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.Decryption.name(),
                "Getting private key from keystore with alias: " + pm_privateKeyAlias);
        PrivateKey userBPrivateKey = (PrivateKey) pm_KeyStore.getKey(pm_privateKeyAlias, pm_key_pass);
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.Decryption.name(),
                "Decrypting symmetric key from configuration file...");
        Cipher rsaCipher = Cipher.getInstance(pm_keyEncryptionAlgorithm);
        rsaCipher.init(Cipher.DECRYPT_MODE, userBPrivateKey);
        return rsaCipher.doFinal(pm_EncryptConfig.getPm_AESKey());
    }
}
