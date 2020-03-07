package moe.langua.lab.auth.utils;

import moe.langua.lab.auth.core.VerificationManager;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

public class SkinGetter {
    public static BufferedImage getSkin(MessageSender sender, VerificationManager verificationManager) throws JSONException, IOException, InterruptedException {
        JSONObject profile;

        while (true) {
            if(sender.isOnline()) return null;
            profile = HTTPSAPI.getPlayerProfile(sender.getUUID());
            if (profile != null) break;
            sender.sendMessage(verificationManager.getLanguage("prefix") + verificationManager.getLanguage("connection_error"));
                Thread.sleep(20000/* try again in 20 seconds */);
        }

        JSONObject value = new JSONObject(new String(Base64.getDecoder().decode(profile.getJSONArray("properties").getJSONObject(0).getString("value"))));
        JSONObject textures= value.getJSONObject("textures");

        BufferedImage skinImage;

        if (textures.has("SKIN")) {
            URL skinFileURL = new URL(textures.getJSONObject("SKIN").getString("url").replace("http://", "https://"));
            while (true) {
                if (sender.isOnline()) return null;
                skinImage = HTTPSAPI.getImage(skinFileURL);
                if (skinImage != null) break;
                sender.sendMessage(verificationManager.getLanguage("prefix") + verificationManager.getLanguage("connection_error"));
                Thread.sleep(20 * 1000);
            }
        } else {
            skinImage = ImageIO.read(verificationManager.getResourceFileURL(((sender.getUUID().hashCode() & 1) != 0 ? "alex" : "steve") + ".png"));
        }

        return skinImage;
    }
}
