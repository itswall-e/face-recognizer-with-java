/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

import opencv.detector.FaceDetector;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.opencv.core.Mat;

/**
 *
 * @author Paulo Andrade
 */
public class ImageViewer extends JFrame implements ActionListener
{
    FaceDetector face;
    JPanel mainPanel, camPanel, optionPanel;
    JLabel lblImg;
    JButton btnStop, btnStart, btnClose, btnTrain, btnProcess;
    String title;
    JTextField txtName;
    private int width, height;
    private boolean ctrCam, ctrWindow;
    private boolean ctr;
    
    /**
     * Constructor
     * 
     * @param title Titulo para la ventana
     */
    public ImageViewer(String title)
    {
        this.title = title; // Titulo del JFrame
        width = 800; // Ancho del JFrame (default 660)
        height = 600; // Alto del JFrame (default 500)
        ctrCam = false; // Control para activar/deactivar la camara
        ctrWindow = false; // Control para cerrar la ventana
        ctr = false; // Control del ciclo de entrenamiento
        
        // Inicializamos el detector de rostros
        face = new FaceDetector();
        
        // Inicializamos los componentes
        initComponents();
    }
    
    /**
     * Metodo getter ctrCam
     * 
     * @return Devuelve el estado de la camara
     */
    public boolean getCtrCam()
    {
        return ctrCam;
    }
    
    /**
     * Metodo getter ctrWindow
     * 
     * @return Devuelve el estado de la ventana
     */
    public boolean getCtrWindow()
    {
        return ctrWindow;
    }
    
    /**
     * Inicializa los componentes del JFrame
     */
    private void initComponents()
    {
        setTitle(title);
        setSize(width, height + 20);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        
        // Componentes
        mainPanel = new JPanel();
        mainPanel.setBounds(0, 0, width, height);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null);
        
        lblImg = new JLabel();
        lblImg.setBounds(10, 100, 640, 480);
        
        camPanel = new JPanel();
        camPanel.setBounds(10, 10, 640, 80);
        camPanel.setBackground(Color.WHITE);
        camPanel.setBorder(BorderFactory.createTitledBorder("Control de camara"));
        
        btnStop = new JButton("Stop");
        btnStop.setBounds(10, 20, 150, 20);
        btnStop.setEnabled(false);
        
        btnStart = new JButton("Start");
        btnStart.setBounds(170, 20, 150, 20);
        
        btnClose = new JButton("Close");
        btnClose.setBounds(330, 20, 150, 20);
        
        optionPanel = new JPanel();
        optionPanel.setBounds(650, 10, 140, 580);
        optionPanel.setBackground(Color.WHITE);
        optionPanel.setBorder(BorderFactory.createTitledBorder("Opciones"));
        optionPanel.setLayout(null);
        
        btnTrain = new JButton("Training");
        btnTrain.setBounds(10, 20, 120, 20);
        btnTrain.setEnabled(false);
        
        txtName = new JTextField();
        txtName.setBounds(10, 50, 120, 20);
        txtName.setVisible(false);
        
        btnProcess = new JButton("Retrain");
        btnProcess.setBounds(10, 80, 120, 20);
        btnProcess.setVisible(false);
        
        // Añadimos componentes
        add(mainPanel);
        mainPanel.add(lblImg);
        mainPanel.add(camPanel);
        camPanel.add(btnStop);
        camPanel.add(btnStart);
        camPanel.add(btnClose);
        mainPanel.add(optionPanel);
        optionPanel.add(btnTrain);
        optionPanel.add(txtName);
        optionPanel.add(btnProcess);
        
        // Eventos
        btnStop.addActionListener(this);
        btnStart.addActionListener(this);
        btnClose.addActionListener(this);
        btnTrain.addActionListener(this);
        
        // Mostramos JFrame
        setVisible(true);
    }
    
    /**
     * Redimenciona el ancho y alto del JFrame
     * 
     * @param width Ancho del JFrame
     * @param height Alto del JFrame
     */
    public void set(int width, int height)
    {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Muestra un frame en pantalla reconociendo las caras
     * 
     * @param m matriz a mostrar en pantalla
     */
    public void show(Mat m)
    {   
        // aplicamos el detector
        m = face.FaceDetect(m, ctr);
        
        // verificamos si es necesario ingresar un nombre
        if(face.getCount() == 5){
            // Mostramos la caja de texto
            txtName.setVisible(true);
            btnProcess.setVisible(true);
            // Desactivamos los botones
            btnClose.setEnabled(false);
            btnStop.setEnabled(false);
        }
        
            
        // Convertimos la matriz en imagen
        Image img = ImageProcessor.toBufferedImage(m);
        
        // Mostramos la imagen
        lblImg.setIcon(new ImageIcon(img));
    }
    
    /**
     * Cerramos la ventana
     */
    public void close()
    {
        // Destruimos la ventana
        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Verificamos el boton pulsado
        if(e.getSource().equals(btnStop)){
            // Desactivamos el boton
            btnStop.setEnabled(false);
            btnStart.setEnabled(true);
            btnClose.setEnabled(true);
            btnTrain.setEnabled(false);
            
            // verificamos el control
            if(ctr){
                ctr = false;
            }
            
            // Actualizamos el control
            ctrCam = !ctrCam;
        } else if(e.getSource().equals(btnStart)){
            // Desactivamos el boton
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            btnClose.setEnabled(false);
            btnTrain.setEnabled(true);
            
            // Actualizamos el control
            ctrCam = !ctrCam;
        } else if(e.getSource().equals(btnClose)){
            ctrWindow = !ctrWindow;
        } else if(e.getSource().equals(btnTrain)){
            // Obtenemos el control del boton
            ctr = !ctr;
            
            // Asignamos control al boton
            btnTrain.setEnabled(false);
        } else if(e.getSource().equals(btnProcess)){
            // Obtenemos el texto
            String name = txtName.getText();
            
            // verificamos el tamaño
            if(name.length() > 4){
                // Actualizamos contador
                face.setCount(6);
                // Bloqueamos boton
                btnProcess.setEnabled(false);
                // Comenzamos proceso de entrenamiento
                if(face.process(name)){
                    // Restablecemos los procesos
                    btnProcess.setEnabled(true);
                    btnProcess.setVisible(false);
                    txtName.setText("");
                    txtName.setVisible(false);
                    btnTrain.setEnabled(true);
                    ctr = false;
                    face.setCount(0);
                    btnClose.setEnabled(true);
                    btnStop.setEnabled(true);
                }
            }
        }
    }
}
