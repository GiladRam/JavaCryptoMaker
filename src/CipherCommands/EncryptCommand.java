package CipherCommands;


import Utilities.CommandLineUI;
import Utilities.CryptoUtill;
import Utilities.EncryptConfig;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.*;
import java.util.Properties;

public class EncryptCommand extends CipherCommand {

    protected static final String psf_outPutFile = "./encrypted.txt";
    protected static final String psf_encryptionConfigOutPutFile = "./encryptionConfigOutPut.ser";
    protected IvParameterSpec pm_IV;
    protected SecretKey pm_symmetricKey;

    public EncryptCommand(
            Properties i_userConfiguration,
            char[] i_keyStorePassword,
            char[] i_keypass)
            throws
            NoSuchAlgorithmException,
            NoSuchProviderException,
            KeyStoreException,
            NoSuchPaddingException,
            IOException {
        super(i_userConfiguration, i_keyStorePassword, i_keypass);
        pm_EncryptConfig = new EncryptConfig();
    }

    public void createFileSignature()
            throws InvalidKeyException,
            SignatureException,
            UnrecoverableKeyException,
            NoSuchAlgorithmException,
            KeyStoreException {
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.Encryption.name(),
                "Creating a file signature using the private key...");
        PrivateKey userPrivateKey = (PrivateKey) pm_KeyStore.getKey(pm_privateKeyAlias, pm_key_pass);
        pm_signatureProvider.initSign(userPrivateKey);
        pm_signatureProvider.update(pm_targetFileContent);
        byte[] fileSignature = pm_signatureProvider.sign();
        pm_EncryptConfig.setFileSignature(fileSignature);
    }

    public void encryptAndWriteOutCipherData()
            throws
            KeyStoreException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            NoSuchPaddingException,
            BadPaddingException,
            IllegalBlockSizeException,
            NoSuchProviderException,
            IOException {

        CommandLineUI.CommandPrintOut(
                enum_CipherModes.Encryption.name(),
                "Getting public key with alias: " + pm_keyBAlias);
        PublicKey userBPublicKey = pm_KeyStore.getCertificate(pm_keyBAlias).getPublicKey();

        generateKey();
        generateIV();

        CommandLineUI.CommandPrintOut(
                enum_CipherModes.Encryption.name(),
                "Encrypting and writing data to " + pm_targetFileDirectory);
        pm_CipherProvider.init(Cipher.ENCRYPT_MODE, pm_symmetricKey, pm_IV);
        CryptoUtill.WriteCipherOutPut(psf_outPutFile, pm_CipherProvider, pm_targetFileContent);
        setEncryptionConfig(userBPublicKey);
        writeEncryptionConfigFile();
    }

    private void setEncryptionConfig(PublicKey userBPublicKey)
            throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.Encryption.name(),
                "Encrypting symmetric key");
        Cipher rsaCipher = Cipher.getInstance(pm_keyEncryptionAlgorithm);
        rsaCipher.init(Cipher.ENCRYPT_MODE, userBPublicKey);
        byte[] cipherAESKey = rsaCipher.doFinal(pm_symmetricKey.getEncoded());
        pm_EncryptConfig.setPm_AESKey(cipherAESKey);
        pm_EncryptConfig.setPm_IV(pm_IV.getIV());
    }

    private void generateKey()
            throws
            NoSuchAlgorithmException,
            NoSuchProviderException {
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.Encryption.name(),
                "Generating symmetric key...");
        SecureRandom random = getRandomness();
        KeyGenerator keyGenerator = KeyGenerator.getInstance(pm_cipherAlgorithm);
        keyGenerator.init(pm_symmetricKeySize, random);
        pm_symmetricKey = keyGenerator.generateKey();
    }

    private void generateIV()
            throws
            NoSuchProviderException,
            NoSuchAlgorithmException {
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.Encryption.name(),
                "Generating IV...");
        byte[] randomByteArray = new byte[16];
        SecureRandom random = getRandomness();
        random.nextBytes(randomByteArray);
        pm_IV = new IvParameterSpec(randomByteArray);
    }

    private SecureRandom getRandomness()
            throws
            NoSuchProviderException,
            NoSuchAlgorithmException {
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.Encryption.name(),
                "Creating secure random");
        SecureRandom random = SecureRandom.getInstance(pm_RNGAlgorithm,pm_SecureRandomProvider);
        random.setSeed(System.currentTimeMillis());
        return random;
    }

    private void writeEncryptionConfigFile()
            throws
            IOException {
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.Encryption.name(),
                "Saving encryption configuration data in " + psf_encryptionConfigOutPutFile);
        FileOutputStream fileOut =
                new FileOutputStream(psf_encryptionConfigOutPutFile);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(pm_EncryptConfig);
        out.close();
        fileOut.close();
        CommandLineUI.CommandPrintOut(
                enum_CipherModes.Encryption.name(),
                "The encryption configuration file was successfully saved");
    }
}

