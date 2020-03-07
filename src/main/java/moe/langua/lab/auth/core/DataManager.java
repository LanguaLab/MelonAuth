package moe.langua.lab.auth.core;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class DataManager {

    private File dataRoot;
    private File verificationDataFile;
    private File verificationDataBackUpFile;
    private HashMap<UUID, Byte> verificationMap;

    boolean readVerificationData(File dataRoot) {
        if (!verificationDataFile.exists()) { //create new data file
            try {
                System.out.println(verificationDataFile.getAbsolutePath());
                verificationDataFile.createNewFile();
                verificationMap = new HashMap<>();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {
                FileInputStream in = new FileInputStream(verificationDataFile);
                ObjectInputStream objIn = new ObjectInputStream(in);
                verificationMap =  (HashMap<UUID, Byte>) objIn.readObject(); // this warn cannot be avoided
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    boolean saveVerificationData(File dataRoot){
        verificationDataBackUpFile.delete();
        verificationDataFile.renameTo(verificationDataBackUpFile);
        try {
            verificationDataFile.createNewFile();
            FileOutputStream out = new FileOutputStream(verificationDataFile);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(verificationMap);
            objOut.flush();
            objOut.close();
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    DataManager(File dataRoot) {
        this.dataRoot = dataRoot;
        this.verificationDataFile = new File(dataRoot, "verification.data");
        this.verificationDataBackUpFile = new File(dataRoot, "verification.data.old");
    }

    File getDataRoot(){
        return dataRoot;
    }

    byte getVerificationStatus(UUID uniqueID) {
        return verificationMap.get(uniqueID);
    }

    public void setVerificationStatus(UUID uniqueID, byte status) {
        verificationMap.put(uniqueID,status);
    }
}
