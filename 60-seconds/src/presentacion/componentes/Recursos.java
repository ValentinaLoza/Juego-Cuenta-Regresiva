package presentacion.componentes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import logica.Persona;
import logica.TipoItem;

/**
 * Carga y guarda en memoria las imágenes de src/recursos.
 * Para cambiar el arte del juego alcanza con reemplazar los PNG
 * manteniendo el mismo nombre de archivo.
 */
public final class Recursos {

    private static final Map<String, BufferedImage> cache = new HashMap<>();

    private Recursos() { }

    public static BufferedImage imagen(String ruta) {
        if (cache.containsKey(ruta)) return cache.get(ruta);
        BufferedImage img = null;
        try (InputStream in = Recursos.class.getResourceAsStream(ruta)) {
            if (in != null) img = ImageIO.read(in);
        } catch (Exception e) {
            System.err.println("No se pudo cargar " + ruta + ": " + e.getMessage());
        }
        cache.put(ruta, img);
        return img;
    }

    /** /recursos/items/agua.png, comida.png, ... */
    public static BufferedImage imagenItem(TipoItem tipo) {
        BufferedImage img = imagen("/recursos/items/" + tipo.name().toLowerCase() + ".png");
        return img != null ? img : marcador(tipo.getNombre(), 128, 128);
    }

    /** /recursos/habitaciones/rec.png, coc.png, ... */
    public static BufferedImage imagenHabitacion(String codigo) {
        BufferedImage img = imagen("/recursos/habitaciones/" + codigo.toLowerCase() + ".png");
        return img != null ? img : marcador(codigo, 560, 300);
    }

    /** Cuerpo entero: /recursos/personas/mujer.png, nino.png, ... */
    public static BufferedImage imagenPersona(Persona p) {
        BufferedImage img = imagen("/recursos/personas/" + p.getRol().name().toLowerCase() + ".png");
        return img != null ? img : marcador(p.getNombre(), 100, 200);
    }

    /** Cara redonda: /recursos/personas/retrato_mujer.png, ... */
    public static BufferedImage retrato(Persona p) {
        BufferedImage img = imagen("/recursos/personas/retrato_" + p.getRol().name().toLowerCase() + ".png");
        return img != null ? img : marcador(p.getNombre().substring(0, 1), 128, 128);
    }

    public static BufferedImage portada() {
        BufferedImage img = imagen("/recursos/portada.png");
        return img != null ? img : marcador("", 960, 640);
    }

    public static ImageIcon iconoItem(TipoItem tipo, int tamano) {
        return new ImageIcon(escalar(imagenItem(tipo), tamano, tamano));
    }

    public static ImageIcon iconoRetrato(Persona p, int tamano) {
        return new ImageIcon(escalar(retrato(p), tamano, tamano));
    }

    public static BufferedImage escalar(Image img, int ancho, int alto) {
        BufferedImage res = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = res.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(img, 0, 0, ancho, alto, null);
        g.dispose();
        return res;
    }

    /** Versión en blanco y negro (para familiares fallecidos). */
    public static BufferedImage grises(BufferedImage img) {
        BufferedImage res = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int argb = img.getRGB(x, y);
                int a = (argb >>> 24), r = (argb >> 16) & 255, gr = (argb >> 8) & 255, b = argb & 255;
                int l = (r * 30 + gr * 59 + b * 11) / 100;
                res.setRGB(x, y, (a << 24) | (l << 16) | (l << 8) | l);
            }
        }
        return res;
    }

    /** Imagen de reemplazo si falta un PNG, para que el juego nunca se rompa. */
    private static BufferedImage marcador(String texto, int ancho, int alto) {
        BufferedImage img = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(90, 90, 100));
        g.fillRoundRect(0, 0, ancho, alto, 20, 20);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, Math.max(12, Math.min(alto, ancho) / 6)));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(texto, (ancho - fm.stringWidth(texto)) / 2, alto / 2 + fm.getAscent() / 3);
        g.dispose();
        return img;
    }
}
