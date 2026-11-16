package presentacion;

import java.awt.*;
import javax.swing.*;
import logica.GestorPartida;
import logica.Persona;
import logica.RegistroPartida;
import logica.ServicioHistorial;
import presentacion.componentes.Estilo;
import presentacion.componentes.PanelConFondo;
import presentacion.componentes.Recursos;

/** Pantalla de fin de partida (victoria o derrota). */
public class PanelFinal extends PanelConFondo {

    public PanelFinal(FrmPrincipal ventana, ServicioHistorial historial, GestorPartida partida,
                      boolean victoria, String titulo, String texto, java.util.List<Persona> familia) {
        super(Recursos.portada(), victoria ? 120 : 200);

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 48));
        lblTitulo.setForeground(victoria ? new Color(255, 225, 150) : Estilo.PELIGRO);
        lblTitulo.setBounds(0, 50, Estilo.ANCHO, 64);
        add(lblTitulo);

        JLabel lblTexto = new JLabel("<html><div style='width:600px;text-align:center;font-size:14px;color:#f0f0f0'>"
                + texto.replace("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
        lblTexto.setBounds(130, 120, 700, 170);
        add(lblTexto);

        // Retratos de la familia: en color los que viven, en gris los que no
        if (familia != null && !familia.isEmpty()) {
            int tam = 96, gap = 30;
            int total = familia.size() * tam + (familia.size() - 1) * gap;
            int x = (Estilo.ANCHO - total) / 2;
            for (Persona p : familia) {
                boolean bien = p.estaViva() && victoria;
                Image img = bien ? Recursos.retrato(p) : Recursos.grises(Recursos.retrato(p));
                JLabel lbl = new JLabel(p.getNombre(), new ImageIcon(Recursos.escalar(img, tam, tam)), SwingConstants.CENTER);
                lbl.setHorizontalTextPosition(SwingConstants.CENTER);
                lbl.setVerticalTextPosition(SwingConstants.BOTTOM);
                lbl.setForeground(bien ? Color.WHITE : Estilo.TEXTO_SUAVE);
                lbl.setFont(Estilo.NEGRITA);
                lbl.setBounds(x, 300, tam, tam + 24);
                add(lbl);
                x += tam + gap;
            }
        }

        // --- Guardar en el historial (alta con validación del nombre) ---
        JLabel lblNombre = new JLabel("Tu nombre:");
        lblNombre.setFont(Estilo.NEGRITA);
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setBounds(230, 462, 90, 34);
        add(lblNombre);
        JTextField txtNombre = new JTextField();
        txtNombre.setFont(new Font("SansSerif", Font.PLAIN, 15));
        txtNombre.setBounds(320, 462, 230, 34);
        add(txtNombre);
        JButton btnGuardar = Estilo.boton("Guardar partida", Estilo.EXITO.darker(), Color.WHITE);
        btnGuardar.setBounds(560, 460, 170, 38);
        add(btnGuardar);
        JLabel lblAviso = new JLabel("", SwingConstants.CENTER);
        lblAviso.setFont(Estilo.NEGRITA);
        lblAviso.setBounds(130, 500, 700, 20);
        add(lblAviso);

        Runnable guardar = () -> {
            try {
                RegistroPartida r = historial.registrar(txtNombre.getText(), partida);
                lblAviso.setForeground(Estilo.EXITO);
                lblAviso.setText("¡Guardada! Puntaje: " + r.getPuntaje() + ". Mirala en Juego → Historial de partidas.");
                txtNombre.setEnabled(false);
                btnGuardar.setEnabled(false);
            } catch (IllegalArgumentException ex) {
                lblAviso.setForeground(Estilo.PELIGRO);
                lblAviso.setText(ex.getMessage());
                txtNombre.requestFocusInWindow();
            }
        };
        btnGuardar.addActionListener(e -> guardar.run());
        txtNombre.addActionListener(e -> guardar.run());

        JButton btnOtra = Estilo.boton("Jugar de nuevo", Estilo.ACENTO, Color.BLACK);
        btnOtra.setBounds(Estilo.ANCHO / 2 - 230, 540, 220, 56);
        btnOtra.addActionListener(e -> ventana.mostrarAdvertencias());
        add(btnOtra);

        JButton btnPortada = Estilo.boton("Volver a la portada", new Color(70, 70, 80), Color.WHITE);
        btnPortada.setBounds(Estilo.ANCHO / 2 + 10, 540, 220, 56);
        btnPortada.addActionListener(e -> ventana.mostrarPortada());
        add(btnPortada);
    }
}
