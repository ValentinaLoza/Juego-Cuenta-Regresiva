package presentacion;

import java.awt.*;
import javax.swing.*;
import logica.GestorPartida;
import logica.Mochila;
import logica.Refugio;
import presentacion.componentes.Estilo;
import presentacion.componentes.PanelConFondo;
import presentacion.componentes.Recursos;

/** Pantalla de advertencias / cómo jugar, antes de empezar la partida. */
public class PanelAdvertencias extends PanelConFondo {

    public PanelAdvertencias(FrmPrincipal ventana) {
        super(Recursos.imagenHabitacion("REF"), 215);

        JLabel lblTitulo = new JLabel("⚠  ALERTA DE ATAQUE  ⚠", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 36));
        lblTitulo.setForeground(Estilo.PELIGRO);
        lblTitulo.setBounds(0, 26, Estilo.ANCHO, 50);
        add(lblTitulo);

        String texto =
              "<html><div style='width:600px;color:#ebebeb;font-family:SansSerif;font-size:13px'>"
            + "<p>Las sirenas suenan. Tenés <b style='color:#ffc846'>" + GestorPartida.TIEMPO_RECOLECCION
            + " segundos</b> antes de que caigan las bombas.</p><br>"
            + "<p style='color:#ffc846'><b>FASE 1 · LA CASA</b></p>"
            + "<ul>"
            + "<li>Empezás en el <b>recibidor</b>. Movete con las <b>puertas</b> (las flechas te dicen a dónde llevan). "
            + "Cada cruce te cuesta " + GestorPartida.COSTO_MOVIMIENTO + " segundo.</li>"
            + "<li>Hacé clic en los objetos para meterlos en la mochila. Tiene <b>" + Mochila.CAPACIDAD
            + " espacios</b> y no todo ocupa lo mismo: el agua ocupa 1, el botiquín 2, la escopeta 3.</li>"
            + "<li>Tu familia (Laura, Tomás y Mía) está en algún lugar de la casa. "
            + "<b>Para llevar a alguien necesitás la mochila vacía.</b></li>"
            + "<li>Bajá al <b>refugio</b> por la escotilla del recibidor para vaciar la mochila, y volvé a subir.</li>"
            + "<li style='color:#ff7a6e'><b>Si el tiempo se acaba y no estás en el refugio, no sobrevivís.</b></li>"
            + "</ul>"
            + "<p style='color:#ffc846'><b>FASE 2 · EL REFUGIO</b></p>"
            + "<ul>"
            + "<li>Cada noche decidís a quién le das agua y comida, y cuánto: media ración o entera. "
            + "No va a alcanzar para todos.</li>"
            + "<li>Cada día pasa algo: tus objetos desbloquean mejores decisiones.</li>"
            + "<li>Ganás si llegan al día " + Refugio.DIAS_PARA_RESCATE + " o si contactan al rescate "
            + Refugio.CONTACTOS_PARA_RESCATE + " veces por radio. Los que quedaron afuera te van a pesar en la conciencia.</li>"
            + "</ul></div></html>";
        JLabel lblTexto = new JLabel(texto);
        lblTexto.setVerticalAlignment(SwingConstants.TOP);
        lblTexto.setBounds(90, 90, 790, 430);
        add(lblTexto);

        JButton btnVolver = Estilo.boton("← Volver", new Color(70, 70, 80), Color.WHITE);
        btnVolver.setBounds(90, 548, 140, 50);
        btnVolver.addActionListener(e -> ventana.mostrarPortada());
        add(btnVolver);

        JButton btnEmpezar = Estilo.boton("¡EMPEZAR!", Estilo.PELIGRO, Color.WHITE);
        btnEmpezar.setFont(new Font("SansSerif", Font.BOLD, 22));
        btnEmpezar.setBounds(Estilo.ANCHO / 2 - 120, 540, 240, 60);
        btnEmpezar.addActionListener(e -> ventana.nuevaPartida());
        add(btnEmpezar);
    }
}
