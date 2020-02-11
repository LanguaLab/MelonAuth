package moe.langua.lab.melonauth.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

public class Verifier {
    private static int[] colorList = {0xff3a539b, 0xff446cb3, 0xff4b77be, 0xff4183d7, 0xff3498db, 0xff59abe3, 0xff6bb9f0, 0xff89c4f4};
    private byte[] paintedSkin;
    private BufferedImage imagePaintedSkin;
    private long timestamp;

    public Verifier(BufferedImage skin) throws IOException {
        timestamp = System.currentTimeMillis();
        Random random = new Random();
        byte colorIndex;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                colorIndex = (byte) random.nextInt(8);
                skin.setRGB(x, y, colorList[colorIndex]);
            }
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(skin, "png", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        paintedSkin = byteArrayOutputStream.toByteArray();
        imagePaintedSkin = skin;
        byteArrayOutputStream.close();
    }

    public byte[] getPaintedSkin() {
        return paintedSkin;
    }

    public boolean verify(BufferedImage image) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (image.getRGB(x, y) != imagePaintedSkin.getRGB(x, y)) return false;
            }
        }
        return true;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
