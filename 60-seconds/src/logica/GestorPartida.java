package logica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Coordina una partida: la fase 1 (recorrer la casa con la mochila) y la
 * fase 2 (los días en el refugio). Aplica las reglas del juego y lanza
 * AccionInvalidaException cuando el jugador intenta algo que no se puede.
 *
 * No construye la casa (eso lo hace Casa) ni sabe de dónde salen los eventos
 * (los pide a un RepositorioEventos que recibe por constructor).
 */
public class GestorPartida {
    // --- Fase 1 ---
    public static final int TIEMPO_RECOLECCION = 60; // segundos
    public static final int COSTO_MOVIMIENTO = 1;    // segundos que cuesta cruzar una puerta

    // --- Fase 2 ---
    public static final int CURACION_BOTIQUIN = 30;
    public static final int SED_POR_NOCHE = 34;    // La hidratación baja esto cada noche (se vacía en 3)
    public static final int HAMBRE_POR_NOCHE = 17; // La alimentación baja esto cada noche (se vacía en 6)
    public static final int MEDIA_RACION = 50;     // Lo que suma media ración (una entera suma el doble)
    public static final int CULPA_POR_FAMILIAR = 4; // salud que se pierde cada noche por cada familiar abandonado
    private static final int PROBABILIDAD_DIA_TRANQUILO = 25; // %

    // Objetos que se reparten por la casa en cada partida
    private static final TipoItem[] OBJETOS_DE_LA_CASA = {
        TipoItem.AGUA, TipoItem.AGUA, TipoItem.AGUA, TipoItem.AGUA, TipoItem.AGUA, TipoItem.AGUA, TipoItem.AGUA,
        TipoItem.COMIDA, TipoItem.COMIDA, TipoItem.COMIDA, TipoItem.COMIDA, TipoItem.COMIDA, TipoItem.COMIDA, TipoItem.COMIDA,
        TipoItem.MEDICAMENTO, TipoItem.MEDICAMENTO,
        TipoItem.HERRAMIENTA, TipoItem.RADIO, TipoItem.ARMA, TipoItem.MAPA, TipoItem.VARIOS,
        TipoItem.INSECTICIDA, TipoItem.MASCARA, TipoItem.LIBRO, TipoItem.ESCOPETA
    };

    private final Random azar;
    private final Casa casa = new Casa();
    private Habitacion habitacionActual;
    private final Persona protagonista = new Persona("Martín", Rol.PROTAGONISTA);
    private final ArrayList<Persona> familia = new ArrayList<>(); // sin contar al protagonista
    private final Mochila mochila = new Mochila();
    private int tiempoRestante = TIEMPO_RECOLECCION;
    private Boolean llegoAlRefugio; // null mientras dura la fase 1

    private final ArrayList<Evento> mazoEventos;
    private final Inventario inventario = new Inventario(); // provisiones guardadas en el refugio
    private final Refugio refugio = new Refugio();
    private Evento eventoActual;
    private Persona objetivoEvento;

    public GestorPartida(RepositorioEventos repositorioEventos) {
        this(repositorioEventos, new Random());
    }

    /** Permite pasar un Random con semilla fija para probar siempre la misma partida. */
    public GestorPartida(RepositorioEventos repositorioEventos, Random azar) {
        this.azar = azar;
        familia.add(new Persona("Laura", Rol.MUJER));
        familia.add(new Persona("Tomás", Rol.NINO));
        familia.add(new Persona("Mía", Rol.NINA));
        casa.repartir(familia, OBJETOS_DE_LA_CASA, azar);
        mazoEventos = new ArrayList<>(repositorioEventos.obtenerTodos());
        Collections.shuffle(mazoEventos, azar);
        habitacionActual = casa.getRecibidor(); // el protagonista arranca en la entrada
    }

    // =====================================================================
    //  FASE 1: RECOLECCIÓN
    // =====================================================================

    /** Pasa un segundo del reloj. Devuelve true si se terminó el tiempo. */
    public boolean avanzarSegundo() {
        if (tiempoRestante > 0) tiempoRestante--;
        return tiempoRestante <= 0;
    }

    /** Cruza una salida de la habitación actual. Cuesta tiempo. */
    public void moverA(Salida salida) throws AccionInvalidaException {
        if (tiempoRestante <= 0) throw new AccionInvalidaException("¡Se terminó el tiempo!");
        if (!habitacionActual.getSalidas().contains(salida)) {
            throw new AccionInvalidaException("No hay una salida a " + salida.getDestino().getNombre() + " desde acá");
        }
        tiempoRestante = Math.max(0, tiempoRestante - COSTO_MOVIMIENTO);
        habitacionActual = salida.getDestino();
    }

    /** Pone en la mochila algo de la habitación actual. */
    public void recoger(Cargable c) throws AccionInvalidaException {
        if (tiempoRestante <= 0) throw new AccionInvalidaException("¡Se terminó el tiempo!");
        if (!habitacionActual.contiene(c)) {
            throw new AccionInvalidaException(c.getNombre() + " no está en esta habitación");
        }
        if (c instanceof Persona && !mochila.estaVacia()) {
            throw new AccionInvalidaException("Para llevar a " + c.getNombre() + " vaciá primero la mochila");
        }
        if (!mochila.puedeCargar(c)) {
            throw new AccionInvalidaException("No entra: ocupa " + c.getTamano() + " y te quedan " + mochila.getEspacioLibre());
        }
        habitacionActual.quitar(c);
        mochila.cargar(c);
    }

    /** Saca algo de la mochila. En el refugio queda guardado; en otra habitación, en el piso. */
    public void soltar(Cargable c) throws AccionInvalidaException {
        if (!mochila.quitar(c)) throw new AccionInvalidaException(c.getNombre() + " no está en la mochila");
        if (habitacionActual.esRefugio()) {
            guardarEnRefugio(c);
        } else if (c instanceof Item) {
            habitacionActual.dejarItem((Item) c, azar);
        } else {
            habitacionActual.dejarPersona((Persona) c, azar);
        }
    }

    /** Deja todo lo de la mochila en el refugio. Devuelve cuántas cosas guardó. */
    public int vaciarMochilaEnRefugio() throws AccionInvalidaException {
        if (!habitacionActual.esRefugio()) {
            throw new AccionInvalidaException("La mochila solo se puede vaciar en el refugio");
        }
        if (mochila.estaVacia()) throw new AccionInvalidaException("La mochila ya está vacía");
        List<Cargable> todo = mochila.vaciar();
        for (Cargable c : todo) guardarEnRefugio(c);
        return todo.size();
    }

    private void guardarEnRefugio(Cargable c) {
        if (c instanceof Item) {
            inventario.agregarItem((Item) c);
        } else {
            Persona p = (Persona) c;
            refugio.agregarPersona(p);
            casa.getRefugio().dejarPersona(p, azar);
        }
    }

    /**
     * Termina la fase 1. Si el protagonista está en el refugio, guarda lo que
     * traía y devuelve true. Si no llegó, devuelve false: la partida está perdida.
     */
    public boolean terminarRecoleccion() {
        tiempoRestante = 0;
        llegoAlRefugio = habitacionActual.esRefugio();
        if (!llegoAlRefugio) return false;
        if (!refugio.getPersonas().contains(protagonista)) { // por si se llama dos veces
            for (Cargable c : mochila.vaciar()) guardarEnRefugio(c);
            refugio.getPersonas().add(0, protagonista);
        }
        return true;
    }

    public boolean estaEnRefugio() { return habitacionActual.esRefugio(); }

    /** Segundos que lleva volver al refugio desde donde está el protagonista. */
    public int segundosHastaRefugio() {
        return casa.pasosHastaRefugio(habitacionActual) * COSTO_MOVIMIENTO;
    }

    /** Familiares que no llegaron al refugio. */
    public List<Persona> getFamiliaresPerdidos() {
        List<Persona> perdidos = new ArrayList<>();
        for (Persona p : familia) if (!refugio.getPersonas().contains(p)) perdidos.add(p);
        return perdidos;
    }

    /** Familiares que ya están a salvo en el refugio. */
    public List<Persona> getFamiliaresEnRefugio() {
        List<Persona> aSalvo = new ArrayList<>();
        for (Persona p : familia) if (refugio.getPersonas().contains(p)) aSalvo.add(p);
        return aSalvo;
    }

    // =====================================================================
    //  FASE 2: EL REFUGIO
    // =====================================================================

    /**
     * Pasa la noche: cada familiar recibe la ración que eligió el jugador,
     * bajan la hidratación y la alimentación, pesa la culpa por los que
     * quedaron afuera y se sortea el evento del día.
     *
     * @return un resumen de la noche para mostrar en pantalla
     */
    public String procesarNuevoDia() {
        refugio.avanzarDia();
        StringBuilder resumen = new StringBuilder();

        for (Persona p : refugio.getVivos()) {
            // 1. Lo que el jugador le dio (en mitades: 1 = media ración, 2 = entera)
            if (p.getRacionAgua() > 0 && inventario.consumirMitades(TipoItem.AGUA, p.getRacionAgua())) {
                p.beber(MEDIA_RACION * p.getRacionAgua());
            }
            if (p.getRacionComida() > 0 && inventario.consumirMitades(TipoItem.COMIDA, p.getRacionComida())) {
                p.comer(MEDIA_RACION * p.getRacionComida());
            }
            p.setRacionAgua(0);
            p.setRacionComida(0);

            // 2. Pasa la noche: bajan las barras; si alguna llega a 0, pierde salud
            int dano = p.pasarNoche(SED_POR_NOCHE, HAMBRE_POR_NOCHE);
            if (dano > 0) {
                resumen.append(p.getNombre());
                if (p.getNochesSinAgua() > 0 && p.getNochesSinComida() > 0) resumen.append(" no tiene agua ni comida");
                else if (p.getNochesSinAgua() > 0) resumen.append(" está deshidratado");
                else resumen.append(" no come hace días");
                resumen.append(" (-").append(dano).append(" salud).\n");
            }
            if (!p.estaViva()) {
                resumen.append("✝ ").append(p.getNombre()).append(" no resistió la noche.\n");
            }
        }

        // 3. La culpa: cada familiar que quedó afuera pesa en el ánimo de todos
        int abandonados = getFamiliaresPerdidos().size();
        if (abandonados > 0) {
            int culpa = CULPA_POR_FAMILIAR * abandonados;
            for (Persona p : refugio.getVivos()) {
                p.modificarSalud(-culpa);
                if (!p.estaViva()) resumen.append("✝ ").append(p.getNombre()).append(" no resistió la noche.\n");
            }
            resumen.append("Pensás en ").append(abandonados == 1 ? "el familiar que quedó" : "los familiares que quedaron")
                   .append(" afuera. La culpa pesa (-").append(culpa).append(" salud).\n");
        }
        if (resumen.length() == 0) resumen.append("La noche pasó sin problemas.\n");

        if (getEstado() != EstadoPartida.EN_CURSO) {
            eventoActual = null;
            return resumen.toString();
        }
        sortearEvento();
        return resumen.toString();
    }

    private void sortearEvento() {
        eventoActual = null;
        if (azar.nextInt(100) >= PROBABILIDAD_DIA_TRANQUILO) {
            for (int i = 0; i < mazoEventos.size(); i++) {
                Evento candidato = mazoEventos.get(i);
                if (!candidato.isRequiereNinos() || refugio.hayNinosVivos()) {
                    eventoActual = mazoEventos.remove(i);
                    break;
                }
            }
        }
        if (eventoActual == null) {
            eventoActual = new Evento("Un día tranquilo en el refugio. No pasa nada inusual.");
            eventoActual.agregarOpcion(new Opcion("Descansar", null, 10, 0)
                    .aTodos()
                    .conResultado("Duermen casi todo el día y recuperan fuerzas."));
            eventoActual.agregarOpcion(new Opcion("Hacer mantenimiento", null, -5, 10)
                    .conResultado("{nombre} revisa paredes y cañerías. El refugio queda más firme."));
        }

        // ¿A quién le pasa el evento?
        List<Persona> candidatos = new ArrayList<>();
        for (Persona p : refugio.getVivos()) {
            if (!eventoActual.isProtagonizaUnNino() || p.getRol().esNino()) candidatos.add(p);
        }
        objetivoEvento = candidatos.get(azar.nextInt(candidatos.size()));
    }

    /** Aplica la opción elegida para el evento del día y devuelve qué pasó. */
    public String ejecutarOpcion(Opcion seleccionada) throws AccionInvalidaException {
        if (eventoActual == null || !eventoActual.getOpciones().contains(seleccionada)) {
            throw new AccionInvalidaException("Esa decisión ya no está disponible");
        }
        List<Persona> vivosAntes = refugio.getVivos();
        String resultado = seleccionada.aplicarConsecuencias(refugio, inventario, azar, objetivoEvento);
        for (Persona p : vivosAntes) {
            if (!p.estaViva()) resultado += "\n✝ " + p.getNombre() + " no sobrevivió.";
        }
        eventoActual = null;
        return resultado;
    }

    /** Usa un botiquín con un familiar. */
    public void usarBotiquin(Persona p) throws AccionInvalidaException {
        if (!p.estaViva()) throw new AccionInvalidaException(p.getNombre() + " ya no puede ser curado");
        if (p.getSalud() >= 100) throw new AccionInvalidaException(p.getNombre() + " está perfectamente sano");
        if (!inventario.consumirRecurso(TipoItem.MEDICAMENTO)) {
            throw new AccionInvalidaException("No quedan botiquines");
        }
        p.modificarSalud(CURACION_BOTIQUIN);
    }

    /** Cuántas mitades de agua o comida eligió repartir el jugador esta noche. */
    public int mitadesElegidas(TipoItem tipo) {
        int n = 0;
        for (Persona p : refugio.getVivos()) {
            n += tipo == TipoItem.AGUA ? p.getRacionAgua() : p.getRacionComida();
        }
        return n;
    }

    /**
     * Cambia la ración de un familiar a la siguiente: nada → ½ → 1 → nada.
     * Se saltea lo que no alcanza con lo que queda en el refugio.
     */
    public void ciclarRacion(Persona p, TipoItem tipo) throws AccionInvalidaException {
        if (!tipo.esRacion()) throw new IllegalArgumentException("Solo se reparten agua y comida");
        if (!p.estaViva()) throw new AccionInvalidaException(p.getNombre() + " ya no necesita raciones");
        int actual = tipo == TipoItem.AGUA ? p.getRacionAgua() : p.getRacionComida();
        int libres = inventario.getMitades(tipo) - mitadesElegidas(tipo) + actual;
        if (libres == 0) throw new AccionInvalidaException("No queda " + tipo.getNombre().toLowerCase() + " para repartir");
        int siguiente = (actual + 1) % 3;
        if (siguiente > libres) siguiente = 0;
        if (tipo == TipoItem.AGUA) p.setRacionAgua(siguiente);
        else p.setRacionComida(siguiente);
    }

    /** Cómo terminó la partida, o null si todavía no terminó. */
    public ResultadoPartida getResultado() {
        if (llegoAlRefugio == null) return null;
        if (!llegoAlRefugio) return ResultadoPartida.NO_LLEGO;
        switch (getEstado()) {
            case VICTORIA:
                return refugio.getContactosRadio() >= Refugio.CONTACTOS_PARA_RESCATE
                        ? ResultadoPartida.RESCATADOS : ResultadoPartida.SOBREVIVIERON;
            case DERROTA:
                return ResultadoPartida.MURIERON;
            default:
                return null;
        }
    }

    public EstadoPartida getEstado() {
        if (refugio.esDerrota()) return EstadoPartida.DERROTA;
        if (refugio.esVictoria()) return EstadoPartida.VICTORIA;
        return EstadoPartida.EN_CURSO;
    }

    // --- Getters ---
    public List<Habitacion> getHabitaciones() { return casa.getHabitaciones(); }
    public Habitacion getHabitacionActual() { return habitacionActual; }
    public Habitacion getHabitacionRefugio() { return casa.getRefugio(); }
    public Persona getProtagonista() { return protagonista; }
    public List<Persona> getFamilia() { return familia; }
    public Mochila getMochila() { return mochila; }
    public int getTiempoRestante() { return tiempoRestante; }
    public Inventario getInventario() { return inventario; }
    public Refugio getRefugio() { return refugio; }
    public Evento getEventoActual() { return eventoActual; }
    public Persona getObjetivoEvento() { return objetivoEvento; }
}
