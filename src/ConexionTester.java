import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionTester extends JFrame {

    private static final Color VERDE = new Color(0, 128, 0);
    private static final Color ROJO = new Color(200, 0, 0);

    private final JTextField txtUrl = new JTextField("jdbc:sqlite:inventario.db", 25);
    private final JTextField txtUsuario = new JTextField(25);
    private final JPasswordField txtClave = new JPasswordField(25);
    private final JButton btnProbar = new JButton("Probar conexión");
    private final JTextArea areaResultado = new JTextArea(8, 40);

    public ConexionTester() {
        super("Probador de conexión JDBC");
        armarVentana();
    }

    private void armarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 8, 8));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        panelCampos.add(new JLabel("URL de conexión:"));
        panelCampos.add(txtUrl);
        panelCampos.add(new JLabel("Usuario (vacío si es SQLite):"));
        panelCampos.add(txtUsuario);
        panelCampos.add(new JLabel("Contraseña (vacío si es SQLite):"));
        panelCampos.add(txtClave);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.add(btnProbar);

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.add(panelCampos, BorderLayout.CENTER);
        arriba.add(panelBoton, BorderLayout.SOUTH);

        areaResultado.setEditable(false);
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true);
        areaResultado.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        JScrollPane scroll = new JScrollPane(areaResultado);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultado de la prueba"));

        add(arriba, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        btnProbar.addActionListener(this::probarConexion);

        pack();
        setLocationRelativeTo(null);
    }

    private void probarConexion(ActionEvent e) {
        String url = txtUrl.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String clave = new String(txtClave.getPassword());

        if (url.isEmpty()) {
            mostrar("Fallo de conexión: la URL no puede estar vacía.", ROJO);
            return;
        }

        try {
            Class.forName(obtenerDriver(url));

            try (Connection conexion = DriverManager.getConnection(url, usuario, clave)) {
                mostrar("Conexión exitosa a: " + url, VERDE);
            }

        } catch (ClassNotFoundException ex) {
            mostrar("Fallo del driver: no se encontró la clase " + ex.getMessage()
                    + "\nRevisa que el .jar del driver esté agregado al proyecto.", ROJO);

        } catch (SQLException ex) {
            mostrar("Fallo de conexión.\nDetalle técnico: " + ex.getMessage(), ROJO);
        }
    }

    private String obtenerDriver(String url) {
        if (url.startsWith("jdbc:sqlite")) {
            return "org.sqlite.JDBC";
        } else if (url.startsWith("jdbc:mysql")) {
            return "com.mysql.cj.jdbc.Driver";
        } else if (url.startsWith("jdbc:postgresql")) {
            return "org.postgresql.Driver";
        }
        return "driver.desconocido";
    }

    private void mostrar(String mensaje, Color color) {
        areaResultado.setForeground(color);
        areaResultado.setText(mensaje);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConexionTester().setVisible(true));
    }
}
