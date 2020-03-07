package moe.langua.lab.auth.core;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import moe.langua.lab.auth.utils.Verifier;
import moe.langua.lab.auth.utils.exceptions.PluginLoadFailedException;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class VerificationManager {
    private boolean isStrictMode;
    private String language;

    private DataManager dataManager;
    private HashMap<String, String> languageMap;
    private HashMap<UUID, Verifier> verifyingMap;
    private Logger logger;

    private boolean setup(File dataRoot) {
        dataManager = new DataManager(dataRoot);
        if (!dataRoot.exists()) {
            dataRoot.mkdir();
            writeResourceFile("config.yml");
            writeResourceFile("en_US.yml");
            writeResourceFile("zh_CN.yml");
        }
        if (!dataManager.readVerificationData(dataManager.getDataRoot())) return false;
        return reload();
    }

    private void writeResourceFile(String fileName) {
        File fileToWrite = new File(dataManager.getDataRoot().getAbsolutePath() + "/" + fileName);
        try {
            FileWriter writer = new FileWriter(fileToWrite, false);
            writer.write(getResourceFile(fileName));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> readConfigFile(File dataRoot, String fileName) {
        try {
            File targetFile = new File(dataRoot.getAbsolutePath() + "/" + fileName);
            if (!targetFile.exists()) writeResourceFile(fileName);
            YamlReader reader = new YamlReader(new FileReader(targetFile));
            HashMap<String, String> readedMap = (HashMap<String, String>) reader.read();
            YamlReader originalReader = new YamlReader(getResourceFile(fileName));
            HashMap<String, String> originalMap = (HashMap<String, String>) originalReader.read();
            boolean needToSave = false;
            for (String x : originalMap.keySet()) {
                if (!readedMap.containsKey(x)) {
                    readedMap.put(x, originalMap.get(x));
                    needToSave = true;
                    logger.warning("Adding new item " + x + " to " + fileName + ", the value of it is \"" + readedMap.get(x) + "\" by default.");
                }
            }
            for (String x : readedMap.keySet()) {
                if (!originalMap.containsKey(x)) {
                    logger.warning("The item named " + x + " in the file " + fileName + " is deprecated, remove it manually to avoid this warning.");
                }
            }
            if (needToSave) {
                YamlWriter writer = new YamlWriter(new FileWriter(targetFile));
                writer.write(readedMap);
                writer.close();
            }
            return readedMap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public VerificationManager(File dataRoot, Logger logger) throws PluginLoadFailedException {
        this.logger = logger;
        boolean success = setup(dataRoot);
        if (!success)
            throw new PluginLoadFailedException("Failed to load plugin, please check the log for further information.");
    }

    public byte getVerificationStatus(UUID uniqueID) {
        return dataManager.getVerificationStatus(uniqueID);
    }

    public void setVerificationStatus(UUID uniqueID, byte status) {
        dataManager.setVerificationStatus(uniqueID, status);
    }

    public boolean saveVerificationMap() {
        return dataManager.saveVerificationData(dataManager.getDataRoot());
    }

    public String getLanguage(String path) {
        return languageMap.get(path);
    }

    public String getLanguage(String path, String... placeholders) {
        String target = getLanguage(path);
        for (int index = 0; index < placeholders.length; index++)
            target = target.replace("{" + index + "}", placeholders[index]);
        return target;
    }

    public boolean isStrictMode() {
        return isStrictMode;
    }

    public String getLanguage() {
        return language;
    }

    public boolean reload() {
        HashMap<String, String> configMap = readConfigFile(dataManager.getDataRoot(), "config.yml");
        if (configMap == null) return false;
        isStrictMode = Boolean.getBoolean(configMap.get("strict_mode"));
        language = configMap.get("language");
        File languageFile = new File(dataManager.getDataRoot().getAbsolutePath() + language + ".yml");
        if (!languageFile.exists()) {
            logger.warning("Language file " + languageFile.getName() + " doesn't exist. Please check your configuration. Trying to use en_US instead.");
            languageMap = readConfigFile(dataManager.getDataRoot(), "en_US.yml");
        } else {
            languageMap = readConfigFile(dataManager.getDataRoot(), languageFile.getName());
        }
        return languageMap != null;
    }

    public URL getResourceFileURL(String fileName) {
        return this.getClass().getResource(fileName);
    }

    public boolean isVerifying(UUID uniqueID) {
        return verifyingMap.containsKey(uniqueID);
    }

    public void setVerifier(UUID uniqueID, Verifier verifier) {
        verifyingMap.put(uniqueID, verifier);
    }

    public Verifier getVerifier(UUID uniqueID) {
        return verifyingMap.get(uniqueID);
    }

    private String getResourceFile(String fileName) {
        BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(fileName))));
        StringBuilder builder = new StringBuilder();
        String line = "";
        try {
            while ((line = in.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
