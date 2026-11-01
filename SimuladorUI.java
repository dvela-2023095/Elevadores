import elevator.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class SimuladorUI extends JFrame {

    private static final Color BACKGROUND_COLOR=new Color(43,45,48);
    private static final Color PANEL_COLOR=new Color(60,63,65);
    private static final Color TEXT_COLOR=new Color(230,230,230);
    private static final Color BUTTON_COLOR=new Color(76,80,82);
    private static final Color ACCENT_BLUE=new Color(73,156,192);
    private static final Color ACCENT_ORANGE=new Color(214,134,48);
    private static final Color ACCENT_RED=new Color(192,73,73);

    private JTextField cantidadElevadoresField;
    private JTextField totalPisosField;
    private JTextField tiempoMovimientoField;
    private JTextField tiempoEsperaField;
    private JTextField pisoOrigenField;
    private JComboBox<String> direccionComboBox;

    private ElevatorManager manager;
    private Timer timerAnimacion;
    private PanelEdificio panelEdificio;



    public SimuladorUI() {
        ventanaPrincipal();
        initComponentes();
        setVisible(true);
    }

    private void ventanaPrincipal() {
        setTitle("Simulador de Elevadores");
        setSize(1200,800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15,15));
        getContentPane().setBackground(BACKGROUND_COLOR);
        getRootPane().setBorder(new EmptyBorder(15,15,15,15));
    }




    private void initComponentes() {
        add(panelControl(),BorderLayout.WEST);
        add(mainPage(),BorderLayout.CENTER);
    }

    private JPanel mainPage() {
        JPanel mainWorkingArea=new JPanel(new BorderLayout(10,10));
        mainWorkingArea.setOpaque(false);
        
        panelEdificio=new PanelEdificio(null,10,3);
        panelEdificio.setBorder(estiloLayout("EDIFICIO Y ELEVADORES"));
        
        Animaciones();

        mainWorkingArea.add(panelManager(),BorderLayout.NORTH);
        mainWorkingArea.add(panelEdificio,BorderLayout.CENTER);
        
        return mainWorkingArea;
    }


    private void Animaciones() {
        timerAnimacion=new Timer(50,e->panelEdificio.repaint());
    }



    private JPanel panelControl() {
        JPanel controlPanel=new JPanel(new BorderLayout(0,20));
        controlPanel.setBackground(PANEL_COLOR);
        controlPanel.setBorder(estiloLayout("PANEL DE CONTROL"));
        controlPanel.setPreferredSize(new Dimension(300,0));

        JPanel configPanel=panelConfiguracion();
        JPanel buttonsPanel=botones();

        controlPanel.add(configPanel,BorderLayout.NORTH);
        controlPanel.add(buttonsPanel,BorderLayout.CENTER);

        return controlPanel;
    }
    
    private JPanel panelConfiguracion() {
        JPanel configPanel=new JPanel(new GridLayout(4,2,10,15));
        configPanel.setOpaque(false);
        configPanel.setBorder(new EmptyBorder(15,15,15,15));

        cantidadElevadoresField=new JTextField("3");
        totalPisosField=new JTextField("10");
        tiempoMovimientoField=new JTextField("1500");
        tiempoEsperaField=new JTextField("3000");

        agregarLabel(configPanel,"Cantidad de Elevadores:",cantidadElevadoresField);
        agregarLabel(configPanel,"Total de Pisos:",totalPisosField);
        agregarLabel(configPanel,"Tiempo Movimiento (ms):",tiempoMovimientoField);
        agregarLabel(configPanel,"Tiempo Espera (ms):",tiempoEsperaField);

        return configPanel;
    }




    private void agregarLabel(JPanel container,String labelText,JTextField textField) {
        JLabel label=new JLabel(labelText);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Segoe UI",Font.BOLD,12));
        estiloTexto(textField);
        container.add(label);
        container.add(textField);
    }


    private JPanel botones() {
        JPanel buttonsPanel=new JPanel(new GridLayout(4,1,0,15));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(20,15,20,15));

        JButton startButton=estiloBoton("INICIAR SIMULACIÓN",ACCENT_BLUE);

        startButton.addActionListener(e->{
            try {
                int elevadores=Integer.parseInt(cantidadElevadoresField.getText());
                int pisos=Integer.parseInt(totalPisosField.getText());
                int movi=Integer.parseInt(tiempoMovimientoField.getText());
                int espe=Integer.parseInt(tiempoEsperaField.getText());

                manager=new ElevatorManager(elevadores,pisos,movi,espe);
                new Thread(manager).start();
                
                panelEdificio.actualizarConfiguracion(manager,pisos,elevadores);
                timerAnimacion.start();
                
                System.out.println("Simulación iniciada con "+elevadores+" elevadores");
            } catch (Exception ex) {
                System.out.println(ex);
            }
        });

        JButton stopButton=estiloBoton("DETENER",ACCENT_ORANGE);
        stopButton.addActionListener(e->{
            if(manager!=null) {
                manager.detenerElevadores();
                timerAnimacion.stop();
            }else{
                System.out.println("NO HAY ELEVADORES QUE PUEDA DETENER");
            }
        });

        JButton resetButton=estiloBoton("RESET",ACCENT_RED);
        resetButton.addActionListener(e->{
            if(manager!=null) {
                manager.resetear();
                System.out.println("Sistema reiniciado: Elevadores volviendo a sus pisos de origen");
            }else{
                System.out.println("No hay elevadores compadre");
            }
        });

        JButton loadButton=estiloBoton("CARGAR ARCHIVO DE REQUERIMIENTOS",BUTTON_COLOR);

        buttonsPanel.add(startButton);
        buttonsPanel.add(stopButton);
        buttonsPanel.add(resetButton);
        buttonsPanel.add(loadButton);

        return buttonsPanel;
    }




    private JPanel panelManager() {
        JPanel contenedorInteraccion=new JPanel(new GridLayout(2,1,0,10));
        contenedorInteraccion.setOpaque(false);

        JPanel panelLlamadasExternas=new JPanel(new FlowLayout(FlowLayout.CENTER,25,10));
        panelLlamadasExternas.setBackground(PANEL_COLOR);
        panelLlamadasExternas.setBorder(estiloLayout("BOTONES DEL PASILLO (LLAMADA EXTERNA)"));

        pisoOrigenField=new JTextField(5);
        estiloTexto(pisoOrigenField);

        direccionComboBox=new JComboBox<>(new String[]{"hacia arriba","hacia abajo"});
        direccionComboBox.setFont(new Font("Segoe UI",Font.BOLD,14));
        direccionComboBox.setBackground(Color.WHITE);
        direccionComboBox.setForeground(Color.BLACK);

        JButton botonLlamarElevador=estiloBoton("LLAMAR ELEVADOR",BUTTON_COLOR);
        botonLlamarElevador.addActionListener(e->{
            if(manager!=null) {
                try {
                    int piso=Integer.parseInt(pisoOrigenField.getText());
                    String direccion=(String)direccionComboBox.getSelectedItem();
                    manager.solicitarElevador(piso,direccion);
                    System.out.println("Petición Externa: Alguien en piso "+piso+" quiere ir "+direccion);
                } catch (Exception ex) {
                    System.out.println("Error en entrada de datos: "+ex.getMessage());
                }
            } else {
                System.out.println("Inicia la simulación primero compadre.");
            }
        });
        panelLlamadasExternas.add(etiquetas("PISO ORIGEN:"));
        panelLlamadasExternas.add(pisoOrigenField);
        panelLlamadasExternas.add(etiquetas("DIRECCIÓN:"));
        panelLlamadasExternas.add(direccionComboBox);
        panelLlamadasExternas.add(botonLlamarElevador);

        JPanel panelLlamadasInternas=new JPanel(new FlowLayout(FlowLayout.CENTER,25,10));
        panelLlamadasInternas.setBackground(PANEL_COLOR);
        panelLlamadasInternas.setBorder(estiloLayout("BOTONES DENTRO DE LA CABINA (PETICIÓN INTERNA)"));
        JTextField idElevadorField=new JTextField(5);
        estiloTexto(idElevadorField);
        JTextField pisoDestinoField=new JTextField(5);
        estiloTexto(pisoDestinoField);
        JButton botonMarcarPiso=estiloBoton("MARCAR PISO",BUTTON_COLOR);
        botonMarcarPiso.addActionListener(e->{
            if(manager!=null) {
                try {
                    int idElevador=Integer.parseInt(idElevadorField.getText());
                    int pisoDestino=Integer.parseInt(pisoDestinoField.getText());
                    manager.peticionInterna(idElevador,pisoDestino);
                    System.out.println("Petición Interna: Pasajero en Elevador "+idElevador+" marcó el piso "+pisoDestino);
                } catch (Exception ex) {
                    System.out.println("Error en entrada de datos: "+ex.getMessage());
                }
            } else {
                System.out.println("Inicia la simulación primero compadre.");
            }
        });

        panelLlamadasInternas.add(etiquetas("ID DEL ELEVADOR:"));
        panelLlamadasInternas.add(idElevadorField);
        panelLlamadasInternas.add(etiquetas("IR AL PISO:"));
        panelLlamadasInternas.add(pisoDestinoField);
        panelLlamadasInternas.add(botonMarcarPiso);
        contenedorInteraccion.add(panelLlamadasExternas);
        contenedorInteraccion.add(panelLlamadasInternas);

        return contenedorInteraccion;
    }

    private JLabel etiquetas(String text) {
        JLabel label=new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Segoe UI",Font.BOLD,12));
        return label;
    }



    private void estiloTexto(JTextField textField) {
        textField.setFont(new Font("Segoe UI",Font.PLAIN,14));
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));
    }


    private JButton estiloBoton(String text,Color backgroundColor) {
        JButton button=new JButton(text);
        button.setFont(new Font("Segoe UI",Font.BOLD,13));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(),1),
                new EmptyBorder(12,20,12,20)
        ));
        return button;
    }

    private TitledBorder estiloLayout(String title) {
        TitledBorder border=BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY,1),
                title
        );
        border.setTitleColor(TEXT_COLOR);
        border.setTitleFont(new Font("Segoe UI",Font.BOLD,12));
        return border;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            new SimuladorUI();
        });
    }




    class PanelEdificio extends JPanel {

        private ElevatorManager manager;
        private int totalPisos;
        private int cantidadElevadores;
    
        private final Color COLOR_FONDO=new Color(43,45,48);
        private final Color COLOR_LINEAS=new Color(76,80,82);
        private final Color COLOR_SUBIENDO=new Color(73,192,100);
        private final Color COLOR_BAJANDO=new Color(214,134,48);
        private final Color COLOR_INACTIVO=new Color(73,156,192);
        private final Color COLOR_BORDE_CABINA=new Color(230,230,230);
    
        public PanelEdificio(ElevatorManager manager,int totalPisos,int cantidadElevadores) {
            this.manager=manager;
            this.totalPisos=totalPisos;
            this.cantidadElevadores=cantidadElevadores;
            setBackground(COLOR_FONDO);
        }
        
        public void actualizarConfiguracion(ElevatorManager manager,int totalPisos,int cantidadElevadores) {
            this.manager=manager;
            this.totalPisos=totalPisos;
            this.cantidadElevadores=cantidadElevadores;
            repaint();
        }
    

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d=graficos(g);
    
            int anchoPanel=getWidth();
            int altoPanel=getHeight();
            int anchoColumna=gridAncho(anchoPanel);
            int altoFila=gridAlto(altoPanel);
    
            generarTablero(g2d,anchoPanel,altoPanel,anchoColumna,altoFila);
            generarCabina(g2d,anchoColumna,altoFila,altoPanel);
        }
    
        private Graphics2D graficos(Graphics g) {
            Graphics2D g2d=(Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            return g2d;
        }
    


        private int gridAncho(int anchoPanel) {
            return cantidadElevadores>0 ? anchoPanel/cantidadElevadores : anchoPanel;
        }
    
        private int gridAlto(int altoPanel) {
            return totalPisos>0 ? altoPanel/totalPisos : altoPanel;
        }
    


        private void generarTablero(Graphics2D g2d,int anchoPanel,int altoPanel,int anchoColumna,int altoFila) {
            g2d.setColor(COLOR_LINEAS);
            g2d.setStroke(new BasicStroke(1.5f));
    
            for(int i=0;i<=cantidadElevadores;i++) {
                int posicionX=i*anchoColumna;
                g2d.drawLine(posicionX,0,posicionX,altoPanel);
            }
    
            for(int i=0;i<=totalPisos;i++) {
                int posicionY=i*altoFila;
                g2d.drawLine(0,posicionY,anchoPanel,posicionY);
            }
        }
    
        private void generarCabina(Graphics2D g2d,int anchoColumna,int altoFila,int altoPanel) {
            if(manager==null||manager.getElevadores()==null) {
                return;
            }
    
            int indiceColumna=0;
            for(Elevator elevador:manager.getElevadores()) {
                dibujarUnaCabina(g2d,elevador,indiceColumna,anchoColumna,altoFila,altoPanel);
                indiceColumna++;
            }
        }
    
        private void dibujarUnaCabina(Graphics2D g2d,Elevator elevador,int indiceColumna,int anchoColumna,int altoFila,int altoPanel) {
            int pisoActual=elevador.getPisoActual();
            String estadoActual=elevador.getDireccion();
    
            int anchoCabina=(int)(anchoColumna*0.6);
            int altoCabina=(int)(altoFila*0.8);
            int margenX=(anchoColumna-anchoCabina)/2;
            int margenY=(altoFila-altoCabina)/2;
    
            int posicionX=(indiceColumna*anchoColumna)+margenX;
            int posicionY=altoPanel-(pisoActual*altoFila)+margenY;
    
            Color colorCabina=colorCabina(estadoActual);
            
            g2d.setColor(colorCabina);
            g2d.fillRoundRect(posicionX,posicionY,anchoCabina,altoCabina,12,12);
    
            g2d.setColor(COLOR_BORDE_CABINA);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawRoundRect(posicionX,posicionY,anchoCabina,altoCabina,12,12);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI",Font.BOLD,12));
            g2d.drawString("Piso: "+pisoActual,posicionX+10,posicionY+20);
            List<Integer> cola=elevador.getColaComandos();
            String textCola=cola.isEmpty() ? "Cola:[]":"Cola: "+cola.toString();
            g2d.drawString(textCola,posicionX+10,posicionY+40);
        }
    

        private Color colorCabina(String estado) {
            if("hacia arriba".equalsIgnoreCase(estado)) {
                return COLOR_SUBIENDO;
            }else if("hacia abajo".equalsIgnoreCase(estado)) {
                return COLOR_BAJANDO;
            }else{
                return COLOR_INACTIVO;
            }
        }
    }
}