//Simulador de elevadores en Java Swing 
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class ConsolaDeComandos {
    public static void main(String[] args) {
        //Ventana principal
        JFrame ventana = new JFrame("Elevadores - Consola de comandos");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(800, 600);

        //Paleta de colores
        Color fondo1 = new Color(47, 61, 71);



        //Menú
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Menú");
        JMenuItem nuevoElevador = new JMenuItem("Agregar Elevador");
        JMenuItem salir = new JMenuItem("Salir");
        fileMenu.add(nuevoElevador);
        fileMenu.addSeparator();
        fileMenu.add(salir);
        menuBar.add(fileMenu);

        //Controles generales
        JPanel panel = new JPanel();
        panel.setBackground(fondo1);
        JButton button = new JButton("Iniciar simulación");
        panel.add(button);

        //Acciones de los botones
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.setSize(1200, 600);
                JOptionPane.showMessageDialog(ventana, "Inicia la simulación");
            }
        });

        //Panel de la bitácora
        JPanel textPanel = new JPanel();
        JLabel label = new JLabel("Bitácora");
        JTextArea bitacora = new JTextArea("Inicio de la bitácora de simulación \n\n");
        //JScrollPane bitacoraScroll=new JScrollPane(bitacora);
        bitacora.setBounds(50,50,150,150);
        label.setForeground(Color.white);
        textPanel.setPreferredSize(new Dimension(150, 150));
        Border border = new LineBorder(new Color(3, 144, 252), 1, true);
        textPanel.setBorder(border);
        textPanel.setBackground(fondo1);
        textPanel.add(label);
        textPanel.add(bitacora);
        

        // Estilo de la ventana
        ventana.setLayout(new BorderLayout());
        
        ventana.setJMenuBar(menuBar);
        ventana.add(panel, BorderLayout.WEST);
        ventana.add(textPanel, BorderLayout.SOUTH);

        ventana.setVisible(true);
    }
}