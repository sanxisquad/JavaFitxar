package fitxar;
import fitxar.CrearUsuari;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import fitxar.DatabaseConnection;
import fitxar.DatabaseConnection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author marc
 */
public class GestioProfessors extends javax.swing.JFrame {
 
    private JButton btnCrear, btnEditar, btnEliminar;  // Botones para las acciones
    private JTextField txtBuscar;  // Campo de texto para la búsqueda
    private File archivoSeleccionado;
    private boolean teHorari = true;

    /**
     * Creates new form GestioProfessors
     */
    public GestioProfessors() {
        initComponents();
        setTitle("Gestió de Professors");
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        setLocationRelativeTo(null);

        // Crear los botones de acciónl
        JPanel panelBotones = new JPanel();
        btnCrear = new JButton("Afegir");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        txtBuscar = new JTextField(20);
        panelBotones.add(btnCrear);
        panelBotones.add(btnEditar);
        panelBotones.add(txtBuscar);
        panelBotones.add(btnEliminar);
        
        // Conectar los botones a las acciones
        btnCrear.addActionListener(e -> crearProfesor());
        btnEditar.addActionListener(e -> editarProfesor());
        btnEliminar.addActionListener(e -> eliminarProfesor());

        // Añadir los componentes al JFrame
        add(panelBotones, BorderLayout.NORTH);
                txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarTabla();
            }
        });

        // Cargar los datos al iniciar la ventana
        cargarProfessores();

        setVisible(true);
    }

    

public void cargarProfessores() {
    String query = "SELECT * FROM usuaris";  // Asegúrate de que 'usuaris' es el nombre correcto de la tabla
    try (Connection connection = DatabaseConnection.getConnection();
         Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        // Crear un modelo de tabla no editable
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "DNI", "Nom", "Cognoms", "Email", "Rol"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que todas las celdas no sean editables
            }
        };

        // Limpiamos la tabla antes de agregar nuevos datos (opcional porque estamos creando un nuevo modelo)
        taulaProfessors.setModel(model);

        // Rellenar el modelo con los datos de la base de datos
        while (rs.next()) {
            int id = rs.getInt("id_usuari");
            String dni = rs.getString("dni");
            String nombre = rs.getString("nom");
            String cognom = rs.getString("cognoms");
            String email = rs.getString("email");
            int idRol = rs.getInt("rol");
            teHorari = rs.getBoolean("teHorari");

            String rol = nomRol(idRol);

            Object[] fila = {id, dni, nombre, cognom, email, rol};
            model.addRow(fila);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al cargar los profesores: " + e.getMessage());
    }
}
    private void filtrarTabla() {
        String filtro = txtBuscar.getText().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) taulaProfessors.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        taulaProfessors.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + filtro));
    }


    // Método para crear un profesor (puedes abrir un formulario de creación)
    private void crearProfesor() {
    CrearUsuari frame = new CrearUsuari(this); // Pasar referencia al marco principal
    frame.setVisible(true);
    frame.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
            cargarProfessores(); // Recargar la tabla después de cerrar el formulario de creación
        }
    });
    }

    // Método para editar un profesor

   private void editarProfesor() {
    int selectedRow = taulaProfessors.getSelectedRow();
    if (selectedRow != -1) {
        try {
            // Obtener valores de la fila seleccionada
            int idProfesor = (int) taulaProfessors.getValueAt(selectedRow, 0);
            String dni = (String) taulaProfessors.getValueAt(selectedRow, 1);
            String nombre = (String) taulaProfessors.getValueAt(selectedRow, 2);
            String apellido = (String) taulaProfessors.getValueAt(selectedRow, 3);
            String email = (String) taulaProfessors.getValueAt(selectedRow, 4);
            String nombreRolActual = taulaProfessors.getValueAt(selectedRow, 5).toString(); // Nombre del rol actual
            String contrasenya = "";

            // Crear diálogo
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(taulaProfessors), "Editar - " + nombre, true);
            dialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Título del formulario
            JLabel tituloLabel = new JLabel("Editar - " + nombre);
            tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            dialog.add(tituloLabel, gbc);
            gbc.gridwidth = 1;

            // Campos
            JTextField txtDni = new JTextField(dni);
            JTextField txtNombre = new JTextField(nombre);
            JTextField txtApellido = new JTextField(apellido);
            JTextField txtEmail = new JTextField(email);
            JPasswordField txtContrasenya = new JPasswordField(contrasenya);
            JPasswordField txtRepetirContrasenya = new JPasswordField(contrasenya);

            // ComboBox para roles
            JComboBox<String> comboRoles = new JComboBox<>();
            Map<String, Integer> rolesMap = new HashMap<>();
            int idRolActual = -1; // ID del rol actual (se buscará más adelante)
            try (Connection connection = DatabaseConnection.getConnection();
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id_rol, nom_rol FROM rols_usuaris")) {
                while (rs.next()) {
                    int idRol = rs.getInt("id_rol");
                    String nomRol = rs.getString("nom_rol");
                    comboRoles.addItem(nomRol);
                    rolesMap.put(nomRol, idRol);
                    if (nomRol.equals(nombreRolActual)) { // Comparar con el nombre del rol actual
                        idRolActual = idRol;
                        comboRoles.setSelectedItem(nomRol);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error al cargar los roles: " + e.getMessage());
                return;
            }

            JLabel lblArchivo = new JLabel("Seleccionar archivo Excel:");
            JTextField txtArchivo = new JTextField(20);
            txtArchivo.setEditable(false);
            JButton btnSeleccionarArchivo = new JButton("Seleccionar");
            btnSeleccionarArchivo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fileChooser = new JFileChooser();
                    int result = fileChooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        archivoSeleccionado = fileChooser.getSelectedFile();
                        txtArchivo.setText(archivoSeleccionado.getAbsolutePath());
                    }
                }
            });

            JPanel panelArchivo = new JPanel();
            panelArchivo.add(lblArchivo);
            panelArchivo.add(txtArchivo);
            panelArchivo.add(btnSeleccionarArchivo);

            // Añadir componentes al diálogo
            gbc.gridx = 0; gbc.gridy = 1;
            dialog.add(new JLabel("DNI:"), gbc);
            gbc.gridx = 1;
            dialog.add(txtDni, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            dialog.add(new JLabel("Nom:"), gbc);
            gbc.gridx = 1;
            dialog.add(txtNombre, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            dialog.add(new JLabel("Cognoms:"), gbc);
            gbc.gridx = 1;
            dialog.add(txtApellido, gbc);

            gbc.gridx = 0; gbc.gridy = 4;
            dialog.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            dialog.add(txtEmail, gbc);

            gbc.gridx = 0; gbc.gridy = 5;
            dialog.add(new JLabel("Rol:"), gbc);
            gbc.gridx = 1;
            dialog.add(comboRoles, gbc);

            gbc.gridx = 0; gbc.gridy = 6;
            dialog.add(new JLabel("Contrasenya:"), gbc);
            gbc.gridx = 1;
            dialog.add(txtContrasenya, gbc);

            gbc.gridx = 0; gbc.gridy = 7;
            dialog.add(new JLabel("Repetir Contrasenya:"), gbc);
            gbc.gridx = 1;
            dialog.add(txtRepetirContrasenya, gbc);

            gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
            dialog.add(panelArchivo, gbc);

            // Botones
            JPanel panelBotones = new JPanel();
            JButton btnGuardar = new JButton("Guardar");
            JButton btnCancelar = new JButton("Cancelar");
            panelBotones.add(btnGuardar);
            panelBotones.add(btnCancelar);

            gbc.gridx = 1; gbc.gridy = 9; gbc.gridwidth = 2;
            dialog.add(panelBotones, gbc);

            // Acción de guardar
            btnGuardar.addActionListener(e -> {
                try {
                    String nuevoDni = txtDni.getText().trim();
                    String nuevoNombre = txtNombre.getText().trim();
                    String nuevoApellido = txtApellido.getText().trim();
                    String nuevoEmail = txtEmail.getText().trim();
                    String nuevaContrasenya = new String(txtContrasenya.getPassword());
                    String repetirContrasenya = new String(txtRepetirContrasenya.getPassword());

                    // Validar contraseñas
                    if (!nuevaContrasenya.equals(repetirContrasenya)) {
                        JOptionPane.showMessageDialog(dialog, "Les contrasenyes no coincideixen");
                        return;
                    }

                    // Validar unicidad (DNI, Email)
                    try (Connection connection = DatabaseConnection.getConnection()) {
                        String uniqueQuery = "SELECT COUNT(*) FROM usuaris WHERE (dni = ? OR email = ?) AND id_usuari != ?";
                        try (PreparedStatement uniqueStmt = connection.prepareStatement(uniqueQuery)) {
                            uniqueStmt.setString(1, nuevoDni);
                            uniqueStmt.setString(2, nuevoEmail);
                            uniqueStmt.setInt(3, idProfesor);

                            ResultSet rs = uniqueStmt.executeQuery();
                            if (rs.next() && rs.getInt(1) > 0) {
                                JOptionPane.showMessageDialog(dialog, "El DNI o el correo ya están en uso.");
                                return;
                            }
                        }

                        // Obtener ID del rol seleccionado
                        String rolSeleccionado = (String) comboRoles.getSelectedItem();
                        int nuevoIdRol = rolesMap.get(rolSeleccionado);

                        // Actualizar los datos en la base de datos
                        String updateQuery = "UPDATE usuaris SET dni = ?, nom = ?, cognoms = ?, email = ?, rol = ?, password = ? WHERE id_usuari = ?";
                        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
                            stmt.setString(1, nuevoDni);
                            stmt.setString(2, nuevoNombre);
                            stmt.setString(3, nuevoApellido);
                            stmt.setString(4, nuevoEmail);
                            stmt.setInt(5, nuevoIdRol); // Guardar la ID del rol
                            stmt.setString(6, nuevaContrasenya.isEmpty() ? contrasenya : nuevaContrasenya);
                            stmt.setInt(7, idProfesor);
                            stmt.executeUpdate();
                            JOptionPane.showMessageDialog(dialog, "Professor actualitzat correctament.");
                            cargarProfessores(); // Recargar la tabla después de la edición
                            dialog.dispose();
                        }

                        if (archivoSeleccionado != null) {
                            try {
                                ExcelProcessor excelProcessor = new ExcelProcessor();
                                List<Horario> horarios = excelProcessor.procesarArchivo(archivoSeleccionado);
                                guardarHorarios(idProfesor, horarios);
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(dialog, "Error al procesar el archivo Excel: " + ex.getMessage());
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, "Error al guardar los cambios: " + ex.getMessage());
                }
            });

            // Acción de cancelar
            btnCancelar.addActionListener(e -> dialog.dispose());

            // Configuración del diálogo
            dialog.setSize(700, 600);
            dialog.setLocationRelativeTo(taulaProfessors);
            dialog.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al abrir el formulario: " + ex.getMessage());
        }
    } else {
        JOptionPane.showMessageDialog(this, "Selecciona un profesor para editar.");
    }
}







    // Método para eliminar un profesor seleccionado
    private void eliminarProfesor() {
        int selectedRow = taulaProfessors.getSelectedRow();
        if (selectedRow != -1) {
            int idProfesor = (int) taulaProfessors.getValueAt(selectedRow, 0);  // Obtener el ID del profesor

            // Confirmar la eliminación
            int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar el profesor con ID: " + idProfesor + "?",
                    "Eliminar Profesor", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection connection = DatabaseConnection.getConnection()) {
                    String deleteQuery = "DELETE FROM usuaris WHERE id_usuari= ?";
                    try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
                        stmt.setInt(1, idProfesor);
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Profesor eliminado correctamente.");
                        cargarProfessores();  // Recargar la tabla después de la eliminación
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el profesor: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un profesor para eliminar.");
        }
    }

    private String nomRol(int idRol) {
        String nomRol = "";
        String query = "SELECT nom_rol FROM rols_usuaris WHERE id_rol = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idRol);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    nomRol = rs.getString("nom_rol");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener el nombre del rol: " + e.getMessage());
        }
        return nomRol;
    }
private void guardarHorarios(int usuariId, List<Horario> horarios) {
    // Consulta para verificar si ya existen horarios para este usuario
    String queryVerificarHorarios = "SELECT COUNT(*) FROM horarios_clase WHERE usuari_id = ?";
    String queryEliminarHorarios = "DELETE FROM horarios_clase WHERE usuari_id = ?";
    String queryInsertarHorarios = "INSERT INTO horarios_clase (usuari_id, dia, hora_inicio, hora_fin) VALUES (?, ?, ?, ?)";
    String queryActualizarTeHorari = "UPDATE usuaris SET teHorari = TRUE WHERE id = ?";

    try (Connection connection = DatabaseConnection.getConnection();  // Obtener la conexión
         PreparedStatement stmtVerificar = connection.prepareStatement(queryVerificarHorarios);
         PreparedStatement stmtEliminar = connection.prepareStatement(queryEliminarHorarios);
         PreparedStatement stmtInsertar = connection.prepareStatement(queryInsertarHorarios);
         PreparedStatement stmtUpdate = connection.prepareStatement(queryActualizarTeHorari)) {

        // Verificar si ya existen horarios para el usuario
        stmtVerificar.setInt(1, usuariId);
        ResultSet rs = stmtVerificar.executeQuery();
        rs.next();
        int horariosExistentes = rs.getInt(1);

        // Si ya existen horarios, preguntar si quiere borrarlos
        if (horariosExistentes > 0) {
            int option = JOptionPane.showConfirmDialog(null, 
                "S'ha trobat un horari registrat amb aquest usuari. Estàs segur de voler eliminar l'horari actual per afegir-ne un de nou?", 
                "Confirmar esborrat d'horari", JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                // Eliminar los horarios antiguos
                stmtEliminar.setInt(1, usuariId);
                stmtEliminar.executeUpdate();
                System.out.println("Horarios antiguos eliminados.");

                // Mostrar el mensaje con el nombre del usuario
                // Suponiendo que tienes una función para obtener el nombre completo del usuario
                String nombreUsuario = obtenerNombreUsuario(usuariId);
                JOptionPane.showMessageDialog(null, 
                    "Horari actual eliminat per al professor: " + nombreUsuario);
            } else {
                System.out.println("El usuario ha cancelado la operación.");
                return;  // Salir sin hacer nada
            }
        }

        // Insertar los nuevos horarios
        for (Horario horario : horarios) {
            // Asegúrate de que la hora de inicio y fin estén en formato correcto
            String horaInicio = formatearHora(horario.getHoraInicio().split(" - ")[0]);  // Hora de inicio
            String horaFin = formatearHora(horario.getHoraInicio().split(" - ")[1]);  // Hora de fin
            
            // Establecer los valores en el prepared statement para los horarios
            stmtInsertar.setInt(1, usuariId);    // ID del profesor
            stmtInsertar.setString(2, horario.getDia()); // Día de la semana
            stmtInsertar.setString(3, horaInicio); // Hora de inicio en formato "HH:mm"
            stmtInsertar.setString(4, horaFin); // Hora de fin en formato "HH:mm"
            stmtInsertar.addBatch();  // Agregar al batch
        }

        // Ejecutar el batch de inserción de horarios
        stmtInsertar.executeBatch();
        System.out.println("Horarios guardados correctamente en la base de datos.");

        // Ahora actualizamos el campo teHorari a true en la tabla usuaris
        stmtUpdate.setInt(1, usuariId);  // Establecemos el ID del usuario
        int filasActualizadas = stmtUpdate.executeUpdate();  // Ejecutar el UPDATE

        if (filasActualizadas > 0) {
            System.out.println("El campo teHorari se ha actualizado a true para el usuario con ID: " + usuariId);
        } else {
            System.err.println("No se pudo actualizar el campo teHorari.");
        }

    } catch (SQLException e) {
        System.err.println("Error al guardar los horarios o actualizar el campo teHorari: " + e.getMessage());
    }
}

// Método para obtener el nombre completo del usuario
private String obtenerNombreUsuario(int usuariId) {
    String query = "SELECT nombre, apellido FROM usuaris WHERE id = ?";
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement stmt = connection.prepareStatement(query)) {
        
        stmt.setInt(1, usuariId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String nombre = rs.getString("nombre");
            String apellido = rs.getString("apellido");
            return nombre + " " + apellido;
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener el nombre del usuario: " + e.getMessage());
    }
    return "Usuario no encontrado";
}



    private String formatearHora(String hora) {
        // Validar si la hora está en formato correcto "HH:mm"
        String[] partes = hora.split(":");
        if (partes.length == 2) {
            String horaParte = partes[0];
            String minutosParte = partes[1];

            // Asegurarnos de que ambos valores tengan dos dígitos (ej. "09" en lugar de "9")
            if (horaParte.length() == 1) {
                horaParte = "0" + horaParte;
            }
            if (minutosParte.length() == 1) {
                minutosParte = "0" + minutosParte;
            }

            return horaParte + ":" + minutosParte;
        } else {
            throw new IllegalArgumentException("Formato de hora inválido: " + hora);
        }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        taulaProfessors = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        taulaProfessors.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "DNI", "Nom", "Cognoms", "Email", "Rol"
            }
        ));
        jScrollPane1.setViewportView(taulaProfessors);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1369, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 636, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GestioProfessors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GestioProfessors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GestioProfessors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GestioProfessors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GestioProfessors().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable taulaProfessors;
    // End of variables declaration//GEN-END:variables
}
