package moe.langua.lab.melonauth;

import moe.langua.lab.melonauth.executor.Auth;
import moe.langua.lab.melonauth.executor.Reload;
import moe.langua.lab.melonauth.listeners.AuthTrigger;
import moe.langua.lab.melonauth.utils.Verifier;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.json.JSONException;
import org.json.JSONObject;
import org.bstats.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Init extends JavaPlugin {
    private boolean isStrictMode;
    private boolean isFreeze;
    private boolean isMute;
    private boolean hasUpdate = false;
    private String latestVersion;

    private File authDataFile = new File(getDataFolder(), "auth.data");
    private File authDataBackUpFile = new File(getDataFolder(), "auth.data.old");
    private ArrayList<Player> verifiedList = new ArrayList<>();
    private ArrayList<Player> waitList = new ArrayList<>();
    private BukkitTask autoSaveTask;
    private Map<String, String> languageMap = new HashMap<>();
    private Map<UUID, Boolean> authMap = new HashMap<>();
    private Map<UUID, Verifier> verifierMap = new HashMap<>();


    @Override
    public void onEnable() {
        if (!this.getDataFolder().exists()) {
            getLogger().info("First setup detected. Setting up your MelonAuth Config files.");
            this.saveDefaultConfig();
            this.setup();
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            try {
                getLogger().info(languageMap.get("prefix") + languageMap.get("checking_update"));
                JSONObject result = apiGET(new URL("https://api.github.com/repos/LanguaLab/MelonAuth/releases/latest"));
                latestVersion = result.getString("tag_name");
                if (!this.getDescription().getVersion().equalsIgnoreCase(latestVersion)) {
                    hasUpdate = true;
                    this.getLogger().info(languageMap.get("prefix") + this.applyPlaceholder(languageMap.get("update_notice"), latestVersion, "https://github.com/LanguaLab/MelonAuth/releases/" + latestVersion));
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }, 0, 20 * 60 * 60 * 12/*every 12 hours*/); //check update
        isStrictMode = getConfig().getBoolean("StrictMode");
        isFreeze = getConfig().getBoolean("Freeze");
        isMute = getConfig().getBoolean("Mute");
        Metrics metrics = new Metrics(this, 6472);
        loadLanguage(this.getConfig().getString("Language")); //load language settings
        authMap = loadAuthMap(); //load saved auth data
        this.getCommand("auth").setExecutor(new Auth(this, verifiedList, authMap, verifierMap, languageMap));
        this.getCommand("reload").setExecutor(new Reload(this, languageMap));
        this.getServer().getPluginManager().registerEvents(new AuthTrigger(this, verifiedList, authMap, verifierMap, languageMap), this);
        int autoSaveTimeInSecond = getConfig().getInt("AutoSave");
        autoSaveTask = Bukkit.getScheduler().runTaskTimer(this, this::saveAuthMap, autoSaveTimeInSecond * 20, autoSaveTimeInSecond * 20);
    }

    @Override
    public void onDisable() {
        saveAuthMap();
    }

    private void setup() {
        writeResourceFile("en_US.yml");
        writeResourceFile("zh_CN.yml");
    }

    private URL getResourceFileURL(String fileName) {
        return this.getClassLoader().getResource(fileName);
    }

    private void writeResourceFile(String name) {
        File fileToWrite = new File(getDataFolder(), name);
        try {
            InputStream inputStream = getInputStreamFromConnection(getResourceFileURL(name).openConnection());
            final OutputStream out = new FileOutputStream(fileToWrite);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream getInputStreamFromConnection(URLConnection connection) throws IOException {
        connection.setUseCaches(false);
        return connection.getInputStream();
    }

    private HashMap<UUID, Boolean> loadAuthMap() {
        if (!authDataFile.exists()) { //create new data file
            try {
                authDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                this.getLogger().warning("Failed to create data file. Plugin will be disabled automatically.");
                getPluginLoader().disablePlugin(this);
            }
            return new HashMap<>();
        } else {
            try {
                FileInputStream in = new FileInputStream(authDataFile);
                ObjectInputStream objIn = new ObjectInputStream(in);
                return (HashMap<UUID, Boolean>) objIn.readObject(); // this warn cannot be avoided
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                this.getLogger().warning("Failed to read data file. Plugin will be disabled automatically.");
                getPluginLoader().disablePlugin(this);
                return new HashMap<>();
            }
        }
    }

    private void saveAuthMap() {
        authDataBackUpFile.delete();
        authDataFile.renameTo(authDataBackUpFile);
        try {
            authDataFile.createNewFile();
            FileOutputStream out = new FileOutputStream(authDataFile);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(authMap);
            objOut.flush();
            objOut.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            this.getLogger().warning("Failed to write data file. Plugin will be disabled automatically.");
            getPluginLoader().disablePlugin(this);
        }
    }

    private void loadLanguage(String lang) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), lang + ".yml"));
        languageMap.clear();
        for (String x : config.getKeys(true)) {
            languageMap.put(x, config.getString(x).replace('&', '§').replace("§§", "&"));
        }
    }

    public String applyPlaceholder(String in, String... placeholders) {
        for (int index = 0; index < placeholders.length; index++) {
            in = in.replace("{" + index + "}", placeholders[index]);
        }
        return in;
    }

    public boolean isStrictMode() {
        return isStrictMode;
    }

    public boolean isFreeze() {
        return isFreeze;
    }

    public boolean isMute() {
        return isMute;
    }

    public boolean hasUpdate() {
        return hasUpdate;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void reload() {
        this.reloadConfig();
        isStrictMode = getConfig().getBoolean("StrictMode");
        isFreeze = getConfig().getBoolean("Freeze");
        isMute = getConfig().getBoolean("Mute");
        loadLanguage(this.getConfig().getString("Language"));
        autoSaveTask.cancel();
        int autoSaveTimeInSecond = getConfig().getInt("AutoSave");
        autoSaveTask = Bukkit.getScheduler().runTaskTimer(this, this::saveAuthMap, autoSaveTimeInSecond * 20, autoSaveTimeInSecond * 20);
    }

    public static JSONObject apiGET(URL reqURL) throws JSONException, IOException {
        HttpsURLConnection connection = (HttpsURLConnection) reqURL.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-type", "application/json");
        connection.setInstanceFollowRedirects(false);
        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String tmpString;
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            tmpString = bufferedReader.readLine();
            if (tmpString == null) break;
            stringBuilder.append(tmpString);
        }
        return new JSONObject(stringBuilder.toString());
    }

    public ArrayList<Player> getWaitList() {
        return waitList;
    }

    @Override
    public InputStream getResource(String filename) {
        return super.getResource(filename);
    }
}
