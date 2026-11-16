package persistencia;

import java.util.ArrayList;
import java.util.List;
import logica.Evento;
import logica.Opcion;
import logica.RepositorioEventos;
import logica.TipoItem;

/**
 * Catálogo de eventos guardado en memoria (ArrayList).
 *
 * Implementa RepositorioEventos: el juego no sabe de dónde salen los eventos.
 * En una versión futura podrían leerse de un archivo o de una base de datos
 * creando otra implementación, sin tocar GestorPartida.
 */
public class RepositorioEventosEnMemoria implements RepositorioEventos {

    @Override
    public List<Evento> obtenerTodos() {
        // Se crean de nuevo en cada partida para que cada una tenga su propio mazo
        ArrayList<Evento> eventos = new ArrayList<>();
        Evento e;

        e = new Evento("Alguien golpea desesperadamente la escotilla pidiendo asilo.");
        e.agregarOpcion(new Opcion("Abrir y compartir comida", TipoItem.COMIDA, 0, 0)
                .conResultado("El desconocido te agradece y, antes de irse, deja dos botellas de agua.")
                .conRecompensa(TipoItem.AGUA, 2));
        e.agregarOpcion(new Opcion("Quedarse en silencio", null, -10, 0)
                .aTodos()
                .conResultado("Los golpes se apagan. La culpa no los deja dormir."));
        eventos.add(e);

        e = new Evento("El sistema de ventilación falla y el aire se vuelve pesado.");
        e.agregarOpcion(new Opcion("Reparar el filtro", TipoItem.HERRAMIENTA, 0, 5)
                .sinConsumir()
                .conResultado("Con las herramientas arreglás el filtro. El aire vuelve a ser respirable."));
        e.agregarOpcion(new Opcion("Aguantar", null, -15, -10)
                .aTodos()
                .conResultado("Pasan el día mareados y con dolor de cabeza."));
        eventos.add(e);

        e = new Evento("Se escuchan ruidos en los estantes: unas ratas encontraron la comida.");
        e.agregarOpcion(new Opcion("Ahuyentarlas con el hacha", TipoItem.ARMA, 0, 0)
                .sinConsumir()
                .conResultado("Las ratas huyen. La comida está a salvo."));
        e.agregarOpcion(new Opcion("Que {nombre} las espante a mano", null, 0, 0)
                .conResultado("{nombre} logra espantarlas sin pérdidas.")
                .conRiesgo(50, "Una rata muerde a {nombre} y se llevan parte de la comida.", -15, 0)
                .conPerdidaSiFalla(TipoItem.COMIDA));
        e.agregarOpcion(new Opcion("Dejarlas", null, 0, 0)
                .conResultado("A la mañana falta una lata.")
                .conPerdida(TipoItem.COMIDA));
        eventos.add(e);

        e = new Evento("Las provisiones escasean. {nombre} se ofrece a salir a buscar suministros.");
        e.agregarOpcion(new Opcion("Que salga con el mapa", TipoItem.MAPA, 0, 0)
                .sinConsumir()
                .conResultado("Gracias al mapa, {nombre} encuentra un almacén saqueado a medias.")
                .conRecompensa(TipoItem.COMIDA, 2)
                .conRiesgo(75, "Una nube de polvo radiactivo obliga a {nombre} a volver con las manos vacías.", -25, 0));
        e.agregarOpcion(new Opcion("Que salga con la máscara de gas", TipoItem.MASCARA, 0, 0)
                .sinConsumir()
                .conResultado("Con la máscara, {nombre} cruza la zona contaminada y vuelve con provisiones.")
                .conRecompensa(TipoItem.AGUA, 2)
                .conRiesgo(80, "{nombre} se cruza con perros salvajes y vuelve corriendo, lastimado.", -15, 0));
        e.agregarOpcion(new Opcion("Que salga a ciegas", null, 0, 0)
                .conResultado("Tras horas de búsqueda, {nombre} vuelve con agua.")
                .conRecompensa(TipoItem.AGUA, 2)
                .conRiesgo(45, "{nombre} se pierde entre los escombros y vuelve herido.", -35, 0));
        e.agregarOpcion(new Opcion("Nadie sale", null, 0, 0)
                .conResultado("Prefieren no arriesgarse."));
        eventos.add(e);

        e = new Evento("Unos saqueadores rodean el refugio y exigen provisiones.");
        e.agregarOpcion(new Opcion("Disparar al aire con la escopeta", TipoItem.ESCOPETA, 0, 0)
                .sinConsumir()
                .conResultado("El estruendo los espanta. No vuelven a acercarse."));
        e.agregarOpcion(new Opcion("Defenderse con el hacha", TipoItem.ARMA, -15, -10)
                .sinConsumir()
                .conResultado("Después de una pelea tensa, los saqueadores se van. {nombre} quedó golpeado."));
        e.agregarOpcion(new Opcion("Entregarles comida", TipoItem.COMIDA, 0, 0)
                .conResultado("Se llevan la lata y se van sin causar daños."));
        e.agregarOpcion(new Opcion("Esconderse y hacer silencio", null, 0, 0)
                .conResultado("Creen que el refugio está vacío y siguen de largo.")
                .conRiesgo(50, "Fuerzan la entrada, rompen todo y se llevan agua.", 0, -25)
                .conPerdidaSiFalla(TipoItem.AGUA));
        eventos.add(e);

        e = new Evento("{nombre} se despierta con fiebre alta y escalofríos.");
        e.agregarOpcion(new Opcion("Darle el botiquín", TipoItem.MEDICAMENTO, 20, 0)
                .conResultado("Los medicamentos hacen efecto. {nombre} se siente mejor."));
        e.agregarOpcion(new Opcion("Que descanse", null, -25, 0)
                .conResultado("La fiebre deja a {nombre} agotado."));
        eventos.add(e);

        e = new Evento("Una pared tiene una filtración: gotea agua turbia dentro del refugio.");
        e.agregarOpcion(new Opcion("Sellarla con herramientas", TipoItem.HERRAMIENTA, 0, 10)
                .sinConsumir()
                .conResultado("Sellás la grieta. El refugio queda más firme."));
        e.agregarOpcion(new Opcion("Purificarla como dice el libro", TipoItem.LIBRO, 0, -5)
                .sinConsumir()
                .conResultado("Siguiendo el libro arman un filtro con arena y carbón. Agua limpia.")
                .conRecompensa(TipoItem.AGUA, 3));
        e.agregarOpcion(new Opcion("Juntar el agua en un balde", null, 0, -15)
                .conResultado("Filtran el agua con una tela, pero la pared se debilita.")
                .conRecompensa(TipoItem.AGUA, 2));
        eventos.add(e);

        e = new Evento("Una tormenta radiactiva se acerca. La escotilla tiembla con el viento.");
        e.agregarOpcion(new Opcion("Reforzar la escotilla", TipoItem.HERRAMIENTA, 0, 0)
                .sinConsumir()
                .conResultado("La escotilla resiste toda la noche."));
        e.agregarOpcion(new Opcion("Rezar para que aguante", null, -10, -20)
                .aTodos()
                .conResultado("La escotilla cede un poco y entra polvo al refugio."));
        eventos.add(e);

        e = new Evento("Entre los escombros de la entrada aparece una caja metálica cerrada.");
        e.agregarOpcion(new Opcion("Abrirla con herramientas", TipoItem.HERRAMIENTA, 0, 0)
                .sinConsumir()
                .conResultado("Adentro hay un botiquín de emergencia.")
                .conRecompensa(TipoItem.MEDICAMENTO, 1));
        e.agregarOpcion(new Opcion("Que {nombre} la fuerce a golpes", null, 0, 0)
                .conResultado("La tapa cede: hay dos latas de comida.")
                .conRecompensa(TipoItem.COMIDA, 2)
                .conRiesgo(50, "{nombre} se corta la mano con el metal.", -15, 0));
        e.agregarOpcion(new Opcion("Dejarla", null, 0, 0)
                .conResultado("Nunca sabrán qué había adentro."));
        eventos.add(e);

        e = new Evento("Un viajero con un carrito golpea la escotilla. Ofrece intercambiar mercadería.");
        e.agregarOpcion(new Opcion("Cambiar el hacha por 3 aguas", TipoItem.ARMA, 0, 0)
                .conResultado("Trato hecho. El viajero se aleja silbando.")
                .conRecompensa(TipoItem.AGUA, 3));
        e.agregarOpcion(new Opcion("Cambiar el mapa por 3 latas", TipoItem.MAPA, 0, 0)
                .conResultado("Trato hecho. Ya no podrán orientarse afuera.")
                .conRecompensa(TipoItem.COMIDA, 3));
        e.agregarOpcion(new Opcion("Rechazar la oferta", null, 0, 0)
                .conResultado("El viajero se encoge de hombros y sigue su camino."));
        eventos.add(e);

        e = new Evento("Un temblor sacude el refugio. Caen pedazos del techo.");
        e.agregarOpcion(new Opcion("Apuntalar el techo", TipoItem.HERRAMIENTA, 0, -5)
                .sinConsumir()
                .conResultado("Apuntalás las vigas a tiempo. Daño mínimo."));
        e.agregarOpcion(new Opcion("Cubrirse bajo los colchones", null, -10, -25)
                .conResultado("Un pedazo de techo golpea a {nombre}."));
        eventos.add(e);

        // --- Eventos de la familia ---
        e = new Evento("{nombre} tiene miedo y no puede dormir. Pregunta si van a salir de acá.").soloConNinos();
        e.agregarOpcion(new Opcion("Jugar a las cartas en familia", TipoItem.VARIOS, 10, 0)
                .sinConsumir().aTodos()
                .conResultado("Por un rato se olvidan de todo. Hasta se ríen."));
        e.agregarOpcion(new Opcion("Leerle el libro como si fuera un cuento", TipoItem.LIBRO, 10, 0)
                .sinConsumir()
                .conResultado("{nombre} se duerme escuchando cómo hacer un fuego sin fósforos."));
        e.agregarOpcion(new Opcion("Darle tu ración de comida", TipoItem.COMIDA, 15, 0)
                .conResultado("{nombre} come y se queda dormido abrazado a vos."));
        e.agregarOpcion(new Opcion("Pedirle que sea fuerte", null, -10, 0)
                .conResultado("{nombre} llora en silencio toda la noche."));
        eventos.add(e);

        e = new Evento("Mientras todos duermen, {nombre} se toma a escondidas una botella de agua.").soloConNinos();
        e.agregarOpcion(new Opcion("Retarlo fuerte", null, -5, 0)
                .conResultado("{nombre} pide perdón. Al menos no tendrá sed hoy.")
                .conPerdida(TipoItem.AGUA));
        e.agregarOpcion(new Opcion("Dejarlo pasar", null, 5, 0)
                .conResultado("Es un chico. Lo abrazás y no decís nada.")
                .conPerdida(TipoItem.AGUA));
        eventos.add(e);

        e = new Evento("La familia discute por las raciones. Los ánimos están muy tensos.");
        e.agregarOpcion(new Opcion("Repartir una lata entre todos", TipoItem.COMIDA, 10, 0)
                .aTodos()
                .conResultado("Comparten la lata en silencio. La tensión baja."));
        e.agregarOpcion(new Opcion("Imponer orden", null, -5, 0)
                .aTodos()
                .conResultado("Nadie vuelve a hablar del tema... por ahora."));
        eventos.add(e);

        // --- Eventos de los objetos nuevos ---
        e = new Evento("Una plaga de cucarachas invade los estantes de la comida.");
        e.agregarOpcion(new Opcion("Fumigar con el insecticida", TipoItem.INSECTICIDA, 0, 0)
                .sinConsumir()
                .conResultado("Las cucarachas caen patas arriba. La comida está a salvo."));
        e.agregarOpcion(new Opcion("Que {nombre} las aplaste una por una", null, 0, 0)
                .conResultado("Después de horas, {nombre} termina con todas.")
                .conRiesgo(50, "Son demasiadas: arruinan una lata y {nombre} queda lleno de picaduras.", -10, 0)
                .conPerdidaSiFalla(TipoItem.COMIDA));
        e.agregarOpcion(new Opcion("Ignorarlas", null, 0, 0)
                .conResultado("A la mañana una lata está agujereada e incomible.")
                .conPerdida(TipoItem.COMIDA));
        eventos.add(e);

        e = new Evento("Mosquitos enormes entran por la ventilación. A {nombre} lo pican toda la noche.");
        e.agregarOpcion(new Opcion("Rociar el insecticida", TipoItem.INSECTICIDA, 0, 0)
                .sinConsumir()
                .conResultado("Un par de rociadas y se termina el zumbido."));
        e.agregarOpcion(new Opcion("Taparse con las frazadas", null, -15, 0)
                .conResultado("{nombre} amanece con fiebre por las picaduras."));
        eventos.add(e);

        e = new Evento("Una nube tóxica se filtra por la ventilación. Empiezan a toser.");
        e.agregarOpcion(new Opcion("Que {nombre} se ponga la máscara y selle la ventilación", TipoItem.MASCARA, 0, 0)
                .sinConsumir()
                .conResultado("Con la máscara puesta, {nombre} sella el ducto. El aire se limpia de a poco."));
        e.agregarOpcion(new Opcion("Tapar con trapos mojados", null, -15, -5)
                .aTodos()
                .conResultado("Los trapos ayudan, pero todos respiran algo de gas."));
        eventos.add(e);

        e = new Evento("Se escuchan animales cerca del refugio. {nombre} propone salir a cazar.");
        e.agregarOpcion(new Opcion("Salir con la escopeta", TipoItem.ESCOPETA, 0, 0)
                .sinConsumir()
                .conResultado("{nombre} vuelve con carne. Hoy hay festín.")
                .conRecompensa(TipoItem.COMIDA, 3)
                .conRiesgo(65, "{nombre} falla el tiro y un animal lo ataca al volver.", -20, 0));
        e.agregarOpcion(new Opcion("Salir con el hacha", TipoItem.ARMA, 0, 0)
                .sinConsumir()
                .conResultado("Con mucha paciencia, {nombre} atrapa algo.")
                .conRecompensa(TipoItem.COMIDA, 2)
                .conRiesgo(35, "El animal se defiende. {nombre} vuelve herido y sin nada.", -25, 0));
        e.agregarOpcion(new Opcion("Nadie sale", null, 0, 0)
                .conResultado("Escuchan los ruidos alejarse."));
        eventos.add(e);

        e = new Evento("{nombre} tiene una herida en la pierna que empezó a infectarse.");
        e.agregarOpcion(new Opcion("Usar el botiquín", TipoItem.MEDICAMENTO, 25, 0)
                .conResultado("La herida se limpia y empieza a cerrar."));
        e.agregarOpcion(new Opcion("Hacer un remedio casero del libro", TipoItem.LIBRO, 10, 0)
                .sinConsumir()
                .conResultado("Una cataplasma de hierbas, como explica el libro. Funciona."));
        e.agregarOpcion(new Opcion("Esperar que se cure sola", null, -20, 0)
                .conResultado("La infección avanza. {nombre} tiene fiebre."));
        eventos.add(e);

        e = new Evento("El agua se está terminando y nadie sabe bien cómo conseguir más.");
        e.agregarOpcion(new Opcion("Armar un colector de lluvia con el libro", TipoItem.LIBRO, 0, 0)
                .sinConsumir()
                .conResultado("Siguiendo los dibujos del libro, arman un colector. Junta agua durante la noche.")
                .conRecompensa(TipoItem.AGUA, 2));
        e.agregarOpcion(new Opcion("Racionar todavía más", null, -5, 0)
                .aTodos()
                .conResultado("Todos se acuestan con la boca seca."));
        eventos.add(e);

        // --- Radio: contactar 3 veces = rescate anticipado ---
        e = new Evento("La radio emite estática... entre el ruido, una voz: \"¿Hay sobrevivientes en la zona?\"");
        e.agregarOpcion(new Opcion("Responder por radio", TipoItem.RADIO, 0, 0)
                .sinConsumir().conContactoRadio()
                .conResultado("Una voz confirma tu posición. Hay esperanza."));
        e.agregarOpcion(new Opcion("Apagarla para ahorrar batería", null, 0, 0)
                .conResultado("El silencio vuelve al refugio."));
        eventos.add(e);

        e = new Evento("La radio capta una frecuencia militar: están organizando evacuaciones.");
        e.agregarOpcion(new Opcion("Transmitir tu ubicación", TipoItem.RADIO, 0, 0)
                .sinConsumir().conContactoRadio()
                .conResultado("\"Recibido, refugio. Manténganse con vida.\""));
        e.agregarOpcion(new Opcion("Escuchar sin responder", null, 0, 0)
                .conResultado("Te quedás con la duda de si te habrían escuchado."));
        eventos.add(e);

        e = new Evento("La radio suena fuerte: \"Último convoy de rescate. Respondan si nos escuchan.\"");
        e.agregarOpcion(new Opcion("¡Responder ahora!", TipoItem.RADIO, 0, 0)
                .sinConsumir().conContactoRadio()
                .conResultado("\"Los tenemos ubicados. Vamos en camino.\""));
        e.agregarOpcion(new Opcion("No responder", null, 0, 0)
                .conResultado("El convoy sigue de largo."));
        eventos.add(e);

        return eventos;
    }
}
