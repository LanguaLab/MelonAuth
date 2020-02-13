package moe.langua.lab.melonauth.runnable;

import moe.langua.lab.melonauth.Init;
import moe.langua.lab.melonauth.utils.API;
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
        while (true){
            if(!player.isOnline()) break;
            profile = API.getPlayerProfile(player.getUniqueId());
            if(profile!=null) break;
            player.sendMessage(languageMap.get("prefix") + languageMap.get("connection_error"));
            try {
                Thread.sleep(20 * 1000/* try again in 20 seconds */);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(profile==null) return;

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
                while (true){
                    if(!player.isOnline()) return;
                    skinImage = API.getSKIN(skinFileURL);
                    if(skinImage!=null) return;
                    player.sendMessage(languageMap.get("prefix") + languageMap.get("connection_error"));
                    Thread.sleep(20 * 1000);
                }
            } else {
                skinImage = ImageIO.read(instance.getResource(((player.getUniqueId().hashCode() & 1) != 0 ? "alex" : "steve") + ".png"));
            }
        } catch (IOException | JSONException | InterruptedException e) {
            e.printStackTrace();
            this.runTaskAsynchronously(instance);
        }
    }
}
