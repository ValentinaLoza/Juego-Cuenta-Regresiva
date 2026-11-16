package logica;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Una decisión que el jugador puede tomar ante un evento.
 *
 * Guarda dos listas de Efecto: los que ocurren si sale bien y los que ocurren
 * si sale mal. No sabe qué hace cada efecto: solo les pide que se apliquen
 * (polimorfismo). Se configura con métodos encadenados, por ejemplo:
 *
 *   new Opcion("Mandar a {nombre} con el mapa", TipoItem.MAPA, 0, 0)
 *       .sinConsumir()
 *       .conRecompensa(TipoItem.AGUA, 2)
 *       .conRiesgo(60, "{nombre} se perdió y volvió herido.", -20, 0)
 *       .conResultado("{nombre} encontró agua en una casa abandonada.");
 */
public class Opcion {
    private final String descripcion;
    private final TipoItem requerimiento; // null = siempre disponible
    private boolean consumeRequerimiento = true;
    private boolean aTodos;
    private int efectoSalud;
    private String textoResultado = "Decisión tomada.";
    private final List<Efecto> efectosExito = new ArrayList<>();

    // Azar: si probabilidadExito < 100, puede salir mal
    private int probabilidadExito = 100;
    private String textoFracaso;
    private int saludFracaso;
    private final List<Efecto> efectosFracaso = new ArrayList<>();

    public Opcion(String descripcion, TipoItem requerimiento, int efectoSalud, int efectoIntegridad) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La opción necesita una descripción");
        }
        this.descripcion = descripcion;
        this.requerimiento = requerimiento;
        this.efectoSalud = efectoSalud;
        if (efectoIntegridad != 0) efectosExito.add(new EfectoIntegridad(efectoIntegridad));
    }

    // ---------- Métodos encadenados ----------

    public Opcion conResultado(String texto) { this.textoResultado = texto; return this; }

    /** El ítem requerido se usa pero no se gasta (ej: la radio, el hacha). */
    public Opcion sinConsumir() { this.consumeRequerimiento = false; return this; }

    /** El efecto de salud le llega a toda la familia, no solo a {nombre}. */
    public Opcion aTodos() { this.aTodos = true; return this; }

    public Opcion conRecompensa(TipoItem tipo, int cantidad) {
        efectosExito.add(new EfectoRecompensa(tipo, cantidad));
        return this;
    }

    public Opcion conPerdida(TipoItem tipo) {
        efectosExito.add(new EfectoPerdida(tipo));
        return this;
    }

    public Opcion conContactoRadio() {
        efectosExito.add(new EfectoContactoRadio());
        return this;
    }

    /** Agrega cualquier efecto (por ejemplo, uno nuevo que se cree en el futuro). */
    public Opcion conEfecto(Efecto efecto) {
        efectosExito.add(efecto);
        return this;
    }

    public Opcion conRiesgo(int probabilidadExito, String textoFracaso, int saludFracaso, int integridadFracaso) {
        if (probabilidadExito < 0 || probabilidadExito > 100) {
            throw new IllegalArgumentException("La probabilidad debe estar entre 0 y 100");
        }
        this.probabilidadExito = probabilidadExito;
        this.textoFracaso = textoFracaso;
        this.saludFracaso = saludFracaso;
        if (integridadFracaso != 0) efectosFracaso.add(new EfectoIntegridad(integridadFracaso));
        return this;
    }

    public Opcion conPerdidaSiFalla(TipoItem tipo) {
        efectosFracaso.add(new EfectoPerdida(tipo));
        return this;
    }

    // ---------- Lógica ----------

    public boolean esEjecutable(Inventario inventario) {
        return requerimiento == null || inventario.tieneRecurso(requerimiento);
    }

    /** Aplica la decisión y devuelve el texto que cuenta lo que pasó. */
    public String aplicarConsecuencias(Refugio refugio, Inventario inventario, Random azar, Persona objetivo)
            throws AccionInvalidaException {
        if (!esEjecutable(inventario)) {
            throw new AccionInvalidaException("Para esta opción necesitás: " + requerimiento.getNombre());
        }
        if (requerimiento != null && consumeRequerimiento) {
            inventario.consumirRecurso(requerimiento);
        }

        boolean exito = azar.nextInt(100) < probabilidadExito;
        String texto = exito ? textoResultado : textoFracaso;
        List<Efecto> efectos = new ArrayList<>(exito ? efectosExito : efectosFracaso);
        efectos.add(new EfectoSalud(exito ? efectoSalud : saludFracaso, aTodos));

        StringBuilder sb = new StringBuilder(texto.replace("{nombre}", objetivo.getNombre()));
        for (Efecto efecto : efectos) {
            String linea = efecto.aplicar(refugio, inventario, objetivo, azar); // polimorfismo
            if (linea != null) sb.append("\n").append(linea);
        }
        return sb.toString();
    }

    public String getDescripcion(Persona objetivo) {
        return descripcion.replace("{nombre}", objetivo.getNombre());
    }

    public TipoItem getRequerimiento() { return requerimiento; }
    public int getProbabilidadExito() { return probabilidadExito; }
}
