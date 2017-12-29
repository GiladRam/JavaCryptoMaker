package CipherCommands;

import Utilities.CommandLineUI;
import Utilities.CryptoUtill;
import Utilities.EncryptConfig;
import Utilities.FilesUtill;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.util.Properties;

public abstract class CipherCommand {
    protected enum enum_CipherModes {
        General,
        Encryption,
        Decryption
    }

    protected KeyStore pm_KeyStore;
    protected EncryptConfig pm_EncryptConfig;
    protected Cipher pm_CipherProvider;
    protected Signature pm_signatureProvider;
    protected String pm_cipherAlgorithm;
    protected String pm_RNGAlgorithm;
    protected String pm_SecureRandomProvider;
    protected String pm_privateKeyAlias;
    protected String pm_keyBAlias;
    protected String pm_keyEncryptionAlgorithm;
    protected String pm_targetFileDirectory;
    protected byte[] pm_targetFileContent;
    protected char[] pm_key_pass;
    protected int pm_symmetricKeySize;


    public CipherCommand(
            Properties i_userConfiguration,
            char[] i_keyStorePassword,
            char[] i_keypass)
            throws
            NoSuchProviderException,
            NoSuchAlgorithmException,
            KeyStoreException,
            NoSuchPaddingException,
            IOException {
        LoadAndSetParams(i_userConfiguration, i_keyStorePassword, i_keypass);

    }

    private void LoadAndSetParams(
            Properties i_userConfiguration,
            char[] i_keyStorePassword,
            char[] i_keypass)
            throws IOException,
            KeyStoreException,
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            NoSuchProviderException {

        String cipherProvider = i_userConfiguration.getProperty("cipherProvider" , "SunJCE");
        String cipherTransformation = i_userConfiguration.getProperty("cipherTransformation", "AES/CBC/PKCS5Padding");
        String signatureProvider = i_userConfiguration.getProperty("signatureProvider", "SunRsaSign");
        String signatureAlgo = i_userConfiguration.getProperty("signatureAlgorithm", "SHA256withRSA");
        String keyStorePath = i_userConfiguration.getProperty("keyStorePath");
        pm_symmetricKeySize = Integer.parseInt(i_userConfiguration.getProperty("symmetryKeySize", "128"));
        pm_cipherAlgorithm = i_userConfiguration.getProperty("cipherAlgorithm", "AES");
        pm_keyEncryptionAlgorithm = i_userConfiguration.getProperty("symmetryKeyEncryptionAlgo", "RSA");
        pm_privateKeyAlias = i_userConfiguration.getProperty("privateAlias");
        pm_keyBAlias = i_userConfiguration.getProperty("userBAlias");
        pm_targetFileDirectory = i_userConfiguration.getProperty("targetFileDirectory");
        pm_RNGAlgorithm = i_userConfiguration.getProperty("RNGAlgo", "SHA1PRNG");
        pm_SecureRandomProvider = i_userConfiguration.getProperty("SecureRandomProvider", "SUN");
        pm_key_pass = i_keypass;

        setCipherProvider(cipherProvider, cipherTransformation);
        setKeyStore(i_keyStorePassword, keyStorePath);
        readTargetFileContent();
        setSignatureProvider(signatureAlgo, signatureProvider);
    }

    private void readTargetFileContent() throws IOException {
        pm_targetFileContent = FilesUtill.readFile(pm_targetFileDirectory);
    }

    private void setKeyStore(
            char[] i_keyStorePassword,
            String i_keyStorePath)
            throws
            IOException,
            KeyStoreException {
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.General.name(),
                "Setting Key Store");
        pm_KeyStore = CryptoUtill.getUserKeyStore(i_keyStorePath, i_keyStorePassword);
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.General.name(),
                "Key Store was set successfully");
    }

    private void setCipherProvider(
            String i_cipherProvider,
            String i_cipherTransformation)
            throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            NoSuchProviderException {
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.General.name(),
                "Setting Cipher provider: " + i_cipherProvider);
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.General.name(),
                "Setting Cipher transformation: " + i_cipherTransformation);
        pm_CipherProvider = Cipher.getInstance(
                i_cipherTransformation,
                i_cipherProvider);
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.General.name(),
                "Cipher provider and Transformation were set successfully");

    }

    private void setSignatureProvider(
            String i_algorithm,
            String i_provider)
            throws
            NoSuchProviderException,
            NoSuchAlgorithmException {
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.General.name(),
                "Setting signature provider: " + i_provider);
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.General.name(),
                "Setting signature algorithm: " + i_algorithm);
        pm_signatureProvider = Signature.getInstance(i_algorithm, i_provider);
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.General.name(),
                "Signature provider and algorithm were set successfully ");
    }


}
