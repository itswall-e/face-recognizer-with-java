/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.opencv.core.Mat;

/**
 *
 * @author Paulo Andrade
 */
public class ImageViewer
{
    JFrame v;
    JPanel mainPanel;
    JLabel lblImg;
    String title;
    int width, height;
    
    /**
     * Constructor
     * 
     * @param title Titulo para la ventana
     */
    public ImageViewer(String title)
    {
        this.title = title; // Titulo del JFrame
        width = 660; // Ancho del JFrame
        height = 500; // Alto del JFrame
        
        // Inicializamos los componentes
        initComponents();
    }
    
    /**
     * Inicializa los componentes del JFrame
     */
    private void initComponents()
    {
        v = new JFrame(title);
        v.setSize(width + 20, height + 20);
        v.setResizable(false);
        v.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        v.setLayout(null);
        
        // Componentes
        mainPanel = new JPanel();
        mainPanel.setBounds(10, 10, width, 480);
        mainPanel.setBackground(Color.WHITE);
        
        lblImg = new JLabel();
        lblImg.setBounds(0, 0, 640, 480);
        
        // Añadimos componentes
        v.add(mainPanel);
        mainPanel.add(lblImg);
        
        // Eventos
        
        // Mostramos JFrame
        v.setVisible(true);
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
        // Convertimos la matriz en imagen y la mostramos en pantalla
        lblImg.setIcon(new ImageIcon(ImageProcessor.toBufferedImage(m)));
    }
}
