package moe.langua.lab.auth.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class Verifier {
    private long seed;
    private static int[] colorList = {0xff3a539b, 0xff446cb3, 0xff4b77be, 0xff4183d7, 0xff3498db, 0xff59abe3, 0xff6bb9f0, 0xff89c4f4};
    private byte[] paintedSkinImage;
    private BufferedImage imagePaintedSkin;

    public Verifier(BufferedImage skin) throws IOException {
        seed = System.nanoTime();
        Random random = new Random(seed);
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
        paintedSkinImage = byteArrayOutputStream.toByteArray();
        imagePaintedSkin = skin;
        byteArrayOutputStream.close();
    }

    public byte[] getPaintedSkin() {
        return paintedSkinImage;
    }

    public boolean verify(BufferedImage image) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (image.getRGB(x, y) != imagePaintedSkin.getRGB(x, y)) return false;
            }
        }
        return true;
    }

    public long getSeed() {
        return seed;
    }
}
