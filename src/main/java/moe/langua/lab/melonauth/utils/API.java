package moe.langua.lab.melonauth.utils;

import org.json.JSONException;
import org.json.JSONObject;
import sun.misc.FileURLMapper;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

public class API {

    public static BufferedImage getSKIN(URL reqURL) {
        try {
            return ImageIO.read(reqURL);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getGithubLatestRelease() {
        try {
            URL reqURL = new URL("https://api.github.com/repos/LanguaLab/MelonAuth/releases/latest");
            return apiGet(reqURL);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getPlayerProfile(UUID uniqueID) {
        try {
            URL reqURL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uniqueID.toString().replace("-", ""));
            return apiGet(reqURL);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JSONObject apiGet(URL reqURL) throws JSONException, IOException {
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
}
