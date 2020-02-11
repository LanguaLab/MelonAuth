package moe.langua.lab.melonauth.runnable;

import moe.langua.lab.melonauth.Init;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GetSkinImage extends BukkitRunnable {
    protected BufferedImage skinImage;
    private Init instance;
    private Map<String, String> languageMap;
    private Player player;

    public GetSkinImage(Init instance, Map<String, String> languageMap, Player player) {
        this.instance = instance;
        this.languageMap = languageMap;
        this.player = player;
    }

    @Override
    public void run() {

        JSONObject profile = null;
        try {
            profile = getProfile();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (profile == null) return;

        JSONObject value;
        try {
            value = new JSONObject(new String(Base64.getDecoder().decode(profile.getJSONArray("properties").getJSONObject(0).getString("value"))));
        } catch (JSONException e) {
            e.printStackTrace();
            this.runTaskAsynchronously(instance);
            return;
        }

        JSONObject textures;
        try {
            textures = value.getJSONObject("textures");
            if (textures.has("SKIN")) {
                URL skinFileURL = new URL(textures.getJSONObject("SKIN").getString("url").replace("http://", "https://"));
                skinImage = readImage(skinFileURL);
            } else {
                skinImage = ImageIO.read(instance.getResource(((player.getUniqueId().hashCode() & 1) != 0 ? "alex" : "steve") + ".png"));
            }
        } catch (IOException | JSONException | InterruptedException e) {
            e.printStackTrace();
            player.sendMessage(languageMap.get("prefix") + languageMap.get("connection_error"));
            this.runTaskAsynchronously(instance);
        }
    }

    private JSONObject getProfile() throws InterruptedException {
        if (!player.isOnline()) return null;
        try {
            return Init.apiGET(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + player.getUniqueId().toString().replace("-", "")));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            player.sendMessage(languageMap.get("prefix") + languageMap.get("connection_error"));
            Thread.sleep(20 * 1000/* try again in 20 seconds */);
            return getProfile();
        }
    }

    private BufferedImage readImage(URL skinFileURL) throws InterruptedException {
        if (!player.isOnline()) return null;
        try {
            return ImageIO.read(skinFileURL);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(languageMap.get("prefix") + languageMap.get("connection_error"));
            Thread.sleep(20 * 1000/* try again in 20 seconds */);
            return readImage(skinFileURL);
        }
    }

}
