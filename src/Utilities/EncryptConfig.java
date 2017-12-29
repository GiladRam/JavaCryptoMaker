package Utilities;


import java.io.Serializable;

public class EncryptConfig implements Serializable {

    private byte[] pm_AESKey;
    private byte[] pm_IV;
    private byte[] fileSignature;

    public EncryptConfig(){

    }
    public byte[] getPm_AESKey() {
        return pm_AESKey;
    }

    public void setPm_AESKey(byte[] pm_AESKey) {
        this.pm_AESKey = pm_AESKey;
    }

    public byte[] getPm_IV() {
        return pm_IV;
    }

    public void setPm_IV(byte[] pm_IV) {
        this.pm_IV = pm_IV;
    }

    public byte[] getFileSignature() {
        return fileSignature;
    }

    public void setFileSignature(byte[] fileSignature) {
        this.fileSignature = fileSignature;
    }

}
