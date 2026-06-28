package tvmanager.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ImageLoader {

    private static final ExecutorService POOL = Executors.newFixedThreadPool(4);
    private static final ConcurrentHashMap<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

    public static void carregar(String imageUrl, int largura, int altura, Consumer<ImageIcon> callback) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            callback.accept(placeholderIcon(largura, altura));
            return;
        }

        String chave = imageUrl + "_" + largura + "x" + altura;
        if (CACHE.containsKey(chave)) {
            callback.accept(CACHE.get(chave));
            return;
        }

        POOL.submit(() -> {
            try {
                BufferedImage img = ImageIO.read(new URL(imageUrl));
                if (img != null) {
                    Image scaled = img.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaled);
                    CACHE.put(chave, icon);
                    SwingUtilities.invokeLater(() -> callback.accept(icon));
                } else {
                    SwingUtilities.invokeLater(() -> callback.accept(placeholderIcon(largura, altura)));
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> callback.accept(placeholderIcon(largura, altura)));
            }
        });
    }

    private static ImageIcon placeholderIcon(int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(30, 30, 45));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(60, 60, 80));
        g.drawRect(2, 2, w - 4, h - 4);
        g.setColor(new Color(100, 100, 120));
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        String txt = "Sem imagem";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(txt, (w - fm.stringWidth(txt)) / 2, h / 2 + fm.getAscent() / 2);
        g.dispose();
        return new ImageIcon(img);
    }

    public static void desligar() {
        POOL.shutdown();
    }
}
